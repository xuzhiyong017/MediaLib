package com.sky.medialib.ui.editvideo.segment;

import android.net.Uri;
import android.os.Bundle;

import com.blankj.utilcode.util.ScreenUtils;
import com.sky.media.kit.base.BaseActivity;
import com.sky.media.kit.player.IMediaPlayer;
import com.sky.media.kit.video.VideoInput;
import com.sky.media.kit.video.VideoProcess;
import com.sky.medialib.ui.editvideo.segment.entity.VideoEditData;
import com.sky.medialib.ui.editvideo.segment.event.ControlVideoEvent;
import com.sky.medialib.ui.editvideo.segment.proto.MusicPlayerProtocol;
import com.sky.medialib.ui.editvideo.segment.proto.VideoPlayerProtocol;
import com.sky.medialib.ui.kit.common.base.AppActivity;
import com.sky.medialib.util.EventBusHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

public class VideoPlaySegment extends BaseSegment<VideoEditData> implements VideoPlayerProtocol {

    private Timer timer;
    private MusicPlayerProtocol musicPlayerProtocol;

    class VideoTimerTask extends TimerTask {
        VideoTimerTask() {
        }

        public void run() {
           activity.runOnUiThread(new Runnable() {
               @Override
               public void run() {
                   try {
                       IMediaPlayer a = ((VideoInput) ((VideoEditData)mData).processExt.getMInput()).getMediaPlayer();
                       if (a != null) {
                           ((VideoEditData)mData).processExt.processVideo((long) (a.getCurrentPosition() * 1000));
                       }
                   } catch (Throwable e) {
                       e.printStackTrace();
                   }
               }
           });
        }

    }

    public VideoPlaySegment(AppActivity baseActivity, VideoEditData videoEditData) {
        super(baseActivity, videoEditData);
    }

    public void setMusicPlayerProtocol(MusicPlayerProtocol musicPlayerProtocol) {
        this.musicPlayerProtocol = musicPlayerProtocol;
    }

    public void onResume() {
        super.onResume();
        onDealResume();
    }

    public void onPause() {
        super.onPause();
        onDealPause();
    }

    public void initPlayer() {
        cancelTimer();
        VideoProcess.MediaPlayerBuilder mediaPlayerBuilder = new VideoProcess.MediaPlayerBuilder();
        mediaPlayerBuilder.setPreviewSize(ScreenUtils.getScreenWidth(), ScreenUtils.getScreenHeight());
        mediaPlayerBuilder.setLooping(true);
        mediaPlayerBuilder.setNeedPlay(!((VideoEditData) this.mData).needPlay);
        mediaPlayerBuilder.volume(((VideoEditData) this.mData).isKeepVoice() ? 1.0f : 0.0f);
        mediaPlayerBuilder.setOnPreparedListener((IMediaPlayer.OnPreparedListener) iMediaPlayer -> {

        });
        if (((VideoEditData) this.mData).isReverse()) {
            ((VideoEditData) this.mData).processExt.initMediaPlayer(Uri.parse(((VideoEditData) this.mData).getVideoReversePath()), mediaPlayerBuilder);
        } else {
            ((VideoEditData) this.mData).processExt.initMediaPlayer(Uri.parse(((VideoEditData) this.mData).getVideoPath()), mediaPlayerBuilder);
        }
    }

    private void startTimer() {
        if (this.timer != null) {
            this.timer.cancel();
        }
        this.timer = new Timer();
        this.timer.schedule(new VideoTimerTask(), 0, 10);
    }

    private void cancelTimer() {
        if (this.timer != null) {
            this.timer.cancel();
        }
        this.timer = null;
    }

    public void onDealResume() {
        if (((VideoEditData) this.mData).needPlay && !((VideoEditData) this.mData).isShare) {
            this.musicPlayerProtocol.startMusicPlay();
            ((VideoEditData) this.mData).processExt.startPlay();
            ((VideoEditData) this.mData).needPlay = false;
        }
        startTimer();
    }

    public void onDealPause() {
        cancelTimer();
        if (!((VideoEditData) this.mData).jumpInputActivity) {
            this.musicPlayerProtocol.stopMusicPlay();
            ((VideoEditData) this.mData).processExt.pauseVideo();
            ((VideoEditData) this.mData).needPlay = true;
        }
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        EventBusHelper.register(this);
    }

    public void onDestroy() {
        super.onDestroy();
        EventBusHelper.unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ControlVideoEvent controlVideoEvent) {
        if (controlVideoEvent.mPause) {
            cancelTimer();
            this.musicPlayerProtocol.stopMusicPlay();
            ((VideoEditData) this.mData).processExt.pauseVideo();
            ((VideoEditData) this.mData).needPlay = true;
        }else{
            onDealResume();
        }

    }
}
