package com.sky.medialib.ui.kit.download;

public class DownObjectJob {

    public static final int STATE_FAILED = 3;
    public static final int STATE_FINISHED = 2;
    public static final int STATE_PAUSE = 4;
    public static final int STATE_RUNNING = 1;
    public static final int STATE_UNKNOWN = -1;
    public static final int STATE_WAITING = 0;

    private IDownloadable mDownloadable;
    private DownFileTask mTask;
    private IDownObjectFinishListener mListener;
    private int mState = -1;


    public interface IDownObjectFinishListener {
        void onDownObjectFinished(IDownloadable iDownloadable, int i);
    }

    public DownObjectJob(IDownloadable iDownloadable) {
        this.mDownloadable = iDownloadable;
    }

    public IDownloadable getDownloadable() {
        return this.mDownloadable;
    }

    public void setDownObjectFinishListener(IDownObjectFinishListener IDownObjectFinishListener) {
        this.mListener = IDownObjectFinishListener;
    }
    public IDownObjectFinishListener getDownObjectFinishListener() {
        return this.mListener;
    }

    public int getState() {
        return this.mState;
    }

    public void setState(int i) {
        this.mState = i;
    }

    public DownFileTask getTask() {
        return this.mTask;
    }

    public void setTask(DownFileTask downFileTask) {
        this.mTask = downFileTask;
    }

    public int hashCode() {
        return (this.mDownloadable == null ? 0 : this.mDownloadable.hashCode()) + 31;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DownObjectJob downObjectJob = (DownObjectJob) obj;
        if (this.mDownloadable == null) {
            if (downObjectJob.mDownloadable != null) {
                return false;
            }
            return true;
        } else if (this.mDownloadable.equals(downObjectJob.mDownloadable)) {
            return true;
        } else {
            return false;
        }
    }
}
