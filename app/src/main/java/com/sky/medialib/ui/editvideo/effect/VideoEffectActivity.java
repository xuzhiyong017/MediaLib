package com.sky.medialib.ui.editvideo.effect;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



import java.io.Serializable;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.blankj.utilcode.util.ScreenUtils;
import com.sky.media.image.core.base.BaseRender;
import com.sky.media.image.core.cache.ImageBitmapCache;
import com.sky.media.image.core.filter.Filter;
import com.sky.media.image.core.filter.IAdjustable;
import com.sky.media.image.core.view.ProcessFrameContainer;
import com.sky.media.image.core.view.ProcessSurfaceView;
import com.sky.media.kit.player.IMediaPlayer;
import com.sky.media.kit.render.videorender.GridRender;
import com.sky.media.kit.render.videorender.LightedEdgeRender;
import com.sky.media.kit.render.videorender.MirrorRender;
import com.sky.media.kit.render.videorender.ShakeRender;
import com.sky.media.kit.render.videorender.SobelEdgeDetectionRender;
import com.sky.media.kit.render.videorender.SoulRender;
import com.sky.media.kit.video.VideoProcess;
import com.sky.medialib.ui.dialog.SimpleAlertDialog;
import com.sky.medialib.ui.editvideo.effect.adapter.VideoEffectAdapter;
import com.sky.medialib.ui.editvideo.effect.adapter.VideoEffectItemTouchListener;
import com.sky.medialib.ui.editvideo.process.VideoProcessExt;
import com.sky.medialib.R;
import com.sky.medialib.ui.kit.common.base.AppActivity;
import com.sky.medialib.ui.kit.manager.ToolFilterManager;
import com.sky.medialib.ui.kit.manager.VideoEffectManager;
import com.sky.medialib.ui.kit.media.CustomMediaPlayer;
import com.sky.medialib.ui.kit.media.VideoProcessCenter;
import com.sky.medialib.ui.kit.model.VideoEffect;
import com.sky.medialib.ui.kit.view.SequenceSeekBar;
import com.sky.medialib.ui.kit.view.SequenceSeekBarHelper;
import com.sky.medialib.util.DateUtil;
import com.sky.medialib.ui.kit.manager.VideoEffectManager.VideoEffectExt;
import com.sky.medialib.ui.kit.view.SequenceSeekBar.SequenceExt;

public class VideoEffectActivity extends AppActivity {
    public static final java.lang.String KEY_VIDEO_EFFECT_CONFIG = "KEY_VIDEO_EFFECT_CONFIG";
    private static final int TAB_FILTER = 0;
    private static final int TAB_TIME = 1;
    @BindView(R.id.crop_cancel)
    ImageView mCancelView;
    @BindView(R.id.container_layout)
    ProcessFrameContainer mContainer;
    private int mCurrentTab = 0;
    @BindView(R.id.current_time)
    TextView mCurrentTimeView;
    @BindView(R.id.effect_filter)
    TextView mEffectFilterView;
    @BindView(R.id.effect_time)
    TextView mEffectTimeView;
    @BindView(R.id.filter_list)
    RecyclerView mFilterList;
    private VideoEffectManager.VideoEffectExt mLastHistory;
    @BindView(R.id.play)
    ImageView mPlayView;
    private VideoProcessCenter.OnVideoProcessListener mReverseCallback;
    @BindView(R.id.crop_done)
    View mSaveView;
    private int mScrollFilterOffset;
    private int mScrollFilterPosition;
    private int mScrollTimeOffset;
    private int mScrollTimePosition;
    @BindView(R.id.seek_bar)
    SequenceSeekBar mSeekBar;
    private SequenceSeekBarHelper mSeekBarHelper;
    @BindView( R.id.process_view)
    ProcessSurfaceView mTextureView;
    @BindView(R.id.tip)
    TextView mTipView;
    @BindView(R.id.total_time)
    TextView mTotalTimeView;
    private boolean mTouchingSeekBar;
    @BindView(R.id.undo)
    ImageView mUndoView;
    private VideoEffect mUsedTimeEffect;
    private VideoEffectAdapter mVideoEffectAdapter;
    private EffectConfig mVideoEffectConfig;
    private String mVideoPath;
    private VideoProcessExt mVideoProcess;


