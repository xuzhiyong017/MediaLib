package com.sky.medialib.util.task;

import android.text.TextUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskQueue {
    private final Set<Task<?>> mSetTask;
    private final PriorityBlockingQueue<Task<?>> mPriorityBlockQueue;
    private Dispatcher[] mDispatchers;
    private AtomicInteger mSeqnum;

    public TaskQueue() {
        this(2);
    }

    public TaskQueue(int i) {
        this.mSetTask = new HashSet();
        this.mPriorityBlockQueue = new PriorityBlockingQueue();
        this.mSeqnum = new AtomicInteger();
        this.mDispatchers = new Dispatcher[i];
    }

    public void start() {
        release();
        for (int i = 0; i < this.mDispatchers.length; i++) {
            Dispatcher dispatcher = new Dispatcher(this.mPriorityBlockQueue);
            this.mDispatchers[i] = dispatcher;
            dispatcher.start();
        }
    }

    public void release() {
        for (Dispatcher dispatcher : this.mDispatchers) {
            if (dispatcher != null) {
                dispatcher.stopDispatch();
            }
        }
    }

    public int getSeq() {
        return this.mSeqnum.incrementAndGet();
    }

    public void cancelTask() {
        synchronized (this.mSetTask) {
            for (Task d : this.mSetTask) {
                d.cancel();
            }
        }
    }

    public <T> Task<T> addTask(Task<T> task) {
        task.setTaskQueue(this);
        synchronized (this.mSetTask) {
            this.mSetTask.add(task);
        }
        task.setSeq(getSeq());
        this.mPriorityBlockQueue.add(task);
        return task;
    }

    public Task<?> getTaskByVideoPath(String str) {
        if (!TextUtils.isEmpty(str)) {
            for (Task<?> task : this.mSetTask) {
                if (str.equals(task.getSourceFile())) {
                    return task;
                }
            }
        }
        return null;
    }

    <T> void removeQueue(Task<T> task) {
        synchronized (this.mSetTask) {
            this.mSetTask.remove(task);
        }
    }
}
