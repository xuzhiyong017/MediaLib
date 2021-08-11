package com.sky.medialib.ui.kit.download;

import android.app.Application;
import android.content.Intent;

import com.sky.medialib.util.AppBroadcastHelper;
import com.sky.medialib.util.FileUtil;
import com.sky.medialib.util.NetworkUtil;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class ObjectsDownloader {

    public static final String ACTION_DOWN_OBJECT_PROGRESS_UPDATED = "action_down_object_progress_updated";
    public static final String ACTION_DOWN_OBJECT_STATE_CHANGED = "action_down_object_state_changed";
    public static final String EXTRA_OBJECT = "extra_object";
    public static final String EXTRA_PROGRESS = "extra_progress";
    public static final String EXTRA_STATE = "extra_state";
    private static final long MIN_RESERVE_SPACE = 1048576;
    private static final int RUNNING_MAX = 1;
    private static final String TAG = "ObjectsDownloader";

    private static Application mApplication;
    private CopyOnWriteArrayList<DownObjectJob> mJobs = new CopyOnWriteArrayList();
    private boolean mListenProgressChanged;
    private boolean mNoWifiDown;
    private IDownFileFinishListener mDownFinishListener = new IDownFileFinishListener() {
        @Override
        public void onDownLoadFinish(DownFileResult downFileResult) {
            if (downFileResult != null) {
                DownFileTask task = downFileResult.getTask();
                DownObjectJob downObject = ObjectsDownloader.this.getDownObjectJob(task);
                if (downObject != null) {
                    IDownloadable downloadable = downObject.getDownloadable();
                    ObjectsDownloader.this.mJobs.remove(downObject);
                    if (downFileResult.getCode() == 0 && task.getTempFileSize() == task.getFileSize() && task.getFileSize() > 0) {
                        downloadable.saveTmp2Cache();
                        ObjectsDownloader.this.jobFinish(downObject);
                    } else {
                        ObjectsDownloader.this.jobFail(downObject);
                    }
                    DownObjectJob.IDownObjectFinishListener downObjectFinishListener = downObject.getDownObjectFinishListener();
                    if (downObjectFinishListener != null) {
                        downObjectFinishListener.onDownObjectFinished(downloadable, downObject.getState());
                    }
                    ObjectsDownloader.notifyDownObjectStateChanged(downloadable, downObject.getState());
                }
            }
        }
    };
    private IDownFileProgressListener mDownFileProgressListener = new IDownFileProgressListener() {
        @Override
        public void onProgress(DownFileTask downFileTask) {
            if (ObjectsDownloader.this.mListenProgressChanged) {
                DownObjectJob a = ObjectsDownloader.this.getDownObjectJob(downFileTask);
                if (a != null) {
                    ObjectsDownloader.notifyDownObjectProgressUpdated(a.getDownloadable(), (long) downFileTask.getProgress());
                }
            }
        }
    };

    public ObjectsDownloader(Application application) {
        mApplication = application;
    }

    private void initJob(DownObjectJob downObjectJob) {
        IDownloadable a = downObjectJob.getDownloadable();
        if (!canDownObject(a)) {
            jobFinish(downObjectJob);
        } else if (!isDownloading(a)) {
            startJob(downObjectJob);
        }
    }

    private void startJob(DownObjectJob downObjectJob) {
        if (!NetworkUtil.isWifiConnected(mApplication) && (!this.mNoWifiDown || !NetworkUtil.isNetConnected(mApplication))) {
            cancelJob(downObjectJob);
        } else if (!FileUtil.INSTANCE.checkExternalSpace(MIN_RESERVE_SPACE)) {
            waitJob(downObjectJob);
        } else if (getDownloadingCount() < RUNNING_MAX) {
            downJob(downObjectJob);
        } else {
            waitJob(downObjectJob);
        }
    }

    private void downJob(DownObjectJob downObjectJob) {
        IDownloadable a = downObjectJob.getDownloadable();
        downObjectJob.setState(DownObjectJob.STATE_RUNNING);
        if (downObjectJob.getTask() == null) {
            DownFileTask downFileTask = new DownFileTask(a.getDownloadUrl(), a.getTmpPath());
            downFileTask.setOnDownFileFinishListener(this.mDownFinishListener);
            if (this.mListenProgressChanged) {
                downFileTask.addDownFileProgressListener(this.mDownFileProgressListener);
            }
            downFileTask.executeTask(new DownFileParams[0]);
            downObjectJob.setTask(downFileTask);
        }
    }

    private void waitJob(DownObjectJob downObjectJob) {
        downObjectJob.setState(DownObjectJob.STATE_WAITING);
    }

    private void cancelJob(DownObjectJob downObjectJob) {
        DownFileTask d = downObjectJob.getTask();
        if (d != null) {
            d.cancel(true);
            downObjectJob.setTask(null);
        }
        waitJob(downObjectJob);
    }

    protected void jobFinish(DownObjectJob downObjectJob) {
        downObjectJob.setState(DownObjectJob.STATE_FINISHED);
        notifyJobFinished();
    }

    protected void jobFail(DownObjectJob downObjectJob) {
        downObjectJob.setState(DownObjectJob.STATE_FAILED);
        notifyJobFinished();
    }

    public void startAllJobs() {
        if (!this.mJobs.isEmpty()) {
            Iterator it = this.mJobs.iterator();
            while (it.hasNext()) {
                initJob((DownObjectJob) it.next());
            }
            ObjectsDownloader.notifyDownObjectStateChanged(((DownObjectJob) this.mJobs.get(0)).getDownloadable());
        }
    }

    public void pauseAllJobs() {
        if (!this.mJobs.isEmpty()) {
            Iterator it = this.mJobs.iterator();
            while (it.hasNext()) {
                cancelJob((DownObjectJob) it.next());
            }
            ObjectsDownloader.notifyDownObjectStateChanged(((DownObjectJob) this.mJobs.get(0)).getDownloadable());
        }
    }

    public boolean canDownObject(IDownloadable iDownloadable) {
        return (iDownloadable == null || iDownloadable.haveCache()) ? false : true;
    }

    public DownObjectJob downObjectImmediately(IDownloadable iDownloadable, DownObjectJob.IDownObjectFinishListener IDownObjectFinishListener) {
        DownObjectJob downObjectJob;
        if (canDownObject(iDownloadable)) {
            if (getDownloadingCount() >= 1) {
                cancelJob(getFirstDownloadingJob());
            }
            downObjectJob = downObjectJob(iDownloadable, false);
            downObjectJob.setDownObjectFinishListener(IDownObjectFinishListener);
            ObjectsDownloader.notifyDownObjectStateChanged(iDownloadable);
            return downObjectJob;
        }
        downObjectJob = new DownObjectJob(iDownloadable);
        downObjectJob.setDownObjectFinishListener(IDownObjectFinishListener);
        return downObjectJob;
    }

    private DownObjectJob downObjectJob(IDownloadable iDownloadable, boolean z) {
        DownObjectJob downObjectJob = new DownObjectJob(iDownloadable);
        int indexOf = this.mJobs.indexOf(downObjectJob);
        if (indexOf != -1) {
            downObjectJob = (DownObjectJob) this.mJobs.get(indexOf);
        } else if (z) {
            this.mJobs.add(0, downObjectJob);
        } else {
            this.mJobs.add(downObjectJob);
        }
        initJob(downObjectJob);
        return downObjectJob;
    }

    public boolean cancelObject(IDownloadable iDownloadable) {
        DownObjectJob d = getDownObjectJob(iDownloadable);
        if (d == null) {
            return false;
        }
        cancelJob(d);
        this.mJobs.remove(d);
        notifyJobFinished();
        ObjectsDownloader.notifyDownObjectStateChanged(iDownloadable);
        return true;
    }

    private void notifyJobFinished(IDownloadable iDownloadable) {
        if (getDownloadingCount() < 1) {
            DownObjectJob f = getFirstWaitingJob(iDownloadable);
            if (f != null) {
                initJob(f);
            }
        }
    }

    private void notifyJobFinished() {
        notifyJobFinished(null);
    }

    private DownObjectJob getFirstDownloadingJob() {
        for (int i = 0; i < mJobs.size(); i++) {
            DownObjectJob downObjectJob = (DownObjectJob) this.mJobs.get(i);
            DownFileTask d = downObjectJob.getTask();
            if (d != null && !d.isCancelled()) {
                return downObjectJob;
            }
        }
        return null;
    }

    private DownObjectJob getFirstWaitingJob(IDownloadable iDownloadable) {
        Iterator<DownObjectJob> iterator = mJobs.iterator();
        while (iterator.hasNext()){
            DownObjectJob downObjectJob = (DownObjectJob) iterator.next();
            if (downObjectJob.getState() == 0) {
                IDownloadable a = downObjectJob.getDownloadable();
                if (!canDownObject(a)) {
                    iterator.remove();
                } else if (iDownloadable == null || !iDownloadable.equals(a)) {
                    return downObjectJob;
                }
            }
        }
        return null;
    }

    public boolean isDownloading(IDownloadable iDownloadable) {
        Iterator it = this.mJobs.iterator();
        while (it.hasNext()) {
            DownObjectJob downObjectJob = (DownObjectJob) it.next();
            if (iDownloadable.equals(downObjectJob.getDownloadable())) {
                DownFileTask d = downObjectJob.getTask();
                if (d == null || d.isCancelled()) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    private DownObjectJob getDownObjectJob(DownFileTask downFileTask) {
        if (downFileTask == null) {
            return null;
        }
        DownObjectJob downObjectJob;
        Iterator it = this.mJobs.iterator();
        while (it.hasNext()) {
            downObjectJob = (DownObjectJob) it.next();
            if (downFileTask.equals(downObjectJob.getTask())) {
                return downObjectJob;
            }
        }
       return null;
    }

    public DownObjectJob getDownObjectJob(IDownloadable iDownloadable) {
        if (iDownloadable == null) {
            return null;
        }
        DownObjectJob downObjectJob;
        Iterator it = this.mJobs.iterator();
        while (it.hasNext()) {
            downObjectJob = (DownObjectJob) it.next();
            if (iDownloadable.equals(downObjectJob.getDownloadable())) {
                return downObjectJob;
            }
        }
        return null;
    }

    private int getDownloadingCount() {
        int i = 0;
        Iterator it = this.mJobs.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return i2;
            }
            DownFileTask d = ((DownObjectJob) it.next()).getTask();
            if (!(d == null || d.isCancelled())) {
                i2++;
            }
            i = i2;
        }
    }

    private static void notifyDownObjectStateChanged(IDownloadable iDownloadable) {
        Intent intent = new Intent(ACTION_DOWN_OBJECT_STATE_CHANGED);
        intent.putExtra(EXTRA_OBJECT, iDownloadable);
        AppBroadcastHelper.sendBroadcast(intent);
    }

    private static void notifyDownObjectStateChanged(IDownloadable iDownloadable, int i) {
        Intent intent = new Intent(ACTION_DOWN_OBJECT_STATE_CHANGED);
        intent.putExtra(EXTRA_OBJECT, iDownloadable);
        intent.putExtra(EXTRA_STATE, i);
        AppBroadcastHelper.sendBroadcast(intent);
    }

    private static void notifyDownObjectProgressUpdated(IDownloadable iDownloadable, long j) {
        Intent intent = new Intent(ACTION_DOWN_OBJECT_PROGRESS_UPDATED);
        intent.putExtra(EXTRA_OBJECT, iDownloadable);
        intent.putExtra(EXTRA_PROGRESS, j);
        AppBroadcastHelper.sendBroadcast(intent);
    }

    public void setListenProgressChanged(boolean z) {
        this.mListenProgressChanged = z;
    }

    public void setNoWifiDown(boolean z) {
        this.mNoWifiDown = z;
    }

    public boolean isListenProgressChanged() {
        return this.mListenProgressChanged;
    }

    public boolean isNoWifiDown() {
        return this.mNoWifiDown;
    }

}
