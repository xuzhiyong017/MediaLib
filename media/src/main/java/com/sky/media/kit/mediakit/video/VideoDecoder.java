package com.sky.media.kit.mediakit.video;

import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;


import com.sky.media.kit.util.CodecUtil;

import java.io.IOException;
import java.nio.ByteBuffer;

public class VideoDecoder {
    private final MediaExtractor mediaExtractor;
    private final int trackIndex;
    private final BufferInfo bufferInfo = new BufferInfo();
    private MediaCodec mediaCodec;
    private ByteBuffer[] byteBuffers;
    private boolean f12246f;
    private boolean isEndOfStream;
    private boolean hasStart;
    private onBufferListener listener;
    private MediaFormat mediaFormat;

    public VideoDecoder(MediaExtractor mediaExtractor, int i) throws IOException {
        this.mediaExtractor = mediaExtractor;
        this.trackIndex = i;
        this.mediaExtractor.selectTrack(this.trackIndex);
        MediaFormat trackFormat = this.mediaExtractor.getTrackFormat(this.trackIndex);
        if (trackFormat.containsKey("rotation-degrees")) {
            trackFormat.setInteger("rotation-degrees", 0);
        }
        this.mediaCodec = MediaCodec.createDecoderByType(trackFormat.getString("mime"));
        this.mediaCodec.configure(trackFormat, null, null, 0);
        this.mediaCodec.start();
        this.hasStart = true;
        this.byteBuffers = this.mediaCodec.getInputBuffers();
    }

    public void startDecode(onBufferListener onBufferListener) {
        this.listener = onBufferListener;
        while (!this.isEndOfStream) {
            Object obj = null;
            int d;
            do {
                d = dequeueoutBuffer();
                if (d != 0) {
                    obj = 1;
                    continue;
                }
            } while (d == 1);
            while (dequeueInputBuffer() != 0) {
                int i = 1;
            }
            if (obj == null) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
            }
        }
        release();
    }

    public MediaFormat getOutputFormat() {
        return this.mediaFormat;
    }

    public void release() {
        if (this.mediaCodec != null) {
            if (this.hasStart) {
                this.mediaCodec.stop();
            }
            this.mediaCodec.release();
            this.mediaCodec = null;
        }
        this.listener = null;
    }

    private int dequeueInputBuffer() {
        if (this.f12246f) {
            return 0;
        }
        int sampleTrackIndex = this.mediaExtractor.getSampleTrackIndex();
        if (sampleTrackIndex >= 0 && sampleTrackIndex != this.trackIndex) {
            return 0;
        }
        int dequeueInputBuffer = this.mediaCodec.dequeueInputBuffer(0);
        if (dequeueInputBuffer < 0) {
            return 0;
        }
        if (sampleTrackIndex < 0) {
            this.f12246f = true;
            this.mediaCodec.queueInputBuffer(dequeueInputBuffer, 0, 0, 0, 4);
            return 0;
        }
        boolean z;
        int i;
        int readSampleData = this.mediaExtractor.readSampleData(this.byteBuffers[dequeueInputBuffer], 0);
        if ((this.mediaExtractor.getSampleFlags() & 1) != 0) {
            z = true;
        } else {
            z = false;
        }
        MediaCodec mediaCodec = this.mediaCodec;
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

    private int dequeueoutBuffer() {
        boolean z = true;
        if (this.isEndOfStream) {
            return 0;
        }
        int dequeueOutputBuffer = this.mediaCodec.dequeueOutputBuffer(this.bufferInfo, 0);
        switch (dequeueOutputBuffer) {
            case -3:
                break;
            case -2:
                this.mediaFormat = this.mediaCodec.getOutputFormat();
                break;
            case -1:
                return 0;
            default:
                if ((this.bufferInfo.flags & 4) != 0) {
                    this.isEndOfStream = true;
                    this.bufferInfo.size = 0;
                }
                if (this.bufferInfo.size <= 0) {
                    z = false;
                }
                if (z) {
                    ByteBuffer b = CodecUtil.getOutputBuffer(this.mediaCodec, dequeueOutputBuffer);
                    if (this.listener != null) {
                        this.listener.onDealData(b, this.bufferInfo);
                    }
                }
                this.mediaCodec.releaseOutputBuffer(dequeueOutputBuffer, z);
                return 2;
        }
        return 1;
    }
}
