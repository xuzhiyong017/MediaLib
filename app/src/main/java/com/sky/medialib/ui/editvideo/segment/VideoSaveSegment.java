package com.sky.medialib.ui.editvideo.segment;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.collection.ArrayMap;

import com.sky.media.kit.BaseMediaApplication;
import com.sky.media.kit.base.BaseActivity;
import com.sky.medialib.R;
import com.sky.medialib.ui.editvideo.segment.entity.VideoEditData;
import com.sky.medialib.ui.editvideo.segment.proto.MusicPlayerProtocol;
import com.sky.medialib.ui.editvideo.segment.proto.TextWatermarksProtocol;
import com.sky.medialib.ui.kit.common.animate.AnimationListener;
import com.sky.medialib.ui.kit.common.animate.ViewAnimator;
import com.sky.medialib.ui.kit.common.base.AppActivity;
import com.sky.medialib.ui.kit.media.VideoProcessCenter;
import com.sky.medialib.util.FileUtil;
import com.sky.medialib.util.PixelUtil;
import com.sky.medialib.util.ToastUtils;
import com.sky.medialib.util.Util;
import com.sky.medialib.util.WeakHandler;

import org.reactivestreams.Subscription;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class VideoSaveSegment extends BaseSegment<VideoEditData> {

    private WeakHandler handler = new WeakHandler();
    private MusicPlayerProtocol musicPlayerProtocol;
    private TextWatermarksProtocol watermarksProtocol;
    private Disposable disposable;
    @BindView(R.id.save_btn)
    ImageView mSaveButton;
    @BindView(R.id.save_toast)
    LinearLayout mSaveToast;

    public VideoSaveSegment(AppActivity baseActivity, VideoEditData videoEditData) {
        super(baseActivity, videoEditData);
        ButterKnife.bind(this, baseActivity);
        init();
    }

    private void init() {
        this.mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processVideoAndSave();
            }
        });
    }

    public void setMusicPlayerProtocol(MusicPlayerProtocol musicPlayerProtocol) {
        this.musicPlayerProtocol = musicPlayerProtocol;
    }

    public void setTextWatermarksProtocol(TextWatermarksProtocol textWatermarksProtocol) {
        this.watermarksProtocol = textWatermarksProtocol;
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.disposable != null && !this.disposable.isDisposed()) {
            this.disposable.dispose();
            this.disposable = null;
        }
    }

    private void hideTips() {
        ViewAnimator.animate(this.mSaveToast).translationY(0.0f, (float) (-PixelUtil.dip2px(65.0f)))
                .setDuration(400).interplolatorDecelerate().setOnEndListener(new AnimationListener.OnEndListener() {
            @Override
            public void onEnd() {
                mSaveToast.setVisibility(View.GONE);
            }
        }).start();
    }

    private void showTips() {
        ViewAnimator.animate(this.mSaveToast).translationY((float) (-PixelUtil.dip2px(65.0f)), 0.0f)
                .setDuration(400).interplolatorDecelerate().setOnStartListener(new AnimationListener.OnStartListener() {
            @Override
            public void onStart() {
                mSaveToast.setVisibility(View.GONE);
            }
        }).setOnEndListener(new AnimationListener.OnEndListener() {
            @Override
            public void onEnd() {

            }
        }).start();
    }


    private void processVideoAndSave() {
        ((VideoEditData) this.mData).processExt.pauseVideo();
        this.musicPlayerProtocol.pausePlay();
        this.disposable = VideoProcessCenter.getInstance().processVideo(mData.processExt.getPlayUri().getPath(), mData.processExt.getSequences(), mData.processExt.getUsedFilters(), mData.getMusicPath(), mData.isKeepVoice(), this.watermarksProtocol.getWaterMarkList())
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(Subscription subscription) throws Exception {
                        activity.showProgressDialog(R.string.combining);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        doVideoFinish(System.currentTimeMillis(), o.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        activity.dismissProgressDialog();
                        ToastUtils.INSTANCE.showToast(R.string.video_combine_failed);
                        mData.processExt.startPlay();
                        musicPlayerProtocol.startPlay();
                    }
                });
    }

     void doVideoFinish(long j, String str) throws Exception {
        this.activity.dismissProgressDialog();
        if (FileUtil.INSTANCE.exists(str)) {
            Util.notifyMediaCenter(BaseMediaApplication.sContext, str);
            showTips();
        } else {
            ToastUtils.INSTANCE.showToast(R.string.video_combine_failed);
        }
        mData.processExt.startPlay();
        musicPlayerProtocol.startPlay();
    }

}