    class OnPressListenerExt implements VideoEffectItemTouchListener.OnPressListener {

        private int selectTab = -1;

        OnPressListenerExt() { }

        public void onSingleUp(int pos) {
            if (mCurrentTab == 1) {
                mVideoEffectAdapter.setSelectedPosition(pos);
                mUsedTimeEffect = mVideoEffectAdapter.getVideoEffect(pos);
                if (this.selectTab != pos) {
                    this.selectTab = pos;
                    pauseVideo();
                    loadVideo(true);
                } else if (mUsedTimeEffect.isReverse() && mUsedTimeEffect.getReverseState() == 2) {
                    if (mReverseCallback != null) {
                        VideoProcessCenter.getInstance().removeVideoListener(mVideoPath, mReverseCallback);
                    }
                    VideoProcessCenter.getInstance().processVideo(mVideoPath, mReverseCallback);
                }
            }
        }

        public void onLongPress(int pos, int action) {
            if (mCurrentTab == 0) {
                VideoEffect videoEffect = mVideoEffectAdapter.getVideoEffect(pos);
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mVideoProcess.pushSequence(mSeekBarHelper.addFilter(videoEffect.getFilter(), videoEffect.getColor()));
                        updateUndoBtn();
                        mVideoEffectAdapter.setSelectedPosition(pos);
                        startVideo();
                        return;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        mVideoEffectAdapter.setSelectedPosition(-1);
                        pauseVideo();
                        mSeekBarHelper.endSequence();
                        return;
                    default:
                        return;
                }
            }
        }
    }

    public static class EffectConfig implements Serializable {
        public java.lang.String videoPath;
        public java.lang.String musicPath;
        public boolean isKeepVoice;
        public boolean hasWhiteningTool;
        public int whiteningLevel;
        public boolean hasBuffingTool;
        public int buffingLevel;
        public boolean hasSlimFaceTool;
        public int faceLevel;
        public boolean hasEyeTool;
        public int eyelevel;
        public boolean hasSwitchRender;
        public int switchRenderId;
    }

    public static void launch(Activity activity, EffectConfig effectConfig, int i) {
        Intent intent = new Intent(activity, VideoEffectActivity.class);
        intent.putExtra("KEY_VIDEO_EFFECT_CONFIG", effectConfig);
        activity.startActivityForResult(intent, i);
    }

    protected boolean hasTitleBar() {
        return true;
    }

    protected boolean isSupportSwipeBack() {
        return false;
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ImageBitmapCache.INSTANCE.clear();
        ToolFilterManager.INSTANCE.initEffectVideo(this);
        VideoEffectManager.getInstance().getVideoEffectExt();
        setContentView(R.layout.activity_video_effect);
        ButterKnife.bind((Activity) this);
        initData();
        initView();
    }

    private void initData() {
        mVideoEffectConfig = (EffectConfig) getIntent().getSerializableExtra("KEY_VIDEO_EFFECT_CONFIG");
        mVideoPath = mVideoEffectConfig.videoPath;
    }

    private void initView() {
        Filter l;
        this.mSeekBarHelper = new SequenceSeekBarHelper(this.mSeekBar, new SequenceSeekBarHelper.OnSequenceSeekListener() {
            @java.lang.Override
            public void onStart(SequenceSeekBar sequenceSeekBar) {
                mTouchingSeekBar = true;
                pauseVideo();
            }

            @java.lang.Override
            public void onProgressChanged(SequenceSeekBar sequenceSeekBar, int i, boolean z) {
                if (mSeekBarHelper.isReverse()) {
                    mVideoProcess.processVideo((long) (mSeekBar.getMax() - i));
                } else {
                    mVideoProcess.processVideo((long) i);
                }
                mCurrentTimeView.setText(DateUtil.getTimeStemp(i));
            }

            @java.lang.Override
            public void onStop(SequenceSeekBar sequenceSeekBar) {
                mTouchingSeekBar = false;
            }
        });
        this.mVideoProcess = new VideoProcessExt(this.mContainer, this.mTextureView);
        loadHistory();
        this.mTextureView.setOnClickListener(new View.OnClickListener() {
            @java.lang.Override
            public void onClick(View v) {
                if (mVideoProcess.isPlaying()) {
                    pauseVideo();
                } else {
                    startVideo();
                }
            }
        });
        this.mUndoView.setOnClickListener(new View.OnClickListener() {
            @java.lang.Override
            public void onClick(View v) {
                SequenceSeekBar.SequenceExt d =mSeekBarHelper.getTopSequence();
               mVideoProcess.popSequence();
                if (d != null) {
                    if (mUsedTimeEffect.isReverse()) {
                       mSeekBar.setProgress((int) (d.total - d.start));
                    } else {
                       mSeekBar.setProgress((int) d.start);
                    }
                   mVideoProcess.seekTo(((int) d.start) / 1000);
                    pauseVideo();
                }
                updateUndoBtn();
            }
        });
       mEffectFilterView.setOnClickListener(new View.OnClickListener() {
           @java.lang.Override
           public void onClick(View v) {
               changeToFilterView();
           }
       });
       mEffectTimeView.setOnClickListener(new View.OnClickListener() {
           @java.lang.Override
           public void onClick(View v) {
               changeToTimeView();
           }
       });
       mCancelView.setOnClickListener(new View.OnClickListener() {
           @java.lang.Override
           public void onClick(View v) {
               showFinishDialog();
           }
       });
       mSaveView.setOnClickListener(new View.OnClickListener() {
           @java.lang.Override
           public void onClick(View v) {
               saveHistory();
           }
       });
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
       mFilterList.setLayoutManager(linearLayoutManager);
       mVideoEffectAdapter = new VideoEffectAdapter(this);
       mVideoEffectAdapter.setVideoEffectList(VideoEffectManager.getInstance().getFilterEffectList());
       mFilterList.setAdapter(this.mVideoEffectAdapter);
       mFilterList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                super.onScrollStateChanged(recyclerView, i);
                View childAt = linearLayoutManager.getChildAt(0);
                if (mCurrentTab == 0) {
                    mScrollFilterOffset = childAt.getLeft();
                    mScrollFilterPosition = linearLayoutManager.getPosition(childAt);
                    return;
                }
                mScrollTimeOffset = childAt.getLeft();
                mScrollTimePosition = linearLayoutManager.getPosition(childAt);
            }
        });
       mFilterList.addOnItemTouchListener(new VideoEffectItemTouchListener(this,mFilterList, new OnPressListenerExt()));
        loadVideo(false);
        if (mVideoEffectConfig.hasBuffingTool) {
            l = ToolFilterManager.INSTANCE.getEffectBuffingTool();
            ((IAdjustable) l.getAdjuster().getMRender()).adjust(mVideoEffectConfig.buffingLevel, 0, 100);
           mVideoProcess.addFilter(l);
        }
        if (mVideoEffectConfig.hasWhiteningTool) {
            l = ToolFilterManager.INSTANCE.getEffectWhiteningTool();
            ((IAdjustable) l.getAdjuster().getMRender()).adjust(mVideoEffectConfig.whiteningLevel, 0, 100);
           mVideoProcess.addFilter(l);
        }

        if (mVideoEffectConfig.hasSwitchRender) {
           mVideoProcess.addFilter(ToolFilterManager.INSTANCE.getEffectVideoFilterById(mVideoEffectConfig.switchRenderId));
        }
    }

    private void loadVideo(final boolean z) {
        int dimensionPixelSize = (getResources().getDisplayMetrics().widthPixels - getResources().getDimensionPixelSize(R.dimen.video_effect_left_margin)) - getResources().getDimensionPixelSize(R.dimen.video_effect_right_margin);
        int a = ((ScreenUtils.getAppScreenHeight() - getResources().getDimensionPixelSize(R.dimen.video_effect_title_bar_height)) - getResources().getDimensionPixelSize(R.dimen.video_effect_bottom_bar_height)) - getResources().getDimensionPixelSize(R.dimen.video_effect_operation_panel_height);
        final VideoProcess.MediaPlayerBuilder mediaPlayerBuilder = new VideoProcess.MediaPlayerBuilder();
        mediaPlayerBuilder.setPreviewSize(dimensionPixelSize, a);
        mediaPlayerBuilder.setLooping(false);
        mediaPlayerBuilder.volume(mVideoEffectConfig.isKeepVoice ? 1.0f : 0.0f);
        mediaPlayerBuilder.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @java.lang.Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                mSeekBarHelper.setMediaPlayer(iMediaPlayer);
                mSeekBarHelper.setReversePlay(mUsedTimeEffect.isReverse(), mUsedTimeEffect.getColor());
                mTotalTimeView.setText(DateUtil.getTimeStemp(iMediaPlayer.getDuration() * 1000));
                if (z) {
                    mSeekBarHelper.startTimer();
                    mPlayView.setVisibility(View.INVISIBLE);
                    return;
                }else{
                    mVideoProcess.seekTo(0);
                    pauseVideo();
                }

            }
        });
        mediaPlayerBuilder.setOnCompletionListener(iMediaPlayer -> {
            if (!mTouchingSeekBar) {
                if (mSeekBarHelper.hasSequenceExt()) {
                    pauseVideo();
                    return;
                }
                mVideoProcess.seekTo(0);
                startVideo();
            }
        });
        mediaPlayerBuilder.setMediaPlayer(new CustomMediaPlayer(this));
        mediaPlayerBuilder.setNeedPlay(z);
        if (mReverseCallback != null) {
            VideoProcessCenter.getInstance().removeVideoListener(mVideoPath,mReverseCallback);
        }
        if (mUsedTimeEffect.isReverse()) {
           mReverseCallback = new VideoProcessCenter.OnVideoProcessListener() {

                public void OnProcessSuccess(java.lang.String str, java.lang.String str2) {
                    mVideoProcess.initMediaPlayer(Uri.parse(str2), mediaPlayerBuilder);
                    mVideoProcess.refreshAllFilters();
                    mUsedTimeEffect.setReverseState(0);
                    mVideoEffectAdapter.notifyDataSetChanged();
                }

                public void onStart(java.lang.String str) {
                    mUsedTimeEffect.setReverseState(1);
                    mVideoEffectAdapter.notifyDataSetChanged();
                    if (mVideoProcess.getMInput() == null) {
                        mVideoProcess.initMediaPlayer(Uri.parse(str), mediaPlayerBuilder);
                    }
                }

                public void onFailed(java.lang.String str) {
                    mUsedTimeEffect.setReverseState(2);
                    mVideoEffectAdapter.notifyDataSetChanged();
                }
            };
            VideoProcessCenter.getInstance().processVideo(mVideoPath,mReverseCallback);
            return;
        }
       mVideoProcess.initMediaPlayer(Uri.parse(mVideoPath), mediaPlayerBuilder);
    }

    private void loadHistory() {
       mUsedTimeEffect = (VideoEffect) VideoEffectManager.getInstance().getTimeEffectList().get(0);
        VideoEffectExt d = VideoEffectManager.getInstance().getVideoEffectExt();
        if (d != null) {
           mLastHistory = new VideoEffectExt();
           mLastHistory.videoEffect = d.videoEffect;
            for (SequenceExt a : d.sequenceExtList) {
                SequenceExt a2 = VideoEffectManager.getInstance().copySequenceExt(a);
               mLastHistory.sequenceExtList.add(a2);
               mVideoProcess.pushSequence(a2);
               mSeekBar.push(a2);
            }
           mUsedTimeEffect = d.videoEffect;
            updateUndoBtn();
        }
    }

    private void saveHistory() {
        VideoEffectExt videoEffectExt = new VideoEffectExt();
        videoEffectExt.sequenceExtList.clear();
        Iterator it =mSeekBar.getSequences().iterator();
        while (it.hasNext()) {
            videoEffectExt.sequenceExtList.add(VideoEffectManager.getInstance().copySequenceExt((SequenceExt) it.next()));
        }
        videoEffectExt.videoEffect =mUsedTimeEffect;
        VideoEffectManager.getInstance().setVideoEffectExt(videoEffectExt);

        setResult(Activity.RESULT_OK);
        finish();
    }

    private void updateUndoBtn() {
        int i = View.INVISIBLE;
        if (mCurrentTab == 0) {
            ImageView imageView = mUndoView;
            if (!mVideoProcess.getSequences().isEmpty()) {
                i = View.VISIBLE;
            }
            imageView.setVisibility(i);
            return;
        }
       mUndoView.setVisibility(View.INVISIBLE);
    }

    private void changeToFilterView() {
        if (mCurrentTab != 0) {
           mCurrentTab = 0;
           mVideoEffectAdapter.setVideoEffectList(VideoEffectManager.getInstance().getFilterEffectList());
           mVideoEffectAdapter.setSelectedPosition(-1);
            ((LinearLayoutManager)mFilterList.getLayoutManager()).scrollToPositionWithOffset(mScrollFilterPosition,mScrollFilterOffset);
            pauseVideo();
           mSeekBar.setShowTimeEffectColor(false);
           mEffectFilterView.setTextColor(-1);
           mEffectFilterView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getResources().getDrawable(R.drawable.shoot_tab_foot_icon_hl));
           mEffectFilterView.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.video_effect_bottom_text_drawable_padding));
           mEffectTimeView.setTextColor(-2130706433);
           mEffectTimeView.setCompoundDrawables(null, null, null, null);
           mTipView.setText("选择位置后，长按使用效果");
           updateUndoBtn();
        }
    }

    private void changeToTimeView() {
        if (mCurrentTab != 1) {
           mCurrentTab = 1;
           mVideoEffectAdapter.setVideoEffectList(VideoEffectManager.getInstance().getTimeEffectList());
           mVideoEffectAdapter.setSelectedPosition(mUsedTimeEffect.isReverse() ? 1 : 0);
            ((LinearLayoutManager)mFilterList.getLayoutManager()).scrollToPositionWithOffset(mScrollTimePosition,mScrollTimeOffset);
            pauseVideo();
           mSeekBar.setShowTimeEffectColor(true);
           mEffectTimeView.setTextColor(-1);
           mEffectTimeView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getResources().getDrawable(R.drawable.shoot_tab_foot_icon_hl));
           mEffectTimeView.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.video_effect_bottom_text_drawable_padding));
           mEffectFilterView.setTextColor(-2130706433);
           mEffectFilterView.setCompoundDrawables(null, null, null, null);
           mTipView.setText("点击选择时间特效");
            updateUndoBtn();
        }
    }

    private void startVideo() {
       mVideoProcess.startPlay();
       mSeekBarHelper.startTimer();
       mPlayView.setVisibility(4);
    }

    private void pauseVideo() {
       mVideoProcess.pauseVideo();
       mSeekBarHelper.destroy();
       mPlayView.setVisibility(0);
    }

    private void showFinishDialog() {
        VideoEffectExt videoEffectExt = new VideoEffectExt();
        videoEffectExt.sequenceExtList =mSeekBar.getSequences();
        videoEffectExt.videoEffect =mUsedTimeEffect;
        if (VideoEffectManager.getInstance().isNoChange(videoEffectExt,mLastHistory)) {
            finish();
        } else {
            SimpleAlertDialog.newBuilder(this)
                    .setCancleable(false)
                    .setMessage((java.lang.CharSequence) "是否清除已添加的特效", 17)
                    .setLeftBtn(R.string.dialog_cancel).setRightBtn(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                @java.lang.Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    dialogInterface.dismiss();
                    VideoEffectManager.getInstance().setVideoEffectExt(mLastHistory);
                    setResult(-1);
                    finish();
                }
            }).build().show();
        }
    }



    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i != 4) {
            return super.onKeyDown(i, keyEvent);
        }
        showFinishDialog();
        return true;
    }

    protected void onPause() {
        super.onPause();
        pauseVideo();
    }

    protected void onDestroy() {
        super.onDestroy();
        if (mReverseCallback != null) {
            VideoProcessCenter.getInstance().removeVideoListener(mVideoPath,mReverseCallback);
        }
    }

    public java.lang.String getPageId() {
        return "10000601";
    }
}
