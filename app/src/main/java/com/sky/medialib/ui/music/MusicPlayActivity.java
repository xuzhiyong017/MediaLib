package com.sky.medialib.ui.music;

import static android.view.View.GONE;
import static com.sky.medialib.ui.music.MusicChooseActivityKt.KEY_IS_FROM_CAMERA;

import android.animation.ObjectAnimator;
import android.app.Application;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.sky.media.ffmpeg.executor.FFmpegExecutor;
import com.sky.media.kit.BaseMediaApplication;
import com.sky.medialib.R;
import com.sky.medialib.ui.kit.common.base.AppActivity;
import com.sky.medialib.ui.kit.common.base.recycler.ErrorView;
import com.sky.medialib.ui.kit.common.view.MusicWaveView;
import com.sky.medialib.ui.kit.common.view.ObservableScrollView;
import com.sky.medialib.ui.kit.download.DownObjectJob;
import com.sky.medialib.ui.kit.download.IDownloadable;
import com.sky.medialib.ui.kit.download.ObjectsDownloader;
import com.sky.medialib.ui.kit.model.Music;
import com.sky.medialib.ui.music.event.CutMusicInfoEvent;
import com.sky.medialib.util.EventBusHelper;
import com.sky.medialib.util.NetworkUtil;
import com.sky.medialib.util.NumberUtil;
import com.sky.medialib.util.Storage;
import com.sky.medialib.util.ToastUtils;
import com.sky.medialib.util.Util;
import com.sky.medialib.util.task.SimpleTask;

import java.util.Timer;
import java.util.TimerTask;


public class MusicPlayActivity extends AppActivity implements OnErrorListener, OnPreparedListener, OnSeekCompleteListener {
    public static final String KEY_MUSIC = "key_music";
    private static final int STATE_IDLE = 1;
    private static final int STATE_PAUSE = 3;
    private static final int STATE_PLAY = 2;
    private static final int STATE_PREPARED = 4;
    private static final String TAG = "MusicPlayActivity";
    LottieAnimationView mAnimationView;
    private ObjectAnimator mAnimator;
    ImageView mBg;
    ImageView mCancel;
    private int mCurrentState = 1;
    LinearLayout mCutMusic;
    private ObjectsDownloader mDownloader;
    ErrorView mEmptyView;
    private boolean mIsClickPaused;
    private boolean mIsCutting;
    private boolean mIsFromCamera;
    private MediaPlayer mMediaPlayer;
    private Music mMusic;
    ImageView mPlayView;
    TextView mSingerName;
    RoundedImageView mSongCover;
    TextView mSongName;
    TextView mStartPoint;
    private Timer mTimer;
    TextView mTip;
    TextView mTotalTime;
    ObservableScrollView mWaveScroll;
    MusicWaveView mWaveView;

