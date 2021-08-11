package com.sky.media.kit.transfer;

import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaExtractor;

import com.sky.media.kit.util.CodecUtil;

public class AudioTrackTranscoder {

    private final MediaExtractor mediaExtractor;
    private final QueuedMuxer queuedMuxer;
    private long presentationTimeUs;
    private final int audioTrack;
    private final android.media.MediaFormat inputFormat;
    private final android.media.MediaFormat outputFormat;
    private final BufferInfo bufferInfo = new BufferInfo();
    private MediaCodec mInputMediaCodec;
    private MediaCodec mOutputMediaCodec;
    private android.media.MediaFormat outFormat;
    private boolean f12124k;
    private boolean f12125l;
    private boolean endOfSteam;
    private boolean f12127n;
    private boolean f12128o;
    private AudioChannel audioChannel;

    public AudioTrackTranscoder(MediaExtractor mediaExtractor, int i, android.media.MediaFormat outputFormat, QueuedMuxer queuedMuxer) {
        this.mediaExtractor = mediaExtractor;
        this.audioTrack = i;
        this.outputFormat = outputFormat;
        this.queuedMuxer = queuedMuxer;
        this.inputFormat = this.mediaExtractor.getTrackFormat(this.audioTrack);
    }

    public void startCodec() {
        this.mediaExtractor.selectTrack(this.audioTrack);
        try {
            this.mOutputMediaCodec = MediaCodec.createEncoderByType(this.outputFormat.getString("mime"));
            this.mOutputMediaCodec.configure(this.outputFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            this.mOutputMediaCodec.start();
            this.f12128o = true;
            android.media.MediaFormat trackFormat = this.mediaExtractor.getTrackFormat(this.audioTrack);
            try {
                this.mInputMediaCodec = MediaCodec.createDecoderByType(trackFormat.getString("mime"));
                this.mInputMediaCodec.configure(trackFormat, null, null, 0);
                this.mInputMediaCodec.start();
                this.f12127n = true;
                this.audioChannel = new AudioChannel(this.mInputMediaCodec, this.mOutputMediaCodec, this.outputFormat);
            } catch (Throwable e) {
                throw new IllegalStateException(e);
            }
        } catch (Throwable e2) {
            throw new IllegalStateException(e2);
        }
    }

    public boolean transferData() {
        boolean z = false;
        while (m14825c(0) != 0) {
            z = true;
        }
        int b;
        do {
            b = m14824b(0);
            if (b != 0) {
                z = true;
                continue;
            }
        } while (b == 1);
        while (this.audioChannel.encodeData(0)) {
            z = true;
        }
        while (m14823a(0) != 0) {
            z = true;
        }
        return z;
    }

    /* renamed from: a */
    private int m14823a(long j) {
        if (this.f12124k) {
            return 0;
        }
        int sampleTrackIndex = this.mediaExtractor.getSampleTrackIndex();
        if (sampleTrackIndex >= 0 && sampleTrackIndex != this.audioTrack) {
            return 0;
        }
        int dequeueInputBuffer = this.mInputMediaCodec.dequeueInputBuffer(j);
        if (dequeueInputBuffer < 0) {
            return 0;
        }
        if (sampleTrackIndex < 0) {
            this.f12124k = true;
            this.mInputMediaCodec.queueInputBuffer(dequeueInputBuffer, 0, 0, 0, 4);
            return 0;
        }
        boolean z;
        int i;
        int readSampleData = this.mediaExtractor.readSampleData(CodecUtil.getInputBuffer(this.mInputMediaCodec, dequeueInputBuffer), 0);
        if ((this.mediaExtractor.getSampleFlags() & 1) != 0) {
            z = true;
        } else {
            z = false;
        }
        MediaCodec mediaCodec = this.mInputMediaCodec;
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

    /* renamed from: b */
    private int m14824b(long j) {
        if (this.f12125l) {
            return 0;
        }
        int dequeueOutputBuffer = this.mInputMediaCodec.dequeueOutputBuffer(this.bufferInfo, j);
        switch (dequeueOutputBuffer) {
            case -3:
                break;
            case -2:
                this.audioChannel.setDecodeFormat(this.mInputMediaCodec.getOutputFormat());
                break;
            case -1:
                return 0;
            default:
                if ((this.bufferInfo.flags & 4) != 0) {
                    this.f12125l = true;
                    this.audioChannel.enqueueBuffer(-1, 0);
                } else if (this.bufferInfo.size > 0) {
                    this.audioChannel.enqueueBuffer(dequeueOutputBuffer, this.bufferInfo.presentationTimeUs);
                }
                return 2;
        }
        return 1;
    }

    private int m14825c(long j) {
        if (this.endOfSteam) {
            return 0;
        }
        int dequeueOutputBuffer = this.mOutputMediaCodec.dequeueOutputBuffer(this.bufferInfo, j);
        switch (dequeueOutputBuffer) {
            case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                return 1;
            case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                if (this.outFormat != null) {
                    throw new RuntimeException("Audio output bitspersample changed twice.");
                }
                this.outFormat = this.mOutputMediaCodec.getOutputFormat();
                this.queuedMuxer.executeTask(QueuedMuxer.MediaType.AUDIO, this.outFormat);
                return 1;
            case MediaCodec.INFO_TRY_AGAIN_LATER:
                return 0;
            default:
                if (this.outFormat == null) {
                    throw new RuntimeException("Could not determine actual output bitspersample.");
                }
                if ((this.bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    this.endOfSteam = true;
                    this.bufferInfo.set(0, 0, 0, this.bufferInfo.flags);
                }
                if ((this.bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    this.mOutputMediaCodec.releaseOutputBuffer(dequeueOutputBuffer, false);
                    return 1;
                }
                this.queuedMuxer.queueBuff(QueuedMuxer.MediaType.AUDIO, CodecUtil.getOutputBuffer(this.mOutputMediaCodec, dequeueOutputBuffer), this.bufferInfo);
                this.presentationTimeUs = this.bufferInfo.presentationTimeUs;
                this.mOutputMediaCodec.releaseOutputBuffer(dequeueOutputBuffer, false);
                return 2;
        }
    }

    public boolean isEndOfStream() {
        return this.endOfSteam;
    }

    public void release() {
        if (this.mInputMediaCodec != null) {
            if (this.f12127n) {
                this.mInputMediaCodec.stop();
            }
            this.mInputMediaCodec.release();
            this.mInputMediaCodec = null;
        }
        if (this.mOutputMediaCodec != null) {
            if (this.f12128o) {
                this.mOutputMediaCodec.stop();
            }
            this.mOutputMediaCodec.release();
            this.mOutputMediaCodec = null;
        }
    }
}
