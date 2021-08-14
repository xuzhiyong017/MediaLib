package com.sky.medialib.ui.kit.common.view;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;

import java.io.IOException;

public class TextureVideoView extends TextureView implements SurfaceTextureListener {

    private MediaPlayer mMediaPlayer;

    private PlayerState mPlayerStatus;

    private volatile boolean hasSetDataSource;

    private volatile boolean surfaceReady;

    private volatile boolean isPrepared;

    private volatile boolean hasPlay;

    private float width;

    private float height;

    private float mRatote = 0.0f;

    private ScaleType scaleType;

    private OnVideoSizeChangedListener onVideoSizeChange;

    private OnCompletionListener mOnCompletionListener;

    private OnPreparedListener mOnPrepareListener;

    private OnErrorListener mOnErrorListener;

    public enum ScaleType {
        CENTER_CROP,
        FILL,
        CENTER
    }

    public enum PlayerState {
        UNINITIALIZED,
        PLAY,
        STOP,
        PAUSE,
        END
    }

    public TextureVideoView(Context context) {
        super(context);
        init();
    }

    public TextureVideoView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public TextureVideoView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void init() {
        createMediaPlayer();
        setScaleType(ScaleType.CENTER_CROP);
        setRotation(0.0f);
        setSurfaceTextureListener(this);
    }

    private void createMediaPlayer() {
        if (this.mMediaPlayer == null) {
            this.mMediaPlayer = new MediaPlayer();
        } else {
            this.mMediaPlayer.reset();
        }
        this.isPrepared = false;
        this.hasPlay = false;
        this.mPlayerStatus = PlayerState.UNINITIALIZED;
    }

    public void setDataSource(String str) {
        createMediaPlayer();
        try {
            this.mMediaPlayer.setDataSource(str);
            this.hasSetDataSource = true;
            prepare();
        } catch (IOException e) {
            log(e.getMessage());
        }
    }

    public void setDataSource(Context context, Uri uri) throws IOException {
        createMediaPlayer();
        this.mMediaPlayer.setDataSource(context, uri);
        this.hasSetDataSource = true;
        prepare();
    }

    public void setDataSource(AssetFileDescriptor assetFileDescriptor) {
        createMediaPlayer();
        try {
            this.mMediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
            this.hasSetDataSource = true;
            prepare();
        } catch (IOException e) {
            log(e.getMessage());
        }
    }

    public long getCurrentPosition() {
        return this.mMediaPlayer != null ? (long) this.mMediaPlayer.getCurrentPosition() : 0;
    }

    public boolean isPlaying() {
        return this.mMediaPlayer != null && this.mMediaPlayer.isPlaying();
    }


