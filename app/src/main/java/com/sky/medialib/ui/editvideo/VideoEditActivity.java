package com.sky.medialib.ui.editvideo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.blankj.utilcode.util.ScreenUtils;
import com.sky.media.image.core.cache.ImageBitmapCache;
import com.sky.media.image.core.view.ContainerViewHelper;
import com.sky.media.image.core.view.ProcessRelativeContainer;
import com.sky.media.image.core.view.ProcessSurfaceView;
import com.sky.media.kit.mediakit.MediaKit;
import com.sky.media.kit.mediakit.Size;
import com.sky.medialib.R;
import com.sky.medialib.ui.crop.VideoCropActivity;
import com.sky.medialib.ui.dialog.SimpleAlertDialog;
import com.sky.medialib.ui.editvideo.process.VideoProcessExt;
import com.sky.medialib.ui.editvideo.segment.BaseSegment;
import com.sky.medialib.ui.editvideo.segment.PublishSegment;
import com.sky.medialib.ui.editvideo.segment.VideoBeautySegment;
import com.sky.medialib.ui.editvideo.segment.VideoDraftSegment;
import com.sky.medialib.ui.editvideo.segment.VideoEffectSegment;
import com.sky.medialib.ui.editvideo.segment.VideoMusicSegment;
import com.sky.medialib.ui.editvideo.segment.VideoPlaySegment;
import com.sky.medialib.ui.editvideo.segment.VideoSaveSegment;
import com.sky.medialib.ui.editvideo.segment.VideoTextSegment;
import com.sky.medialib.ui.editvideo.segment.VideoToolbarSegment;
import com.sky.medialib.ui.editvideo.segment.VideoVoiceSegment;
import com.sky.medialib.ui.editvideo.segment.entity.VideoDraft;
import com.sky.medialib.ui.editvideo.segment.entity.VideoEditData;
import com.sky.medialib.ui.editvideo.segment.listener.IDataChangedListener;
import com.sky.medialib.ui.kit.common.base.AppActivity;
import com.sky.medialib.ui.kit.media.VideoProcessCenter;
import com.sky.medialib.ui.kit.model.Music;
import com.sky.medialib.ui.kit.model.PublishVideo;
import com.sky.medialib.util.EventBusHelper;
import com.sky.medialib.util.ToastUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoEditActivity extends AppActivity implements IDataChangedListener<VideoEditData> {
    @BindView( R.id.back)
    ImageView mBack;
    private VideoEditData mData;
    private boolean mIsJumpFromCamera;
    private int mOldDataHashCode;
    @BindView(R.id.frame)
    ProcessRelativeContainer mPreviewLayout;
    private List<BaseSegment> mSegments = new ArrayList();
    private VideoBeautySegment mVideoBeautySegment;
    private VideoDraftSegment mVideoDraftSegment;
    private VideoEffectSegment mVideoEffectSegment;
    private VideoMusicSegment mVideoMusicSegment;
    private VideoPlaySegment mVideoPlaySegment;
    private VideoSaveSegment mVideoSaveSegment;
    private VideoTextSegment mVideoTextSegment;
    private VideoToolbarSegment mVideoToolbarSegment;
    private VideoVoiceSegment mVideoVoiceSegment;
    private PublishSegment mPublishSegment;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_video_edit);
        ButterKnife.bind((Activity) this);
        ImageBitmapCache.INSTANCE.clear();
        EventBusHelper.register(this);
        if (dealIntent()) {
            initView();
            mVideoBeautySegment = new VideoBeautySegment(this, mData);
            mVideoVoiceSegment = new VideoVoiceSegment(this, mData);
            mVideoTextSegment = new VideoTextSegment(this, mData);
            mVideoEffectSegment = new VideoEffectSegment(this, mData);
            mVideoMusicSegment = new VideoMusicSegment(this, mData);
            mVideoSaveSegment = new VideoSaveSegment(this, mData);
            mVideoPlaySegment = new VideoPlaySegment(this, mData);
            mPublishSegment = new PublishSegment(this, mData);
            mVideoToolbarSegment = new VideoToolbarSegment(this, mData);
            mVideoDraftSegment = new VideoDraftSegment(this, mData);
            mVideoBeautySegment.setDraftLayoutProtocol(mVideoDraftSegment);
            mVideoBeautySegment.setPublishLayoutProtocol(mPublishSegment);
            mVideoPlaySegment.setMusicPlayerProtocol(mVideoMusicSegment);
            mVideoEffectSegment.setVideoPlayerProtocol(mVideoPlaySegment);
            mVideoSaveSegment.setMusicPlayerProtocol(mVideoMusicSegment);
            mVideoSaveSegment.setTextWatermarksProtocol(mVideoTextSegment);
            mVideoTextSegment.setToolbarProtocol(mVideoToolbarSegment);
            mSegments.add(mVideoBeautySegment);
            mSegments.add(mVideoVoiceSegment);
            mSegments.add(mVideoDraftSegment);
            mSegments.add(mVideoTextSegment);
            mSegments.add(mVideoEffectSegment);
            mSegments.add(mVideoMusicSegment);
            mSegments.add(mVideoSaveSegment);
            mSegments.add(mVideoPlaySegment);
            mSegments.add(mVideoToolbarSegment);
            for (BaseSegment a : mSegments) {
                a.onCreate(bundle);
            }
            mVideoPlaySegment.initPlayer();
            mVideoMusicSegment.startMusicPlay();
            mVideoVoiceSegment.initVoiceStart();
            mVideoMusicSegment.initMusicStart();
            mVideoTextSegment.initText();
            mVideoEffectSegment.initEffect();
            mVideoBeautySegment.initBeautyFilter();
        }else{
            ToastUtils.INSTANCE.showToast(R.string.video_not_exist);
            finish();
        }

    }

    public static void launchByDraft(Activity activity, VideoDraft videoDraft) {
        Intent intent = new Intent(activity, VideoEditActivity.class);
        intent.putExtra("key_video_draft", videoDraft);
        activity.startActivity(intent);
    }

    protected boolean isSupportSwipeBack() {
        return false;
    }

    private boolean dealIntent() {
        Intent intent = getIntent();
        if (intent == null || intent.getExtras() == null) {
            return false;
        }
        VideoDraft videoDraft = (VideoDraft) intent.getSerializableExtra("key_video_draft");
        if (videoDraft == null) {
            PublishVideo publishVideo = (PublishVideo) intent.getSerializableExtra(VideoCropActivity.KEY_VIDEO);
            if (publishVideo == null) {
                return false;
            }
            String videoPath = publishVideo.getVideoPath();
            if (TextUtils.isEmpty(videoPath) || !new File(videoPath).exists()) {
                return false;
            }
            mData = new VideoEditData(new VideoDraft());
            mData.addDataChangedListener((IDataChangedListener) this);
            mData.setVideoPath(videoPath);
            mData.getTopic(intent.getStringExtra("key_topic_name"));
            mData.setTopicList(intent.getStringArrayListExtra("key_dynamic_sicker_topic"));
            mData.isCameraMusic(intent.getBooleanExtra("key_camera_had_music", false));
            mData.setMusic((Music) intent.getSerializableExtra("key_camera_add_music_info"));
            mIsJumpFromCamera = true;
        } else {
            String obj = videoDraft.videoPath;
            if (TextUtils.isEmpty(obj) || !new File(obj).exists()) {
                return false;
            }
            mData = new VideoEditData(videoDraft);
            mData.addDataChangedListener((IDataChangedListener) this);
            mIsJumpFromCamera = false;
        }
        VideoProcessCenter.getInstance().processVideo(mData.getVideoPath(), null);
        mOldDataHashCode = mData.getVideoDraft().hashCode();
        return true;
    }

    private void initView() {
        Size c;
        if (mData.isReverse()) {
            c = MediaKit.getVideoSize(mData.getVideoReversePath());
        } else {
            c = MediaKit.getVideoSize(mData.getVideoPath());
        }
        int a = ScreenUtils.getScreenWidth();
        int b = ScreenUtils.getScreenHeight();
        mPreviewLayout.setScaleType(ContainerViewHelper.ScaleType.FIT_WIDTH);
        if (c == null || c.height == 0 || c.width == 0) {
            mPreviewLayout.setAspectRatio(0.5625f, a, b);
        } else {
            mPreviewLayout.setAspectRatio((((float) c.width) * 1.0f) / ((float) c.height), a, b);
        }
        ProcessSurfaceView processSurfaceView = (ProcessSurfaceView) findViewById(R.id.processing_view);
        processSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mVideoBeautySegment.onTouch(event);
                return true;
            }
        });
        VideoProcessExt videoProcessExt = new VideoProcessExt(mPreviewLayout, processSurfaceView);
        mData.processExt = videoProcessExt;
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCancel();
            }
        });
    }


    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        for (BaseSegment a : mSegments) {
            a.onActivityResult(i, i2, intent);
        }
    }

    public void onResume() {
        super.onResume();
        for (BaseSegment g_ : mSegments) {
            g_.onResume();
        }
    }

    public void onPause() {
        super.onPause();
        for (BaseSegment h_ : mSegments) {
            h_.onPause();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        VideoProcessCenter.getInstance().cancelTask();
        if (mData != null) {
            mData.removeDataChangedListener((IDataChangedListener) this);
        }
        EventBusHelper.unregister(this);
        for (BaseSegment i_ : mSegments) {
            i_.onDestroy();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String str) {
        if ("event_weibo_close_activity".equals(str)) {
            finish();
        }
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i != 4) {
            return super.onKeyDown(i, keyEvent);
        }
        if (!mVideoBeautySegment.hideView()) {
            doCancel();
        }
        return true;
    }

    private void doCancel() {
        if (mData == null || mData.getVideoDraft() == null || mData.getVideoDraft().hashCode() == mOldDataHashCode) {
            setResult(-1);
            finish();
            return;
        }
        SimpleAlertDialog.newBuilder(this)
                .setMessage(mIsJumpFromCamera ? R.string.back_edit_video_to_camera : R.string.back_edit_video_to_draft, 17)
                .setCancleable(false).setLeftBtn(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (!mIsJumpFromCamera) {
                    finish();
                }
            }
        }).setRightBtn(mIsJumpFromCamera ? R.string.continue_back_camera : R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
                if (mIsJumpFromCamera) {
                    setResult(-1);
                    finish();
                } else {
                    mVideoDraftSegment.saveDraft(false);
                    finish();
                }
            }
        }).build().show();
    }

    public String getPageId() {
        return "30000013";
    }

    public void onDataChanged(VideoEditData videoEditData) {
    }
}
