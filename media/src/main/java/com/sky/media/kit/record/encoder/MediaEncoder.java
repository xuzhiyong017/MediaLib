package com.sky.media.kit.record.encoder;

import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.util.Log;

import com.sky.media.kit.util.CodecUtil;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

public abstract class MediaEncoder implements Runnable {
    private static final String TAG = "MediaEncoder";

    protected static final int TIMEOUT_USEC = 10000;    // 10[msec]

    protected final Object mSync = new Object();

    // 停止录制
    protected final Object mOperation = new Object();
    protected volatile boolean mIsCapturing;
    protected volatile boolean mRequestStop;
    protected boolean mIsEOS;
    protected boolean mMuxerStarted;
    protected int mTrackIndex;
    protected MediaCodec mMediaCodec;
    protected final WeakReference<MediaMuxerWrapper> mWeakMuxer;
    private int mRequestDrain;
    private BufferInfo mBufferInfo;
    private long prevOutputPTSUs = 0;

    abstract void prepare() throws Exception;

    public MediaEncoder(MediaMuxerWrapper mediaMuxerWrapper) {
        this.mWeakMuxer = new WeakReference(mediaMuxerWrapper);
        mediaMuxerWrapper.addEncoder(this);
        synchronized (this.mSync) {
            this.mBufferInfo = new BufferInfo();
            new Thread(this, getClass().getSimpleName()).start();
            try {
                this.mSync.wait();
            } catch (Throwable e) {
                Log.w("MediaEncoder", e);
            }
        }
    }

    public boolean frameAvailableSoon() {
        synchronized (this.mSync) {
            if (!this.mIsCapturing || this.mRequestStop) {
                return false;
            }
            this.mRequestDrain++;
            this.mSync.notifyAll();
            return true;
        }
    }

    public void run() {
        synchronized (this.mSync) {
            this.mRequestStop = false;
            this.mRequestDrain = 0;
            this.mSync.notify();
        }
        final boolean isRunning = true;
        boolean localRequestStop;
        boolean localRequestDrain;
        while (isRunning) {
            synchronized (mSync) {
                localRequestStop = mRequestStop;
                localRequestDrain = (mRequestDrain > 0);
                if (localRequestDrain) {
                    mRequestDrain--;
                }
            }
            // 停止
            if (localRequestStop) {
                synchronized (mOperation) {
                    drain();
                    // request stop recording
                    signalEndOfInputStream();
                    // process output data again for EOS signale
                    drain();
                    // 通知最后一帧写入完成，进入释放阶段
                    mOperation.notifyAll();
                    // release all related objects
                    release();
                }
                break;
            }

            // 录制
            if (localRequestDrain) {
                drain();
            } else {
                synchronized (mSync) {
                    try {
                        mSync.wait();
                    } catch (final InterruptedException e) {
                        break;
                    }
                }
            }
        }

        synchronized (mSync) {
            mRequestStop = true;
            mIsCapturing = false;
        }
    }

    protected void signalEndOfInputStream() {
        encode(null, 0, getPTSUs());
    }


    protected void startRecording() {
        synchronized (this.mSync) {
            this.mIsCapturing = true;
            this.mRequestStop = false;
            this.mSync.notifyAll();
        }
    }

    protected void stopRecording() {
        synchronized (mSync) {
            if (!mIsCapturing || mRequestStop) {
                return;
            }
            mRequestStop = true;    // for rejecting newer frame
            mSync.notifyAll();
            // We can not know when the encoding and writing finish.
            // so we return immediately after request to avoid delay of caller thread
        }

        // 等待录制最后一帧完成
        synchronized (mOperation) {
            try {
                mOperation.wait();
            } catch (InterruptedException e) {

            }
        }
    }

    protected void release() {
        this.mIsCapturing = false;
        if (this.mMediaCodec != null) {
            try {
                this.mMediaCodec.stop();
                this.mMediaCodec.release();
                this.mMediaCodec = null;
            } catch (Throwable e) {
                Log.w("MediaEncoder", e);
            }
        }
        if (mMuxerStarted) {
            final MediaMuxerWrapper muxer = mWeakMuxer != null ? mWeakMuxer.get() : null;
            if (muxer != null) {
                try {
                    muxer.stop();
                } catch (final Exception e) {
                    Log.e(TAG, "failed stopping muxer", e);
                }
            }
        }
        this.mBufferInfo = null;
    }

