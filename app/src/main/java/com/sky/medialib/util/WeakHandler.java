package com.sky.medialib.util;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WeakHandler {

    final ChainedRef mRunnables;

    private final Callback mCallback;

    private final ExecHandler mExec;

    private Lock mLock;

    static class ChainedRef {

        @Nullable
        ChainedRef next;
        @Nullable
        ChainedRef prev;
        @NonNull
        final Runnable runnable;
        @NonNull
        final WeakRunnable wrapper;
        @NonNull
        Lock lock;

        public ChainedRef(Lock lock, Runnable runnable) {
            this.runnable = runnable;
            this.lock = lock;
            this.wrapper = new WeakRunnable(new WeakReference(runnable), new WeakReference(this));
        }

        public WeakRunnable remove() {
            this.lock.lock();
            try {
                if (this.prev != null) {
                    this.prev.next = this.next;
                }
                if (this.next != null) {
                    this.next.prev = this.prev;
                }
                this.prev = null;
                this.next = null;
                return this.wrapper;
            } finally {
                this.lock.unlock();
            }
        }

        /* renamed from: a */
        public void insertAfter(ChainedRef chainedRef) {
            this.lock.lock();
            try {
                if (this.next != null) {
                    this.next.prev = chainedRef;
                }
                chainedRef.next = this.next;
                this.next = chainedRef;
                chainedRef.prev = this;
            } finally {
                this.lock.unlock();
            }
        }

       @Nullable
        public WeakRunnable remove(Runnable runnable) {
           lock.lock();
           try {
               ChainedRef curr = this.next; // Skipping head
               while (curr != null) {
                   if (curr.runnable == runnable) { // We do comparison exactly how Handler does inside
                       return curr.remove();
                   }
                   curr = curr.next;
               }
           } finally {
               lock.unlock();
           }
           return null;
        }
    }

    private static class ExecHandler extends Handler {

        private final WeakReference<Callback> mCallBack;

        ExecHandler() {
            this.mCallBack = null;
        }

        ExecHandler(WeakReference<Callback> weakReference) {
            this.mCallBack = weakReference;
        }

        ExecHandler(Looper looper) {
            super(looper);
            this.mCallBack = null;
        }

        public void handleMessage(Message message) {
            if (this.mCallBack != null) {
                Callback callback = (Callback) this.mCallBack.get();
                if (callback != null) {
                    callback.handleMessage(message);
                }
            }
        }
    }

    static class WeakRunnable implements Runnable {

        private final WeakReference<Runnable> mDelegate;
        private final WeakReference<ChainedRef> mReference;

        WeakRunnable(WeakReference<Runnable> weakReference, WeakReference<ChainedRef> weakReference2) {
            this.mDelegate = weakReference;
            this.mReference = weakReference2;
        }

        public void run() {
            Runnable runnable = (Runnable) this.mDelegate.get();
            ChainedRef chainedRef = (ChainedRef) this.mReference.get();
            if (chainedRef != null) {
                chainedRef.remove();
            }
            if (runnable != null) {
                runnable.run();
            }
        }
    }

    public WeakHandler() {
        this.mLock = new ReentrantLock();
        this.mRunnables = new ChainedRef(this.mLock, null);
        this.mCallback = null;
        this.mExec = new ExecHandler();
    }

    public WeakHandler(Callback callback) {
        this.mLock = new ReentrantLock();
        this.mRunnables = new ChainedRef(this.mLock, null);
        this.mCallback = callback;
        this.mExec = new ExecHandler(new WeakReference(callback));
    }

    public WeakHandler(Looper looper) {
        this.mLock = new ReentrantLock();
        this.mRunnables = new ChainedRef(this.mLock, null);
        this.mCallback = null;
        this.mExec = new ExecHandler(looper);
    }

    public final boolean post(Runnable runnable) {
        return this.mExec.post(wrapRunnable(runnable));
    }

    public final boolean postDelayed(Runnable runnable, long delayMillis) {
        return this.mExec.postDelayed(wrapRunnable(runnable), delayMillis);
    }

    public final void removeCallbacks(Runnable runnable) {
        Runnable a = this.mRunnables.remove(runnable);
        if (a != null) {
            this.mExec.removeCallbacks(a);
        }
    }

    public final boolean sendMessage(Message message) {
        return this.mExec.sendMessage(message);
    }

    public final boolean sendEmptyMessage(int what) {
        return this.mExec.sendEmptyMessage(what);
    }

    public final boolean sendEmptyMessageDelayed(int what, long delayMillis) {
        return this.mExec.sendEmptyMessageDelayed(what, delayMillis);
    }

    public final boolean sendMessageDelayed(Message message, long delayMillis) {
        return this.mExec.sendMessageDelayed(message, delayMillis);
    }

    public final void removeMessages(int what) {
        this.mExec.removeMessages(what);
    }

    private WeakRunnable wrapRunnable(Runnable runnable) {
        if (runnable == null) {
            throw new NullPointerException("Runnable can't be null");
        }
        ChainedRef chainedRef = new ChainedRef(this.mLock, runnable);
        this.mRunnables.insertAfter(chainedRef);
        return chainedRef.wrapper;
    }
}
