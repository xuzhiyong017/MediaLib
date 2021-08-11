package com.sky.medialib.ui.kit.download;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbsDownAsyncTask<Params, Progress, Result> implements ICancelable{

    private static final BlockingQueue<Runnable> sWorkQueue = new LinkedBlockingQueue(64);

    private static final ThreadFactory threadFactory = new DefaultThreadFactory();

    private static final ThreadPoolExecutor mThreadPoolExecutor = new ThreadPoolExecutor(4, 128, 1, TimeUnit.SECONDS, sWorkQueue, threadFactory, new DiscardOldestPolicy());
    private static final InternalHandler mMainHandler = new InternalHandler(Looper.getMainLooper());

    private final WorkerRunnable<Params, Result> mWorkerRunnable;
    private final FutureTask<Result> futureTask;
    private volatile Status mStatus = Status.PENDING;

    static class DefaultThreadFactory implements ThreadFactory {

        private final AtomicInteger threadNum = new AtomicInteger(1);

        DefaultThreadFactory() {
        }

        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "AsyncTask #" + this.threadNum.getAndIncrement());
        }
    }

    private static abstract class WorkerRunnable<Params, Result> implements Callable<Result> {

        Params[] mParams;

        private WorkerRunnable() {
        }

        private WorkerRunnable(DefaultThreadFactory defaultThreadFactory) {
            this();
        }
    }

    private static class AsyncTaskResult<Data> {
        final AbsDownAsyncTask mTask;
        final Data[] mData;

        AsyncTaskResult(AbsDownAsyncTask absDownAsyncTask, Data... dataArr) {
            this.mTask = absDownAsyncTask;
            this.mData = dataArr;
        }
    }

    private static class InternalHandler extends Handler {
        public InternalHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            AsyncTaskResult asyncTaskResult = (AsyncTaskResult) message.obj;
            switch (message.what) {
                case 1:
                    asyncTaskResult.mTask.finish(asyncTaskResult.mData[0]);
                    return;
                case 2:
                    asyncTaskResult.mTask.onProgressUpdate(asyncTaskResult.mData);
                    return;
                case 3:
                    asyncTaskResult.mTask.onCancelled();
                    return;
                default:
                    return;
            }
        }
    }

    public enum Status {
        PENDING,
        RUNNING,
        FINISHED
    }

    protected abstract Result doInBackground(Params... paramsArr);

    public AbsDownAsyncTask(final int threadPriority) {
        mStatus = Status.PENDING;
        this.mWorkerRunnable = new WorkerRunnable<Params, Result>() {
            public Result call() throws Exception {
                Process.setThreadPriority(threadPriority);
                return AbsDownAsyncTask.this.doInBackground(this.mParams);
            }
        };
        this.futureTask = new FutureTask<Result>(this.mWorkerRunnable) {
            protected void done() {
                Object obj = null;
                try {
                    obj = get();
                } catch (ExecutionException e2) {
                    throw new RuntimeException("An error occured while executing doInBackground()", e2.getCause());
                } catch (CancellationException e3) {
                    AbsDownAsyncTask.mMainHandler.obtainMessage(3, new AsyncTaskResult(AbsDownAsyncTask.this, (Object[]) obj)).sendToTarget();
                    return;
                } catch (Throwable th) {
                    RuntimeException runtimeException = new RuntimeException("An error occured while executing doInBackground()", th);
                }
                AbsDownAsyncTask.mMainHandler.obtainMessage(1, new AsyncTaskResult(AbsDownAsyncTask.this, obj)).sendToTarget();
            }
        };
    }

    protected void onPreExecute() {

    }

    protected void onPostExecute(Result result) {
    }

    protected void onProgressUpdate(Progress... progressArr) {
    }

    protected void onCancelled() {
    }

    public final boolean isCancelled() {
        return this.futureTask.isCancelled();
    }

    public final boolean cancel(boolean z) {
        return this.futureTask.cancel(z);
    }

    public final AbsDownAsyncTask<Params, Progress, Result> executeTask(Params... paramsArr) {
        if (this.mStatus != Status.PENDING) {
            switch (this.mStatus) {
                case RUNNING:
                    throw new IllegalStateException("Cannot execute task: the task is already running.");
                case FINISHED:
                    throw new IllegalStateException("Cannot execute task: the task has already been executed (a task can be executed only once)");
            }
        }
        this.mStatus = Status.RUNNING;
        onPreExecute();
        this.mWorkerRunnable.mParams = paramsArr;
        mThreadPoolExecutor.execute(this.futureTask);
        return this;
    }

    protected final void publishProgress(Progress... progressArr) {
        mMainHandler.obtainMessage(2, new AsyncTaskResult(this, progressArr)).sendToTarget();
    }

    private void finish(Result result) {
        Result result2 = result;
        if (isCancelled()) {
            result2 = null;
        }
        onPostExecute(result2);
        this.mStatus = Status.FINISHED;
    }
}
