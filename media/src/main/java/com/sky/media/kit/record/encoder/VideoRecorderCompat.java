package com.sky.media.kit.record.encoder;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.sky.media.image.core.util.LogUtils;
import com.sky.media.kit.mediakit.MediaKitCompat;
import com.sky.media.kit.record.IVideoRecorder;
import com.sky.media.kit.record.RecordListener;
import com.sky.media.kit.util.Util;
import com.weibo.soundtouch.SoundTouch;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class VideoRecorderCompat implements IVideoRecorder {

    private Context mContext;
    private int width;
    private int height;
    private String mVideoTemPath;
    private BlockingQueue<EncodeData> mEncodeQueue = new LinkedBlockingQueue(256);
    private VideoRecorderThread mVideoRecorderThread;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private MediaMuxerWrapper mMediaMuxerWrapper;
    private MediaVideoEncoder mMediaVideoEncoder;
    private boolean isStarted = false;
    private boolean enableAudio = true;
    private SoundTouch mSoundTouch;
    private String mVideoOutputPath;
    private String mAudioTemPath;
    private RecordListener mRecordListener;
    private int retryCount = 0;
    private long totalFrame;
    private long totalTime;

    class StartRunnable implements Runnable {
        StartRunnable() {
        }

        public void run() {
            mRecordListener.onRecordStart();
        }
    }

    class StopRunnable implements Runnable {
        StopRunnable() {
        }

        public void run() {
            mRecordListener.onRecordStop();
        }
    }

    private class VideoRecorderThread extends Thread {

        private boolean recording;

        private VideoRecorderThread() {
            this.recording = true;
        }

        VideoRecorderThread(VideoRecorderCompat videoRecorderCompat, StartRunnable startRunnable) {
            this();
        }

        public void run() {
            while (true) {
                if (VideoRecorderCompat.this.mEncodeQueue.size() > 0) {
                    if (VideoRecorderCompat.this.mMediaMuxerWrapper != null) {
                        VideoRecorderCompat.this.encodeData();
                    }
                } else if (!this.recording) {
                    break;
                } else {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                    }
                }
            }
            if (VideoRecorderCompat.this.totalFrame > 0) {
                String str = "VideoRecorderCompat";
                Log.i(str, "Total frame: " + VideoRecorderCompat.this.totalFrame + " Time: " + VideoRecorderCompat.this.totalTime + "ms Encode frame avg: " + (VideoRecorderCompat.this.totalTime / VideoRecorderCompat.this.totalFrame) + "ms");
            }
            VideoRecorderCompat.this.stopRecord();
            if (!VideoRecorderCompat.this.enableAudio || VideoRecorderCompat.this.mSoundTouch == null) {
                boolean z = true;
                if (VideoRecorderCompat.this.mSoundTouch != null) {
                    File file = new File(VideoRecorderCompat.this.mVideoTemPath);
                    if (file.exists()) {
                        z = file.renameTo(new File(VideoRecorderCompat.this.mVideoOutputPath));
                    }
                }
                VideoRecorderCompat.this.recordSuccess(z);
                return;
            }
            VideoRecorderCompat.this.recordSuccess(VideoRecorderCompat.this.isRecordVideoSuccess());
        }
    }

    static class EncodeData {
        byte[] mData;
        EncodeData(byte[] bArr) {
            this.mData = bArr;
        }
    }

    public VideoRecorderCompat(Context context, int width, int height, String videoOutputPath, SoundTouch soundTouch) {
        this.width = width;
        this.height = height;
        this.mVideoTemPath = videoOutputPath;
        this.mContext = context;
        this.mSoundTouch = soundTouch;
        if (this.mSoundTouch != null) {
            File file = new File(this.mVideoTemPath);
            String name = file.getName();
            name = name.substring(0, name.length() - name.substring(name.lastIndexOf(".")).length());
            this.mVideoOutputPath = this.mVideoTemPath;
            this.mAudioTemPath = new File(file.getParent(), name + "_temp.aac").getPath();
            this.mVideoTemPath = new File(file.getParent(), name + "_temp.mp4").getPath();
        }
    }

    public void enableAudio(boolean enableAudio) {
        this.enableAudio = enableAudio;
    }

    public void setRecordListener(RecordListener recordListener) {
        this.mRecordListener = recordListener;
    }

    public void onEncodeData(byte[] bArr) {
        if (isRecording()) {
            this.mEncodeQueue.offer(new EncodeData(bArr));
        }
    }

    public boolean prepared() {
        try {
            this.mMediaMuxerWrapper = new MediaMuxerWrapper(this.mVideoTemPath);
            this.mMediaVideoEncoder = new MediaVideoEncoder(this.mMediaMuxerWrapper, this.width, this.height);
            if (this.enableAudio) {
                MediaAudioEncoder mediaAudioEncoder = new MediaAudioEncoder(this.mMediaMuxerWrapper, this.mAudioTemPath, this.mSoundTouch);
            }
            this.mMediaMuxerWrapper.prepare();
            this.mVideoRecorderThread = new VideoRecorderThread(this, null);
            this.mVideoRecorderThread.start();
            this.mMediaMuxerWrapper.startRecording();
            this.isStarted = true;
            startRecord();
            return true;
        } catch (Throwable e) {
            LogUtils.loge("VideoRecorderCompat", Log.getStackTraceString(e));
            stopRecord();
            return false;
        }
    }

    public void recordStop() {
        this.isStarted = false;
        if (this.mVideoRecorderThread != null) {
            this.mVideoRecorderThread.recording = false;
        }
        notifyStop();
    }

    public boolean isRecording() {
        return this.mMediaMuxerWrapper != null && this.isStarted;
    }

    public void stopRecord() {
        this.isStarted = false;
        if (this.mMediaMuxerWrapper != null) {
            this.mMediaMuxerWrapper.stopRecording();
            this.mMediaMuxerWrapper = null;
            this.mEncodeQueue.clear();
        }
    }

    private boolean isRecordVideoSuccess() {
        boolean b;
        while (true) {
            this.retryCount++;
            b = MediaKitCompat.mergeVideoStatus(mVideoTemPath,mAudioTemPath,mVideoOutputPath,mContext);
            if (!b && this.retryCount < 3) {
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                }
            } else if (b) {
                Util.deleteFile(mVideoTemPath);
                Util.deleteFile(mAudioTemPath);
                return true;
            }
        }
    }

    private void encodeData() {
        this.totalFrame++;
        EncodeData encodeData = (EncodeData) this.mEncodeQueue.poll();
        long currentTimeMillis = System.currentTimeMillis();
        this.mMediaVideoEncoder.ColorCovertToEncode(encodeData.mData);
        this.totalTime += System.currentTimeMillis() - currentTimeMillis;
    }

    private void startRecord() {
        if (this.mRecordListener != null) {
            this.mHandler.post(new StartRunnable());
        }
    }

    private void notifyStop() {
        if (this.mRecordListener != null) {
            this.mHandler.post(new StopRunnable());
        }
    }

    private void recordSuccess(final boolean z) {
        if (this.mRecordListener != null) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    mRecordListener.onRecordSuccess(z);
                }
            });
        }
    }
}
