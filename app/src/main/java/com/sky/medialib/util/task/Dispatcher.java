package com.sky.medialib.util.task;

import android.os.Process;

import java.util.concurrent.BlockingQueue;

class Dispatcher extends Thread {

    private final BlockingQueue<Task<?>> mBlockQueue;
    private volatile boolean isStop = false;

    public Dispatcher(BlockingQueue<Task<?>> blockingQueue) {
        this.mBlockQueue = blockingQueue;
    }

    public void stopDispatch() {
        this.isStop = true;
        interrupt();
    }

    public void run() {
        Process.setThreadPriority(10);
        while (true) {
            try {
                Task task = (Task) this.mBlockQueue.take();
                if (task.isCancel()) {
                    task.stop();
                } else {
                    task.startProcess();
                    task.executeTask();
                    task.stop();
                }
            } catch (InterruptedException e) {
                if (this.isStop) {
                    return;
                }
            }
        }
    }
}
