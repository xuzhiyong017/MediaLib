package com.sky.medialib.ui.kit.download;


import com.sky.media.image.core.util.LogUtils;
import com.sky.medialib.util.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DownFileTask extends AbsDownAsyncTask<DownFileParams, Object, DownFileResult> {

    protected static final int BYTE_SIZE = 8192;
    public static final int CODE_FAIL_KEY = -1;
    public static final int CODE_SUCCESS_KEY = 0;
    private static int HTTP_STATE_SC_OK = 200;
    private static int HTTP_STATE_SC_PARTIAL_CONTENT = 206;
    private static int HTTP_STATE_SC_REQUESTED_RANGE_NOT_SATISFIABLE = 416;
    protected static final int MAX_RE_DOWNLOAD_TIMES = 3;

    protected ArrayList<IDownFileProgressListener> mListeners;
    protected IDownFileFinishListener mIDownFileFinishListener;
    protected DownFileResult mDownFileResult;
    protected String downUrl;
    protected String mFilePath;
    protected long totalSize;
    protected long tempSize;
    protected int retryCount;
    protected OkHttpClient okHttpClient;

    public DownFileTask(String downUrl, String storePath) {
        this(downUrl, storePath, null, 10);
    }

    public DownFileTask(String downUrl, String storePath, OkHttpClient okHttpClient, int threadPriority) {
        super(threadPriority);
        this.mListeners = new ArrayList();
        this.mDownFileResult = new DownFileResult(-1, this, null);
        this.totalSize = 0;
        this.tempSize = 0;
        this.retryCount = 0;
        this.downUrl = downUrl;
        this.mFilePath = storePath;
        if (okHttpClient == null) {
            this.okHttpClient = newOkHttpClient();
        } else {
            this.okHttpClient = okHttpClient;
        }
    }

    private OkHttpClient newOkHttpClient() {
        return new OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).retryOnConnectionFailure(true).followRedirects(true).build();
    }

    public String getDownUrl() {
        return this.downUrl;
    }

    protected boolean startDownFile(String str, String filePath) {
        boolean result = true;
        LogUtils.logd("DownFileTask", "下载地址：" + str);
        this.retryCount++;
        if (this.retryCount >= 3) {
            return false;
        }
        InputStream inputStream = null;
        try {
             LogUtils.logd("DownFileTask", "文件地址：" + filePath);
            File storeFile = new File(filePath);
            if (storeFile.exists() && storeFile.isFile()) {
                this.tempSize = storeFile.length();
                 LogUtils.logd("DownFileTask", "已经下载的文件大小：" + this.tempSize);
            } else {
                this.tempSize = 0;
                 LogUtils.logd("DownFileTask", "未下载过，开始下载");
                File parentFile = storeFile.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                storeFile.createNewFile();
            }
            Response execute = this.okHttpClient.newCall(new Request.Builder().url(str).header("RANGE", "bytes=" + this.tempSize + "-").build()).execute();
            if (execute.isSuccessful()) {
                ResponseBody g = execute.body();
                if (g != null) {
                    this.totalSize = g.contentLength();
                     LogUtils.logd("DownFileTask", "文件总大小:" + this.totalSize);
                    if (this.tempSize > this.totalSize) {
                         LogUtils.logd("DownFileTask", "临时文件大小比文件总大小还大，删除重新下载");
                        storeFile.delete();
                        result = startDownFile(str, filePath);
                    } else if (this.tempSize != this.totalSize || this.totalSize == 0) {
                        inputStream = g.byteStream();
                        result = readAndWriteFile(inputStream, filePath);
                    } else {
                         LogUtils.logd("DownFileTask", "临时文件大小与文件总大小相等，不重复下载");
                        publishProgress(Long.valueOf(this.tempSize), Long.valueOf(this.totalSize));
                        if (this.mIDownFileFinishListener != null) {
                            this.mDownFileResult.setCode(CODE_SUCCESS_KEY);
                            this.mIDownFileFinishListener.onDownLoadFinish(this.mDownFileResult);
                        }
                    }
                } else {
                     LogUtils.logd("DownFileTask", "未获取到文件流");
                    result = false;
                }
            } else {
                result = false;
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                     LogUtils.loge("DownFileTask", e.getMessage());
                    result = false;
                }
            }
        } catch (Exception e2) {
             LogUtils.loge("DownFileTask", e2.getMessage());
            if (inputStream != null) {
                try {
                    inputStream.close();
                    result = false;
                } catch (Exception e22) {
                     LogUtils.loge("DownFileTask", e22.getMessage());
                    result = false;
                }
            } else {
                result = false;
            }
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e3) {
                     LogUtils.loge("DownFileTask", e3.getMessage());
                }
            }
        }
        return result;
    }

    protected boolean readAndWriteFile(InputStream inputStream, String str) {
        boolean result = true;
        FileOutputStream outputStream = null;
        try {
            byte[] bArr = new byte[BYTE_SIZE];
            Arrays.fill(bArr, (byte) 0);
            outputStream = new FileOutputStream(str, true);
            while (true) {

                int read = inputStream.read(bArr, 0, bArr.length);
                if (read == -1 || isCancelled()) {
                    break;
                } else if (!FileUtil.INSTANCE.isStorageWriteable()) {
                    result = false;
                    break;
                } else {
                    outputStream.write(bArr, 0, read);
                    outputStream.flush();
                    this.tempSize += (long) read;
                    if (this.totalSize < this.tempSize) {
                        this.totalSize = this.tempSize;
                    }
                    publishProgress(Long.valueOf(this.tempSize), Long.valueOf(this.totalSize));
                }
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(outputStream != null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


    public void notifyResult() {
        boolean result = false;
        while (!isCancelled() && this.retryCount < MAX_RE_DOWNLOAD_TIMES) {
            result = startDownFile(this.downUrl, this.mFilePath);
            if (result) {
                 LogUtils.logd("DownFileTask", this.downUrl + " 下载成功");
                break;
            }
             LogUtils.logd("DownFileTask", this.downUrl + " 下载失败:" + this.retryCount);
        }
        if (result) {
            this.mDownFileResult.setCode(CODE_SUCCESS_KEY);
        } else {
            this.mDownFileResult.setCode(CODE_FAIL_KEY);
        }
    }

    protected DownFileResult doInBackground(DownFileParams... downFileParamsArr) {
        notifyResult();
        return this.mDownFileResult;
    }

    protected void onProgressUpdate(Object... objArr) {
        Iterator it = this.mListeners.iterator();
        while (it.hasNext()) {
            IDownFileProgressListener iDownFileProgressListener = (IDownFileProgressListener) it.next();
            if (iDownFileProgressListener != null) {
                iDownFileProgressListener.onProgress(this);
            }
        }
    }

    protected void onPostExecute(DownFileResult downFileResult) {
        super.onPostExecute(downFileResult);
        if (this.mIDownFileFinishListener != null) {
            this.mIDownFileFinishListener.onDownLoadFinish(downFileResult);
        }
    }

    protected void onCancelled() {
        this.mIDownFileFinishListener = null;
        super.onCancelled();
    }

    public String getDownLoadFilePath() {
        return this.mFilePath;
    }

    public long getTempFileSize() {
        return this.tempSize;
    }

    public long getFileSize() {
        return this.totalSize;
    }

    public int getProgress() {
        if (this.totalSize > 0) {
            return Math.round((((float) this.tempSize) * 100.0f) / ((float) this.totalSize));
        }
        return 0;
    }
    public void setOnDownFileFinishListener(IDownFileFinishListener iDownFileFinishListener) {
        this.mIDownFileFinishListener = iDownFileFinishListener;
    }
    public void addDownFileProgressListener(IDownFileProgressListener iDownFileProgressListener) {
        if (!this.mListeners.contains(iDownFileProgressListener)) {
            this.mListeners.add(iDownFileProgressListener);
        }
    }
}
