package com.sky.medialib.ui.kit.download;

public class DownFileResult {
    private int mCode;
    private DownFileTask mTask;
    private Object mContent;

    public DownFileResult(int code, DownFileTask mTask, Object obj) {
        this.mCode = code;
        this.mTask = mTask;
        this.mContent = obj;
    }

    public DownFileTask getTask() {
        return this.mTask;
    }

    public void setCode(int code) {
        this.mCode = code;
    }

    public int getCode() {
        return this.mCode;
    }

    public Object getContent() {
        return this.mContent;
    }

    public void setContent(Object obj) {
        this.mContent = obj;
    }

    public void setTask(DownFileTask downFileTask) {
        this.mTask = downFileTask;
    }

}