    class TimerTaskExt extends TimerTask {
        TimerTaskExt() {
        }

        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (mMediaPlayer != null && !isDestroyed()) {
                            int currentPosition = mMediaPlayer.getCurrentPosition();
                            mWaveView.setProgress(currentPosition);
                            int round = Math.round((((float) mWaveView.getOffsetX()) * getPlayDuration()) / 1080.0f);
                            if (((float) (currentPosition - round)) >= getPlayDuration()) {
                                mMediaPlayer.seekTo(round);
                            }
                        }
                    } catch (Throwable e) {
                       e.printStackTrace();
                    }
                }
            });
        }
    }

    class CutTask extends SimpleTask {
     
        String curPath = "";
        int result = -1;

        CutTask() {
        }

        protected void onPreExecute() {
        }

        protected void doInBackground() {
            curPath = Storage.getFilePathByType(4) + Util.getNewMp3Path();
            float a = NumberUtil.valueOffloat(((((float) mWaveView.getOffsetX()) * getPlayDuration()) / 1080.0f) / 1000.0f, 1);
            result = FFmpegExecutor.getInstance(MusicPlayActivity.this).cutAudio(mMusic.getFinalPath(), curPath, a, NumberUtil.valueOffloat(Math.min(getPlayDuration() / 1000.0f, ((float) mMusic.duration) - a), 1));
        }

        protected void onPostExecute() {
           dismissProgressDialog();
            if (result == 0) {
                CutMusicInfoEvent cutMusicInfoEvent = new CutMusicInfoEvent();
                cutMusicInfoEvent.mMusic = mMusic;
                cutMusicInfoEvent.mCutMusicPath = curPath;
                cutMusicInfoEvent.mIsFromCamera = mIsFromCamera;
                EventBusHelper.post(cutMusicInfoEvent);
                mIsCutting = false;
                finish();
            }else{
                mIsCutting = false;
                ToastUtils.INSTANCE.showToast(R.string.music_cut_failed);
            }

        }
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_music_play);
        bindView();
        mMusic = (Music) getIntent().getSerializableExtra("key_music");
        mIsFromCamera = getIntent().getBooleanExtra(KEY_IS_FROM_CAMERA, false);
        if (mMusic == null || TextUtils.isEmpty(mMusic.url)) {
            finish();
            return;
        }
        initMediaPlayer();
        initView();
        initMusicInfo();
        initDownloader();
    }

    private void bindView() {
        mSongCover = findViewById(R.id.song_cover);
        mSongName = findViewById(R.id.song_name);
        mSingerName = findViewById(R.id.singer_name);
        mTotalTime = findViewById(R.id.total_time);
        mBg = findViewById(R.id.bg);
        mCancel = findViewById(R.id.toolbar_navigation);
        mCutMusic = findViewById(R.id.toolbar_menu_layout);
        mEmptyView = findViewById(R.id.error_view);
        mPlayView = findViewById(R.id.play_view);
        mAnimationView = findViewById(R.id.loading);
        mWaveScroll = findViewById(R.id.wave_view_scroll);
        mWaveView = findViewById(R.id.wave_view);
        mTip = findViewById(R.id.tip);
        mStartPoint = findViewById(R.id.start_point);
    }

    private float getPlayDuration() {
        return mIsFromCamera ? 15000.0f : 30000.0f;
    }

    public void startTimer() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimer = new Timer();
        mTimer.schedule(new TimerTaskExt(), 0, 10);
    }

    public void cancelTimer() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimer = null;
    }

    public String getPageId() {
        return "30000218";
    }

    protected boolean hasTitleBar() {
        return true;
    }

    protected boolean isSupportSwipeBack() {
        return false;
    }

    private void initMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnSeekCompleteListener(this);
        mMediaPlayer.setLooping(true);
    }

    private void initDownloader() {
        mDownloader = new ObjectsDownloader((Application) BaseMediaApplication.sContext);
        mDownloader.setListenProgressChanged(true);
        mDownloader.setNoWifiDown(true);
    }

    private void initView() {
        addImageMenu(R.drawable.selector_crop_done);
        mPlayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentState == 1) {
                    initMusicInfo();
                } else if (mCurrentState == 2) {
                    mIsClickPaused = true;
                    pause();
                } else if (mCurrentState == 3 || mCurrentState == 4) {
                    mIsClickPaused = false;
                    start();
                }
            }
        });
        StringBuilder stringBuilder = new StringBuilder();
        if (mMusic.artist == null || mMusic.artist.size() <= 0) {
            stringBuilder.append("未知歌手");
        } else {
            for (String append : mMusic.artist) {
                stringBuilder.append(append).append(" ");
            }
        }
        mSingerName.setText(stringBuilder.toString());
        mSongName.setText(mMusic.name);
        Glide.with(this).load(mMusic.photo).placeholder(R.drawable.music_cover_img_default).into(mSongCover);
        Glide.with(this).load(mMusic.photo).placeholder(R.drawable.music_cover_img_default).into(mBg);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(getString(R.string.start_point, new Object[]{Util.formatShowTime(0)}));
        spannableStringBuilder.setSpan(new ForegroundColorSpan(-26552), 3, 8, 33);
        mStartPoint.setText(spannableStringBuilder);
        mWaveScroll.setOnScrollChangedListener(new ObservableScrollView.OnScrollChangedListener() {
            @Override
            public void onStopScroll() {
                try {
                    int round = Math.round((((float) mWaveScroll.getScrollX()) * getPlayDuration()) / 1080.0f);
                    mWaveView.setProgress(round);
                    mMediaPlayer.seekTo(round);
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(getString(R.string.start_point, new Object[]{Util.formatShowTime((long) round)}));
                    spannableStringBuilder.setSpan(new ForegroundColorSpan(-26552), 3, 8, 33);
                    mStartPoint.setText(spannableStringBuilder);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onScrollChanged(HorizontalScrollView horizontalScrollView, int i, int i2, int i3, int i4) {
                mWaveView.setOffsetX(Math.max(i, 0));
            }
        });
        mWaveScroll.setScrollEnable(false);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mCutMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cutMusic();
            }
        });
        mEmptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initMusicInfo();
            }
        });
    }

    private void addImageMenu(int selector_crop_done) {
        ((ImageView)findViewById(R.id.toolbar_image_menu)).setImageResource(selector_crop_done);
    }


    private void initMusicInfo() {
        updateView();
    }

    private void updateView() {
        if (mMusic != null) {
            mEmptyView.updateStatus(View.VISIBLE);
            showAll();
            if (NetworkUtil.isNetConnected(this)) {
                String str = mMusic.url;
                if (mMusic.haveCache()) {
                    str = mMusic.getFinalPath();
                }
                try {
                    if (mMediaPlayer != null) {
                        mMediaPlayer.setDataSource(str);
                        mMediaPlayer.prepareAsync();
                        mMediaPlayer.setOnPreparedListener(this);
                        mMediaPlayer.setOnErrorListener(this);
                    }
                } catch (Throwable e) {
                   e.printStackTrace();

                }
            }else{
                mCurrentState = 1;
                mAnimationView.setVisibility(GONE);
                mPlayView.setVisibility(View.VISIBLE);
                ToastUtils.INSTANCE.showToast(R.string.music_load_error);
            }
        }else {
            mEmptyView.updateStatus(1);
        }

    }

    private void showAll() {
        mCutMusic.setVisibility(View.VISIBLE);
        mSongCover.setVisibility(View.VISIBLE);
        mAnimationView.setProgress(1.0f);
        mAnimationView.playAnimation();
        mAnimationView.setVisibility(View.VISIBLE);
        mPlayView.setVisibility(GONE);
        mSongName.setVisibility(View.VISIBLE);
        mSingerName.setVisibility(View.VISIBLE);
        mTotalTime.setVisibility(View.VISIBLE);
        mTip.setVisibility(View.VISIBLE);
        mStartPoint.setVisibility(View.VISIBLE);
        mBg.setVisibility(View.VISIBLE);
        if (mWaveView.getDuration() == 0) {
            mWaveView.setPlayDuration(getPlayDuration());
            mWaveView.setDuration(mMusic.duration * 1000);
        }
        mTotalTime.setText(Util.formatShowTime((long) (mMusic.duration * 1000)));
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(getString(R.string.start_point, new Object[]{Util.formatShowTime(0)}));
        spannableStringBuilder.setSpan(new ForegroundColorSpan(-26552), 3, 8, 33);
        mStartPoint.setText(spannableStringBuilder);
    }

    protected void onResume() {
        super.onResume();
        if (mCurrentState == 3 && !mIsClickPaused) {
            start();
        }
    }

    protected void onPause() {
        super.onPause();
        if (mCurrentState == 2) {
            pause();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        release();
    }

    public void onPrepared(MediaPlayer mediaPlayer) {
        mCurrentState = 4;
        mAnimationView.setVisibility(GONE);
        mPlayView.setVisibility(View.VISIBLE);
        start();
        mWaveScroll.setScrollEnable(true);
    }

    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        mCurrentState = 1;
        mAnimationView.setVisibility(GONE);
        mPlayView.setVisibility(View.VISIBLE);
        ToastUtils.INSTANCE.showToast(R.string.music_load_error);
        return false;
    }

    public void onSeekComplete(MediaPlayer mediaPlayer) {
        if (!mIsClickPaused) {
            start();
        }
    }

    private void start() {
        if (mCurrentState == 3 || mCurrentState == 4) {
            startTimer();
            mMediaPlayer.start();
            mCurrentState = 2;
            mPlayView.setImageResource(R.drawable.selector_music_play_state);
            mAnimator = ObjectAnimator.ofFloat(mSongCover, "rotation", new float[]{mSongCover.getRotation(), mSongCover.getRotation() + 360.0f});
            mAnimator.setDuration(8000);
            mAnimator.setInterpolator(new LinearInterpolator());
            mAnimator.setRepeatCount(-1);
            mAnimator.setRepeatMode(Animation.RESTART);
            mAnimator.start();
        }
    }

    private void pause() {
        if (mCurrentState == 2) {
            mCurrentState = 3;
            cancelTimer();
            mMediaPlayer.pause();
            mPlayView.setImageResource(R.drawable.selector_music_pause_state);
            if (mAnimator != null) {
                mAnimator.cancel();
            }
        }
    }

    private void release() {
        cancelTimer();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
    }

    private void cutMusic() {
        if (!mIsCutting) {
            mIsCutting = true;
            if (mMusic.haveCache()) {
                actionCut();
            } else if (NetworkUtil.isNetConnected(BaseMediaApplication.sContext)) {
                showProgressDialog(getString(R.string.music_downloading));
                mDownloader.downObjectImmediately(mMusic, new DownObjectJob.IDownObjectFinishListener() {
                    @Override
                    public void onDownObjectFinished(IDownloadable iDownloadable, int i) {
                        mIsCutting = false;
                        if (i == 2) {
                            actionCut();
                        } else if (i == 3) {
                            dismissProgressDialog();
                            ToastUtils.INSTANCE.showToast(R.string.music_download_failed);
                        }
                    }
                });
            } else {
                mIsCutting = false;
                ToastUtils.INSTANCE.showToast(R.string.music_download_failed);
            }
        }
    }
    
    private void actionCut() {
        new CutTask().execute();
    }
}