    private void prepare() {
        try {
            this.mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    TextureVideoView.this.onPrepared(mp);
                }
            });
            this.mMediaPlayer.setOnVideoSizeChangedListener(new OnVideoSizeChangedListener() {
                @Override
                public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                    TextureVideoView.this.onVideoSizeChanged(mp,width,height);
                }
            });
            this.mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                }
            });
            this.mMediaPlayer.setOnErrorListener(new OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    return onMediaError(mp,what,extra);
                }
            });
            this.mMediaPlayer.prepareAsync();
        } catch (IllegalArgumentException e) {
            log(e.getMessage());
        } catch (SecurityException e2) {
            log(e2.getMessage());
        } catch (IllegalStateException e3) {
            log(e3.toString());
        }
    }

   void onPrepared(MediaPlayer mediaPlayer) {
        this.isPrepared = true;
        if (this.hasPlay && this.surfaceReady) {
            play();
        }
        if (this.mOnPrepareListener != null) {
            this.mOnPrepareListener.onPrepared(mediaPlayer);
        }
    }


   void onVideoSizeChanged(MediaPlayer mediaPlayer, int width, int height) {
        this.width = (float) width;
        this.height = (float) height;
        adjustSize();
        if (this.onVideoSizeChange != null) {
            this.onVideoSizeChange.onVideoSizeChanged(mediaPlayer, width, height);
        }
    }

     boolean onMediaError(MediaPlayer mediaPlayer, int i, int i2) {
        this.mPlayerStatus = PlayerState.END;
        this.mMediaPlayer.stop();
        this.mMediaPlayer.reset();
        if (this.mOnErrorListener != null) {
            this.mOnErrorListener.onError(mediaPlayer, i, i2);
        }
        return true;
    }

    public void play() {
        if (this.hasSetDataSource) {
            this.hasPlay = true;
            if (!this.isPrepared) {
                log("play() was called but video is not prepared yet, waiting.");
                return;
            } else if (!this.surfaceReady) {
                log("play() was called but view is not available yet, waiting.");
                return;
            } else if (this.mPlayerStatus == PlayerState.PLAY) {
                log("play() was called but video is already playing.");
                return;
            } else if (this.mPlayerStatus == PlayerState.PAUSE) {
                log("play() was called but video is paused, resuming.");
                this.mPlayerStatus = PlayerState.PLAY;
                this.mMediaPlayer.start();
                return;
            } else if (this.mPlayerStatus == PlayerState.END || this.mPlayerStatus == PlayerState.STOP) {
                log("play() was called but video already ended, starting over.");
                this.mPlayerStatus = PlayerState.PLAY;
                this.mMediaPlayer.seekTo(0);
                this.mMediaPlayer.start();
                return;
            } else {
                this.mPlayerStatus = PlayerState.PLAY;
                this.mMediaPlayer.start();
                return;
            }
        }
        log("play() was called but data source was not set.");
    }

    public void pause() {
        if (this.mPlayerStatus == PlayerState.PAUSE) {
            log("pause() was called but video already paused.");
        } else if (this.mPlayerStatus == PlayerState.STOP) {
            log("pause() was called but video already stopped.");
        } else if (this.mPlayerStatus == PlayerState.END) {
            log("pause() was called but video already ended.");
        } else {
            this.mPlayerStatus = PlayerState.PAUSE;
            if (this.mMediaPlayer.isPlaying()) {
                this.mMediaPlayer.pause();
            }
        }
    }

    public void stop() {
        if (this.mPlayerStatus == PlayerState.STOP) {
            log("stop() was called but video already stopped.");
        } else if (this.mPlayerStatus == PlayerState.END) {
            log("stop() was called but video already ended.");
        } else {
            this.mPlayerStatus = PlayerState.STOP;
            if (this.mMediaPlayer.isPlaying()) {
                this.mMediaPlayer.pause();
                this.mMediaPlayer.seekTo(0);
            }
        }
    }

    public void release() {
        if (this.mPlayerStatus == PlayerState.UNINITIALIZED) {
            log("release() was called but video uninitialized.");
            return;
        }
        if (this.mPlayerStatus == PlayerState.PLAY || this.mPlayerStatus == PlayerState.PAUSE) {
            stop();
        }
        this.mPlayerStatus = PlayerState.UNINITIALIZED;
        if (this.mMediaPlayer != null) {
            this.mMediaPlayer.release();
            this.mMediaPlayer = null;
        }
    }

    public void setLooping(boolean looping) {
        if (this.mMediaPlayer != null) {
            this.mMediaPlayer.setLooping(looping);
        }
    }

    public void seekTo(int i) {
        if (this.mMediaPlayer != null) {
            this.mMediaPlayer.seekTo(i);
        }
    }

    public int getDuration() {
        if (this.mMediaPlayer != null) {
            return this.mMediaPlayer.getDuration();
        }
        return 0;
    }

    private void log(String str) {
        Log.d("TextureVideoView", str);
    }

    public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener onVideoSizeChangedListener) {
        this.onVideoSizeChange = onVideoSizeChangedListener;
    }

    public void setOnCompletionListener(OnCompletionListener onCompletionListener) {
        this.mOnCompletionListener = onCompletionListener;
    }

    public void setOnPreparedListener(OnPreparedListener onPreparedListener) {
        this.mOnPrepareListener = onPreparedListener;
    }

    public void setOnErrorListener(OnErrorListener onErrorListener) {
        this.mOnErrorListener = onErrorListener;
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
        this.mMediaPlayer.setSurface(new Surface(surfaceTexture));
        this.surfaceReady = true;
        if (this.hasSetDataSource && this.hasPlay && this.isPrepared) {
            log("View is available and play() was called.");
            play();
        }
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
        adjustSize();
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    public void setRotation(float f) {
        this.mRatote = f;
        adjustSize();
    }

    public void setScaleType(ScaleType scaleType) {
        this.scaleType = scaleType;
        adjustSize();
    }

    private void adjustSize() {
        if (this.width > 0.0f && this.height > 0.0f) {
            int measuredWidth = getMeasuredWidth();
            int measuredHeight = getMeasuredHeight();
            Matrix a = adjustMatrix((float) measuredWidth, (float) measuredHeight, this.width, this.height, this.scaleType);
            a.postRotate(this.mRatote, (float) (measuredWidth / 2), (float) (measuredHeight / 2));
            setTransform(a);
        }
    }


    private float getSlideRatio(float f, float f2) {
        return f > f2 ? f / f2 : f2 / f;
    }

    private float getWHRatio(float f, float f2) {
        return f / f2;
    }

    private Matrix scaleMatrix(float f, float f2, float f3, float f4) {
        Matrix matrix = new Matrix();
        matrix.setScale(f, f2, f3 / 2.0f, f4 / 2.0f);
        return matrix;
    }

    private Matrix adjustMatrix(float f, float f2, float f3, float f4, ScaleType scaleType) {
        float a = getSlideRatio(f, f3);
        float b = getSlideRatio(f2, f4);
        float c = getWHRatio(f3, f4);
        switch (scaleType) {
            case CENTER_CROP:
                return scaleMatrix(centerCropX(f, f2, f3, f4, a, b, c), centerCropY(f, f2, f3, f4, a, b, c), f, f2);
            case CENTER:
                return scaleMatrix(centerX(f, f2, f3, f4, a, b, c), centerY(f, f2, f3, f4, a, b, c), f, f2);
            default:
                return new Matrix();
        }
    }

    private float centerX(float f, float f2, float f3, float f4, float f5, float f6, float f7) {
        if (f < f3) {
            if (f2 >= f4 || f5 > f6) {
                return 1.0f;
            }
            return (f2 * f7) / f;
        } else if (f2 < f4) {
            return (f2 * f7) / f;
        } else {
            if (f5 >= f6) {
                return (f2 * f7) / f;
            }
            return 1.0f;
        }
    }

    private float centerY(float f, float f2, float f3, float f4, float f5, float f6, float f7) {
        if (f2 < f4) {
            if (f >= f3 || f6 > f5) {
                return 1.0f;
            }
            return (f / f7) / f2;
        } else if (f < f3) {
            return (f / f7) / f2;
        } else {
            if (f6 > f5) {
                return (f / f7) / f2;
            }
            return 1.0f;
        }
    }

    private float centerCropX(float f, float f2, float f3, float f4, float f5, float f6, float f7) {
        if (f < f3) {
            if (f2 >= f4) {
                return (f2 * f7) / f;
            }
            if (f5 > f6) {
                return (f2 * f7) / f;
            }
            return 1.0f;
        } else if (f2 < f4 || f5 >= f6) {
            return 1.0f;
        } else {
            return (f2 * f7) / f;
        }
    }

    private float centerCropY(float f, float f2, float f3, float f4, float f5, float f6, float f7) {
        if (f2 < f4) {
            if (f >= f3) {
                return (f / f7) / f2;
            }
            if (f6 > f5) {
                return (f / f7) / f2;
            }
            return 1.0f;
        } else if (f < f3 || f6 > f5) {
            return 1.0f;
        } else {
            return (f / f7) / f2;
        }
    }
}
