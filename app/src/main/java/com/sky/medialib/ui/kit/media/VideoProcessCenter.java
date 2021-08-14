package com.sky.medialib.ui.kit.media;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.sky.media.image.core.cache.ImageBitmapCache;
import com.sky.media.image.core.filter.Filter;
import com.sky.media.image.core.util.BitmapUtil;
import com.sky.media.kit.BaseMediaApplication;
import com.sky.media.kit.mediakit.MediaKit;
import com.sky.media.kit.mediakit.MediaKitCompat;
import com.sky.media.kit.mediakit.Size;
import com.sky.media.kit.video.VideoSequenceHelper;
import com.sky.medialib.ui.kit.common.network.RxUtil;
import com.sky.medialib.ui.kit.effect.Effect;
import com.sky.medialib.ui.kit.effect.EffectReader;
import com.sky.medialib.ui.kit.effect.EffectRender;
import com.sky.medialib.util.FileUtil;
import com.sky.medialib.util.Storage;
import com.sky.medialib.util.Util;
import com.sky.medialib.util.task.Task;
import com.sky.medialib.util.task.TaskQueue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;

public class VideoProcessCenter {
    private static VideoProcessCenter sInstance;
    private Context mContext = BaseMediaApplication.sContext;
    private final String tempVideoDir = Storage.getFilePathByType(3);
    private final ConcurrentHashMap<String, String> mCacheSuccess = new ConcurrentHashMap();
    private final TaskQueue mTaskQueue = new TaskQueue(1);
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public interface OnVideoProcessListener {
        void onStart(String str);
        void OnProcessSuccess(String str, String str2);
        void onFailed(String str);
    }

    private class ReverseVideoTask extends Task<String> {
        String mSourceFile;
        String outputFile;
        CopyOnWriteArrayList<OnVideoProcessListener> mListener = new CopyOnWriteArrayList();

        ReverseVideoTask(String str) {
            Log.d("VideoProcessCenter", "Reverse new:" + str);
            this.mSourceFile = str;
        }

