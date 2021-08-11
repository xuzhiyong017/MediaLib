package com.sky.media.kit.player;

import android.content.Context;
import android.net.Uri;
import android.view.Surface;

import java.io.IOException;

public interface IMediaPlayer {

    public interface OnPreparedListener {
        void onPrepared(IMediaPlayer iMediaPlayer);
    }

    public interface OnCompletionListener {
        void onCompletion(IMediaPlayer iMediaPlayer);
    }

    void setOnCompletionListener(OnCompletionListener onCompletionListener);

    void setOnPreparedListener(OnPreparedListener onPreparedListener);

    int getCurrentPosition();

    int getDuration();

    int getVideoWidth();

    boolean isPlaying();

    void pause() throws IllegalStateException;

    void prepareAsync() throws IllegalStateException;

    void release();

    void seekTo(int i) throws IllegalStateException;

    void setDataSource(Context context, Uri uri) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException;

    void setLooping(boolean z);

    void setSurface(Surface surface);

    void setVolume(float f, float f2);

    void start() throws IllegalStateException;
}
