package com.sky.media.kit.transfer;

import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaExtractor;


import java.io.IOException;
import java.nio.ByteBuffer;

public class VideoTrackTranscoder {
    private final MediaExtractor mediaExtractor;
    private final int trackIndex;
    private final android.media.MediaFormat videoFormat;
    private final QueuedMuxer queuedMuxer;
    private final BufferInfo bufferInfo;
    private MediaCodec mDecodeCodec;
    private MediaCodec mEncoderCodec;
    private ByteBuffer[] inputBuffers;
    private ByteBuffer[] outputBuffers;
    private android.media.MediaFormat outputFormat;
    private OutputSurface outputSurface;
    private InputSurface inputSurface;
    private boolean f12183m;
    private boolean isdecoderEndOfStream;
    private boolean endOfSteam;
    private boolean decodeCodesStarted;
    private boolean encoderCodecStarted;
    private long presentationTimeUs;
    private OutputSurface.OnProcessCallback onProcessCallback;

    public VideoTrackTranscoder(MediaExtractor mediaExtractor, int i, android.media.MediaFormat mediaFormat, QueuedMuxer queuedMuxer) {
        this(mediaExtractor, i, mediaFormat, queuedMuxer, null);
    }

    public VideoTrackTranscoder(MediaExtractor mediaExtractor, int i, android.media.MediaFormat mediaFormat, QueuedMuxer queuedMuxer, OutputSurface.OnProcessCallback onProcessCallback) {
        this.bufferInfo = new BufferInfo();
        this.mediaExtractor = mediaExtractor;
        this.trackIndex = i;
        this.videoFormat = mediaFormat;
        this.queuedMuxer = queuedMuxer;
        if (onProcessCallback != null) {
            this.onProcessCallback = onProcessCallback;
        } else {
            this.onProcessCallback = new TextureRender();
        }
    }

    public void setRotation(int i) {
        if (this.onProcessCallback instanceof TextureRender) {
            ((TextureRender) this.onProcessCallback).setRotation(i);
        }
    }

