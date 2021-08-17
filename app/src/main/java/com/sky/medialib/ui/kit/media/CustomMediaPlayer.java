package com.sky.medialib.ui.kit.media;

import android.content.Context;
import android.net.Uri;
import android.view.Surface;


import com.sky.media.kit.player.IMediaPlayer;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class CustomMediaPlayer implements IMediaPlayer {

    private IjkMediaPlayer exoMediaPlayer;

    public CustomMediaPlayer(Context context) {
        this.exoMediaPlayer = new IjkMediaPlayer();
    }

    public void setDataSource(Context context, Uri uri) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        this.exoMediaPlayer.setDataSource(context, uri);
    }

    public void prepareAsync() throws IllegalStateException {
        this.exoMediaPlayer.prepareAsync();
    }

    public void start() throws IllegalStateException {
        this.exoMediaPlayer.start();
    }

    public void pause() throws IllegalStateException {
        this.exoMediaPlayer.pause();
    }

    public int getVideoWidth() {
        return this.exoMediaPlayer.getVideoWidth();
    }

    public boolean isPlaying() {
        return this.exoMediaPlayer.isPlaying();
    }

    public void seekTo(int i) throws IllegalStateException {
        this.exoMediaPlayer.seekTo(i);
    }

    public int getCurrentPosition() {
        return (int) this.exoMediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return (int) this.exoMediaPlayer.getDuration();
    }

    public void release() {
        this.exoMediaPlayer.release();
    }

    public void setVolume(float f, float f2) {
        this.exoMediaPlayer.setVolume(f, f2);
    }

    public void setOnPreparedListener(final OnPreparedListener onPreparedListener) {
        this.exoMediaPlayer.setOnPreparedListener(new tv.danmaku.ijk.media.player.IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(tv.danmaku.ijk.media.player.IMediaPlayer iMediaPlayer) {
                onPreparedListener.onPrepared(CustomMediaPlayer.this);
            }
        });
    }

    public void setOnCompletionListener(final OnCompletionListener onCompletionListener) {
        this.exoMediaPlayer.setOnCompletionListener(new tv.danmaku.ijk.media.player.IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(tv.danmaku.ijk.media.player.IMediaPlayer iMediaPlayer) {
                onCompletionListener.onCompletion(CustomMediaPlayer.this);
            }
        });
    }

    public void setLooping(boolean z) {
        this.exoMediaPlayer.setLooping(z);
    }

    public void setSurface(Surface surface) {
        this.exoMediaPlayer.setSurface(surface);
    }
}
