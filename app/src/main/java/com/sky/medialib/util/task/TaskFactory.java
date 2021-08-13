package com.sky.medialib.util.task;

import java.util.concurrent.ThreadFactory;

public class TaskFactory implements ThreadFactory {
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setName("SimpleTask");
        thread.setPriority(5);
        return thread;
    }
}
