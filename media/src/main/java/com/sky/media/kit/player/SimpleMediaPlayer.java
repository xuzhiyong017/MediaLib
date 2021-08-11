package com.sky.media.kit.player;

import android.media.MediaPlayer;

public class SimpleMediaPlayer extends MediaPlayer implements IMediaPlayer {

    public void setOnPreparedListener(final IMediaPlayer.OnPreparedListener onPreparedListener) {
        setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mediaPlayer) {
                onPreparedListener.onPrepared(SimpleMediaPlayer.this);
            }
        });
    }

    public void setOnCompletionListener(final IMediaPlayer.OnCompletionListener onCompletionListener) {
        setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
                onCompletionListener.onCompletion(SimpleMediaPlayer.this);
            }
        });
    }
}