    protected void encode(byte[] buffer, int length, long presentationTimeUs) {
        if (!mIsCapturing) return;
        final ByteBuffer[] inputBuffers = mMediaCodec.getInputBuffers();
        while (mIsCapturing) {
            final int inputBufferIndex = mMediaCodec.dequeueInputBuffer(TIMEOUT_USEC);
            if (inputBufferIndex >= 0) {
                final ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                inputBuffer.clear();
                if (buffer != null) {
                    inputBuffer.put(buffer);
                }
                if (length <= 0) {
                    // send EOS
                    mIsEOS = true;
                    mMediaCodec.queueInputBuffer(inputBufferIndex, 0, 0,
                            presentationTimeUs, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    break;
                } else {
                    mMediaCodec.queueInputBuffer(inputBufferIndex, 0, length,
                            presentationTimeUs, 0);
                }
                break;
            } else if (inputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                // wait for MediaCodec encoder is ready to encode
                // nothing to do here because MediaCodec#dequeueInputBuffer(TIMEOUT_USEC)
                // will wait for maximum TIMEOUT_USEC(10msec) on each call
            }
        }
    }

    protected void drain() {
        if (this.mMediaCodec != null) {
            MediaMuxerWrapper mediaMuxerWrapper = (MediaMuxerWrapper) this.mWeakMuxer.get();
            if (mediaMuxerWrapper != null) {
                int count = 0;
                LOOP:
                while (this.mIsCapturing) {
                    int encoderStatus = this.mMediaCodec.dequeueOutputBuffer(this.mBufferInfo, TIMEOUT_USEC);
                    if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                        // wait 5 counts(=TIMEOUT_USEC x 5 = 50msec) until data/EOS come
                        if (!mIsEOS) {
                            if (++count > 5)
                                break LOOP;        // out of while
                        }
                    } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                        continue;
                    } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        if (this.mMuxerStarted) {
                            throw new RuntimeException("format changed twice");
                        }
                        this.mTrackIndex = mediaMuxerWrapper.addTrack(this.mMediaCodec.getOutputFormat());
                        this.mMuxerStarted = true;
                        if (!mediaMuxerWrapper.start()) {
                            // we should wait until muxer is ready
                            // 等待复用器准备完成
                            synchronized (mediaMuxerWrapper) {
                                while (!mediaMuxerWrapper.isStarted())
                                    try {
                                        mediaMuxerWrapper.wait(100);
                                    } catch (final InterruptedException e) {
                                        break LOOP;
                                    }
                            }
                        }
                    } else if (encoderStatus >= 0) {
                        ByteBuffer encodedData = CodecUtil.getOutputBuffer(this.mMediaCodec, encoderStatus);
                        if (encodedData == null) {
                            throw new RuntimeException("OutputBuffer " + encoderStatus + " was null");
                        }
                        if ((this.mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                            this.mBufferInfo.size = 0;
                        }
                        if (this.mBufferInfo.size != 0) {
                            if (this.mMuxerStarted) {
                                this.mBufferInfo.presentationTimeUs = getPTSUs();
                                mediaMuxerWrapper.writeSampleData(this.mTrackIndex, encodedData, this.mBufferInfo);
                                this.prevOutputPTSUs = this.mBufferInfo.presentationTimeUs;
                                count = 0;
                            } else {
                                throw new RuntimeException("drain:muxer hasn't started");
                            }
                        }
                        this.mMediaCodec.releaseOutputBuffer(encoderStatus, false);
                        if ((this.mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                            this.mIsCapturing = false;
                            return;
                        }
                    } else {
                        continue;
                    }
                }
            }
        }
    }

    protected long getPTSUs() {
        long nanoTime = System.nanoTime() / 1000;
        if (nanoTime < this.prevOutputPTSUs) {
            return nanoTime + (this.prevOutputPTSUs - nanoTime);
        }
        return nanoTime;
    }
}