        protected void startProcess() {
            Log.d("VideoProcessCenter", "Reverse start:" + this.mSourceFile + " callback:" + this.mListener.size());
            if (Util.isNotEmptyList(this.mListener)) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        notifyStart();
                    }
                });
            }
        }


         void notifyStart() {
            Iterator it = this.mListener.iterator();
            while (it.hasNext()) {
                ((OnVideoProcessListener) it.next()).onStart(this.mSourceFile);
            }
        }

        protected void executeTask() {
            long currentTimeMillis = System.currentTimeMillis();
            String str = tempVideoDir + getSimpleName(this.mSourceFile).replace(".mp4", "_reverse.mp4");
            String str2 = tempVideoDir + getSimpleName(this.mSourceFile).replace(".mp4", "_reverse1.mp4");
            String str3 = tempVideoDir + getSimpleName(this.mSourceFile).replace(".mp4", "_audio.aac");
            if (FileUtil.INSTANCE.exists(str)) {
                this.outputFile = str;
            } else {
                if (!isCancel() && MediaKitCompat.reverseVideo(this.mSourceFile, str2, mContext)) {
                    if (isCancel() || !MediaKit.containsAudio(this.mSourceFile)) {
                        FileUtil.INSTANCE.renameTo(str2, str);
                        this.outputFile = str;
                    } else if (!isCancel() && MediaKit.covertAudioToAACFile(this.mSourceFile, str3) && !isCancel() && MediaKitCompat.mergeAudioAndVideo(str2, str3, str, mContext) && FileUtil.INSTANCE.exists(str)) {
                        this.outputFile = str;
                        FileUtil.INSTANCE.deleteFiles(str2);
                    }
                }
                FileUtil.INSTANCE.deleteFiles(str3);
            }
            Log.d("VideoProcessCenter", "Reverse cost:" + (System.currentTimeMillis() - currentTimeMillis) + "ms");
        }

        protected void stopProcess() {
            if (FileUtil.INSTANCE.exists(this.outputFile)) {
                Log.d("VideoProcessCenter", "Reverse success:" + this.mSourceFile + "\n path:" + this.outputFile + " callback:" + this.mListener.size());
                mCacheSuccess.put(this.mSourceFile, this.outputFile);
                if (!isCancel() && Util.isNotEmptyList(this.mListener)) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            notifySuccess();
                        }
                    });
                    return;
                }
                return;
            }
            Log.w("VideoProcessCenter", "Reverse failed:" + this.mSourceFile + " callback:" + this.mListener.size());
            if (!isCancel() && Util.isNotEmptyList(this.mListener)) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        notifyFail();
                    }
                });
            }
        }

        void notifySuccess() {
            Iterator it = this.mListener.iterator();
            while (it.hasNext()) {
                ((OnVideoProcessListener) it.next()).OnProcessSuccess(this.mSourceFile, this.outputFile);
            }
        }

         void notifyFail() {
            Iterator it = this.mListener.iterator();
            while (it.hasNext()) {
                ((OnVideoProcessListener) it.next()).onFailed(this.mSourceFile);
            }
        }

        public void cancel() {
            super.cancel();
            this.mListener.clear();
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            return this.mSourceFile.equals(((ReverseVideoTask) obj).mSourceFile);
        }

        public int hashCode() {
            return this.mSourceFile.hashCode();
        }
    }

    public static VideoProcessCenter getInstance() {
        if (sInstance == null) {
            sInstance = new VideoProcessCenter();
        }
        return sInstance;
    }

    private VideoProcessCenter() {
        this.mTaskQueue.start();
    }

    private Effect readWaterMarkEffect(int width, int height) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        gsonBuilder.disableHtmlEscaping();
        return EffectReader.readEffect(BaseMediaApplication.sContext, BitmapUtil.Scheme.ASSETS.wrap("watermark/"), str -> gsonBuilder.create().fromJson(str,Effect.class), width, height);
    }

    public Flowable<Object> processVideo(final String str, final Stack<VideoSequenceHelper.BaseSequence> stack, final List<Filter> list, final String str2, final boolean z, final List<Watermark> list2) {
        return Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<String> emitter) throws Exception {
                processVideoFilter(list,str,stack,list2,str2,z,emitter);
            }
        }, BackpressureStrategy.DROP).compose(RxUtil.rxSchedulers());
    }

    void processVideoFilter(List list, String str, Stack stack, List list2, String str2, boolean z, FlowableEmitter flowableEmitter) throws Exception {
        boolean a;
        List arrayList = new ArrayList();
        if (Util.isNotEmptyList(list)) {
            arrayList.addAll(list);
        }
        EffectRender effectRender = null;
        if (true) {
            Size c = MediaKit.getVideoSize(str);
            if (c == null) {
                c = new Size(this.mContext.getResources().getDisplayMetrics().widthPixels, this.mContext.getResources().getDisplayMetrics().heightPixels);
            }
            Effect a2 = readWaterMarkEffect(c.width, c.height);
            effectRender = new EffectRender(this.mContext, ImageBitmapCache.INSTANCE);
            effectRender.setEffect(a2);
        }
        String str3 = Storage.getFilePathByType(3) + getSimpleName(str).replace(".mp4", "_filter.mp4");
        String str4 = Storage.getFilePathByType(1) + Util.getNewVideoPath();
        if (MediaKitExt.processVideoWithFilter(this.mContext, stack, list2, str, str3, arrayList, effectRender)) {
            a = MediaKitExt.doMusicAndVideo(str3, str4, str2, z, this.mContext);
        } else {
            a = false;
        }
        FileUtil.INSTANCE.deleteFiles(str3);
        if (a && FileUtil.INSTANCE.exists(str4)) {
            flowableEmitter.onNext(str4);
        } else {
            flowableEmitter.onNext("");
        }
        flowableEmitter.onComplete();
    }

    public void processVideo(String str, OnVideoProcessListener onVideoProcessListener) {
        String str2 = (String) this.mCacheSuccess.get(str);
        if (FileUtil.INSTANCE.exists(str2)) {
            this.mHandler.post(new Runnable() {
                @Override
                public void run() {
                    notifyVideoSuccess(onVideoProcessListener,str,str2);
                }
            });
            return;
        }
        this.mCacheSuccess.remove(str);
        Task a = this.mTaskQueue.getTaskByVideoPath(str);
        ReverseVideoTask reverseVideoTask;
        if (a instanceof ReverseVideoTask) {
            reverseVideoTask = (ReverseVideoTask) a;
            if (onVideoProcessListener != null) {
                if (!reverseVideoTask.mListener.contains(onVideoProcessListener)) {
                    reverseVideoTask.mListener.add(onVideoProcessListener);
                }
                onVideoProcessListener.onStart(str);
                return;
            }
            return;
        }
        reverseVideoTask = new ReverseVideoTask(str);
        if (onVideoProcessListener != null) {
            if (!reverseVideoTask.mListener.contains(onVideoProcessListener)) {
                reverseVideoTask.mListener.add(onVideoProcessListener);
            }
            onVideoProcessListener.onStart(str);
        }
        this.mTaskQueue.addTask(reverseVideoTask.setSourceFile(str));
    }

    static void notifyVideoSuccess(OnVideoProcessListener onVideoProcessListener, String str, String str2) {
        if (onVideoProcessListener != null) {
            onVideoProcessListener.OnProcessSuccess(str, str2);
        }
    }

    public void removeVideoListener(String str, OnVideoProcessListener onVideoProcessListener) {
        Task a = this.mTaskQueue.getTaskByVideoPath(str);
        if (a instanceof ReverseVideoTask) {
            ReverseVideoTask reverseVideoTask = (ReverseVideoTask) a;
            if (reverseVideoTask.mListener.contains(onVideoProcessListener)) {
                reverseVideoTask.mListener.remove(onVideoProcessListener);
            }
        }
    }

    public void cancelTask() {
        this.mTaskQueue.cancelTask();
    }

    public void destroy() {
        this.mCacheSuccess.clear();
        this.mTaskQueue.release();
        this.mContext = null;
        sInstance = null;
    }

    private String getSimpleName(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        return str.substring(str.lastIndexOf("/") + 1, str.length());
    }
}
