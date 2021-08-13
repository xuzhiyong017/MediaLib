package com.sky.medialib.util.task;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class SimpleTask implements Runnable {

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private Runnable beforeRunnable = new Runnable() {
        @Override
        public void run() {
            onPreExecute();
        }
    };

    private Runnable afterRunnable = new Runnable() {
        @Override
        public void run() {
            onPostExecute();
        }
    };

    protected AtomicBoolean isCancel = new AtomicBoolean(false);

    protected abstract void doInBackground();

    public void run() {
        this.mHandler.post(this.beforeRunnable);
        doInBackground();
        if (!this.isCancel.get()) {
            this.mHandler.post(this.afterRunnable);
        }
    }

    protected void onPreExecute() {
    }

    protected void onPostExecute() {
    }

    public SimpleTask execute() {
        TaskPoolExecutor.getInstance().excuteRunnable(this);
        return this;
    }

    public void cancel() {
        this.isCancel.set(true);
    }
}
