package com.sky.medialib.util.task;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskPoolExecutor extends ThreadPoolExecutor {

    private static final Integer sLock = Integer.valueOf(1);

    private static TaskPoolExecutor sTaskPoolExecutor;

    public static TaskPoolExecutor getInstance() {
        synchronized (sLock) {
            if (sTaskPoolExecutor == null) {
                synchronized (sLock) {
                    sTaskPoolExecutor = new TaskPoolExecutor();
                    sTaskPoolExecutor.allowCoreThreadTimeOut(true);
                }
            }
        }
        return sTaskPoolExecutor;
    }

    private TaskPoolExecutor() {
        super(Runtime.getRuntime().availableProcessors(), (Runtime.getRuntime().availableProcessors() * 2) + 1, 10, TimeUnit.SECONDS, new LinkedBlockingQueue(), new TaskFactory());
    }

    public void excuteRunnable(Runnable runnable) {
        execute(runnable);
    }
}
