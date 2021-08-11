package com.sky.medialib.ui.camera.helper;

import android.text.TextUtils;


import com.sky.medialib.ui.kit.media.MediaKitExt;
import com.sky.medialib.ui.kit.model.Music;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class CameraAudioHelper {
    //变速类型
    private int shootType = 3;
    private String mMusicPath;
    private IjkMediaPlayer mPlayer;
    private Music mMusic;

    public void setMusic(Music music) {
        this.mMusic = music;
    }

    public Music getMusic() {
        return this.mMusic;
    }

    public void setMusicPath(String str) {
        this.mMusicPath = str;
    }

    public void setShootType(int shootType) {
        this.shootType = shootType;
    }

    public IjkMediaPlayer getMediaPlayer() {
        return this.mPlayer;
    }

    public void seekTo(long time) {
        if (this.mPlayer != null) {
            this.mPlayer.setVolume(0.0f, 0.0f);
            this.mPlayer.setOnSeekCompleteListener(new IMediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(IMediaPlayer iMediaPlayer) {
                    mPlayer.setVolume(1.0f, 1.0f);
                }
            });
           mPlayer.seekTo(time);
        }
    }

    private void prepare() {
        if (!TextUtils.isEmpty(this.mMusicPath)) {
            try {
                if (this.mPlayer == null) {
                    this.mPlayer = new IjkMediaPlayer();
                } else {
                    this.mPlayer.reset();
                }
                this.mPlayer.setDataSource(this.mMusicPath);
                this.mPlayer.setOption(4, "soundtouch", 1);
                this.mPlayer.setSpeed(1.0f / MediaKitExt.getSpeedByType(this.shootType));
                this.mPlayer.setLooping(true);
                this.mPlayer.setVolume(1.0f, 1.0f);
                this.mPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(IMediaPlayer iMediaPlayer) {
                        iMediaPlayer.start();
                    }
                });
                this.mPlayer.prepareAsync();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public void startPlayer() {
        if (this.mPlayer == null || TextUtils.isEmpty(this.mMusicPath) || !this.mMusicPath.equals(this.mPlayer.getDataSource()) || this.mPlayer.isPlaying()) {
            prepare();
        } else {
            this.mPlayer.start();
        }
    }

    public void pause() {
        if (this.mPlayer != null && this.mPlayer.isPlaying()) {
            this.mPlayer.pause();
        }
    }

    public void release() {
        if (this.mPlayer != null) {
            this.mPlayer.release();
            this.mPlayer = null;
        }
    }
}