    public void startCodec() throws IOException {
        this.mediaExtractor.selectTrack(this.trackIndex);
        this.mEncoderCodec = MediaCodec.createEncoderByType(this.videoFormat.getString("mime"));
        this.mEncoderCodec.configure(this.videoFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        this.inputSurface = new InputSurface(this.mEncoderCodec.createInputSurface());
        this.inputSurface.eglMakeCurrent();
        this.mEncoderCodec.start();
        this.encoderCodecStarted = true;
        this.outputBuffers = this.mEncoderCodec.getOutputBuffers();
        android.media.MediaFormat trackFormat = this.mediaExtractor.getTrackFormat(this.trackIndex);
        if (trackFormat.containsKey("rotation-degrees")) {
            trackFormat.setInteger("rotation-degrees", 0);
        }
        this.outputSurface = new OutputSurface(this.onProcessCallback);
        this.mDecodeCodec = MediaCodec.createDecoderByType(trackFormat.getString("mime"));
        this.mDecodeCodec.configure(trackFormat, this.outputSurface.getSurface(), null, 0);
        this.mDecodeCodec.start();
        this.decodeCodesStarted = true;
        this.inputBuffers = this.mDecodeCodec.getInputBuffers();
    }

    public boolean transferData() {
        boolean z = false;
        while (encodeData(0) != 0) {
            z = true;
        }
        int b;
        do {
            b = dequeueOutputBuffer(0);
            if (b != 0) {
                z = true;
                continue;
            }
        } while (b == 1);
        while (dequeueInputBuffer(0) != 0) {
            z = true;
        }
        return z;
    }

    public boolean isEndOfStream() {
        return this.endOfSteam;
    }

    public void release() {
        if (this.outputSurface != null) {
            this.outputSurface.release();
            this.outputSurface = null;
        }
        if (this.inputSurface != null) {
            this.inputSurface.release();
            this.inputSurface = null;
        }
        if (this.mDecodeCodec != null) {
            if (this.decodeCodesStarted) {
                this.mDecodeCodec.stop();
            }
            this.mDecodeCodec.release();
            this.mDecodeCodec = null;
        }
        if (this.mEncoderCodec != null) {
            if (this.encoderCodecStarted) {
                this.mEncoderCodec.stop();
            }
            this.mEncoderCodec.release();
            this.mEncoderCodec = null;
        }
    }

    private int dequeueInputBuffer(long j) {
        if (this.f12183m) {
            return 0;
        }
        int sampleTrackIndex = this.mediaExtractor.getSampleTrackIndex();
        if (sampleTrackIndex >= 0 && sampleTrackIndex != this.trackIndex) {
            return 0;
        }
        int dequeueInputBuffer = this.mDecodeCodec.dequeueInputBuffer(j);
        if (dequeueInputBuffer < 0) {
            return 0;
        }
        if (sampleTrackIndex < 0) {
            this.f12183m = true;
            this.mDecodeCodec.queueInputBuffer(dequeueInputBuffer, 0, 0, 0, 4);
            return 0;
        }
        boolean z;
        int i;
        int readSampleData = this.mediaExtractor.readSampleData(this.inputBuffers[dequeueInputBuffer], 0);
        if ((this.mediaExtractor.getSampleFlags() & 1) != 0) {
            z = true;
        } else {
            z = false;
        }
        MediaCodec mediaCodec = this.mDecodeCodec;
        long sampleTime = this.mediaExtractor.getSampleTime();
        if (z) {
            i = 1;
        } else {
            i = 0;
        }
        mediaCodec.queueInputBuffer(dequeueInputBuffer, 0, readSampleData, sampleTime, i);
        this.mediaExtractor.advance();
        return 2;
    }

    private int dequeueOutputBuffer(long j) {
        boolean z = true;
        if (this.isdecoderEndOfStream) {
            return 0;
        }
        int dequeueOutputBuffer = this.mDecodeCodec.dequeueOutputBuffer(this.bufferInfo, j);
        switch (dequeueOutputBuffer) {
            case -3:
            case -2:
                return 1;
            case -1:
                return 0;
            default:
                if ((this.bufferInfo.flags & 4) != 0) {
                    this.mEncoderCodec.signalEndOfInputStream();
                    this.isdecoderEndOfStream = true;
                    this.bufferInfo.size = 0;
                }
                if (this.bufferInfo.size <= 0) {
                    z = false;
                }
                this.mDecodeCodec.releaseOutputBuffer(dequeueOutputBuffer, z);
                if (z) {
                    this.outputSurface.updateTexImage();
                    this.outputSurface.processVideo(this.bufferInfo.presentationTimeUs);
                    this.inputSurface.eglPresentationTimeANDROID(this.bufferInfo.presentationTimeUs * 1000);
                    this.inputSurface.eglSwap();
                }
                return 2;
        }
    }

    private int encodeData(long j) {
        if (this.endOfSteam) {
            return 0;
        }
        int dequeueOutputBuffer = this.mEncoderCodec.dequeueOutputBuffer(this.bufferInfo, j);
        switch (dequeueOutputBuffer) {
            case -3:
                this.outputBuffers = this.mEncoderCodec.getOutputBuffers();
                return 1;
            case -2:
                if (this.outputFormat != null) {
                    throw new RuntimeException("Video output bitspersample changed twice.");
                }
                this.outputFormat = this.mEncoderCodec.getOutputFormat();
                this.queuedMuxer.executeTask(QueuedMuxer.MediaType.VIDEO, this.outputFormat);
                return 1;
            case -1:
                return 0;
            default:
                if (this.outputFormat == null) {
                    throw new RuntimeException("Could not determine actual output bitspersample.");
                }
                if ((this.bufferInfo.flags & 4) != 0) {
                    this.endOfSteam = true;
                    this.bufferInfo.set(0, 0, 0, this.bufferInfo.flags);
                }
                if ((this.bufferInfo.flags & 2) != 0) {
                    this.mEncoderCodec.releaseOutputBuffer(dequeueOutputBuffer, false);
                    return 1;
                }
                this.queuedMuxer.queueBuff(QueuedMuxer.MediaType.VIDEO, this.outputBuffers[dequeueOutputBuffer], this.bufferInfo);
                this.presentationTimeUs = this.bufferInfo.presentationTimeUs;
                this.mEncoderCodec.releaseOutputBuffer(dequeueOutputBuffer, false);
                return 2;
        }
    }
}
