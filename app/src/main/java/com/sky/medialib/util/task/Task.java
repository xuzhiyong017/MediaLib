package com.sky.medialib.util.task;

public abstract class Task<T> implements Comparable<Task<T>> {
    private Integer seq;
    private TaskQueue mQueue;
    private boolean isCancel = false;
    private String mSourceFile;

    public enum RunPriority {
        LOW,
        NORMAL,
        HIGH,
        IMMEDIATE
    }

    protected abstract void executeTask();

    public Task<?> setSourceFile(String str) {
        this.mSourceFile = str;
        return this;
    }

    public String getSourceFile() {
        return this.mSourceFile;
    }

    void stop() {
        if (this.mQueue != null) {
            this.mQueue.removeQueue(this);
            stopProcess();
        }
    }
    public Task<?> setTaskQueue(TaskQueue taskQueue) {
        this.mQueue = taskQueue;
        return this;
    }

    public final Task<?> setSeq(int i) {
        this.seq = Integer.valueOf(i);
        return this;
    }

    public void cancel() {
        this.isCancel = true;
    }

    public boolean isCancel() {
        return this.isCancel;
    }

    public RunPriority mo15811k() {
        return RunPriority.NORMAL;
    }

    protected void startProcess() {
    }

    protected void stopProcess() {
    }

    public int compareTo(Task<T> task) {
        RunPriority k = mo15811k();
        RunPriority k2 = task.mo15811k();
        return k == k2 ? this.seq.intValue() - task.seq.intValue() : k2.ordinal() - k.ordinal();
    }
}
