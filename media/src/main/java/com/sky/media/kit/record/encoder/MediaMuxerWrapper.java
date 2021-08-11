package com.sky.media.kit.record.encoder;

import android.media.MediaCodec.BufferInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;

import java.io.IOException;
import java.nio.ByteBuffer;

class MediaMuxerWrapper {
    private final MediaMuxer mMediaMuxer;
    private MediaEncoder mVideoEncoder;
    private MediaEncoder mAudioEncoder;
    private int mEncoderCount = 0;
    private int mStartedCount = 0;
    private boolean mIsStarted = false;

    MediaMuxerWrapper(String str) throws IOException {
        this.mMediaMuxer = new MediaMuxer(str, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        mEncoderCount = mStartedCount = 0;
        mIsStarted = false;
    }

    void prepare() throws Exception {
        if (this.mVideoEncoder != null) {
            this.mVideoEncoder.prepare();
        }
        if (this.mAudioEncoder != null) {
            this.mAudioEncoder.prepare();
        }
    }

    void startRecording() {
        if (this.mVideoEncoder != null) {
            this.mVideoEncoder.startRecording();
        }
        if (this.mAudioEncoder != null) {
            this.mAudioEncoder.startRecording();
        }
    }

    void stopRecording() {
        if (this.mVideoEncoder != null) {
            this.mVideoEncoder.stopRecording();
            this.mVideoEncoder = null;
        }
        if (this.mAudioEncoder != null) {
            this.mAudioEncoder.stopRecording();
            this.mAudioEncoder = null;
        }
    }

    synchronized boolean isStarted() {
        return this.mIsStarted;
    }

    void addEncoder(MediaEncoder encoder) {
        if (encoder instanceof MediaVideoEncoder) {
            if (mVideoEncoder != null)
                throw new IllegalArgumentException("Video encoder already added.");
            mVideoEncoder = encoder;
        } else if (encoder instanceof MediaAudioEncoder) {
            if (mAudioEncoder != null)
                throw new IllegalArgumentException("Video encoder already added.");
            mAudioEncoder = encoder;
        } else
            throw new IllegalArgumentException("unsupported encoder");
        mEncoderCount = (mVideoEncoder != null ? 1 : 0) + (mAudioEncoder != null ? 1 : 0);
    }

    synchronized boolean start() {
        this.mStartedCount++;
        if (this.mEncoderCount > 0 && this.mStartedCount == this.mEncoderCount) {
            this.mMediaMuxer.start();
            this.mIsStarted = true;
            notifyAll();
        }
        return this.mIsStarted;
    }

    synchronized void stop() {
        this.mStartedCount--;
        if (this.mEncoderCount > 0 && this.mStartedCount <= 0) {
            this.mMediaMuxer.stop();
            this.mMediaMuxer.release();
            this.mIsStarted = false;
        }
    }

    synchronized int addTrack(MediaFormat mediaFormat) {
        if (this.mIsStarted) {
            throw new IllegalStateException("muxer already started");
        }
        return this.mMediaMuxer.addTrack(mediaFormat);
    }

    synchronized void writeSampleData(int trackIndex, ByteBuffer byteBuffer, BufferInfo bufferInfo) {
        if (this.mStartedCount > 0) {
            this.mMediaMuxer.writeSampleData(trackIndex, byteBuffer, bufferInfo);
        }
    }
}
