package com.sky.media.kit.transfer;

import android.media.MediaCodec;
import android.media.MediaFormat;


import com.sky.media.kit.util.CodecUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayDeque;
import java.util.Queue;

class AudioChannel {
    private final Queue<BufferWrap> dirtyQueue = new ArrayDeque();
    private final Queue<BufferWrap> dataQueue = new ArrayDeque();
    private final MediaCodec inputCodec;
    private final MediaCodec outputCodec;
    private final MediaFormat outputFormat;
    private int sampleRate;
    private int inputChannelCount;
    private int outputChannelCount;
    AudioRemixer audioRemixer;
    private final BufferWrap bufferWrap = new BufferWrap();
    private MediaFormat inputFormat;

    private static class BufferWrap {
        int bufferIndex;
        long presentationTimeUs;
        ShortBuffer buffer;

        private BufferWrap() {
        }
    }

    public AudioChannel(MediaCodec mediaCodec, MediaCodec mediaCodec2, MediaFormat mediaFormat) {
        this.inputCodec = mediaCodec;
        this.outputCodec = mediaCodec2;
        this.outputFormat = mediaFormat;
    }

    public void setDecodeFormat(MediaFormat mediaFormat) {
        this.inputFormat = mediaFormat;
        this.sampleRate = this.inputFormat.getInteger("sample-rate");
        if (this.sampleRate != this.outputFormat.getInteger("sample-rate")) {
            throw new UnsupportedOperationException("Audio sample rate conversion not supported yet.");
        }
        this.inputChannelCount = this.inputFormat.getInteger("channel-count");
        this.outputChannelCount = this.outputFormat.getInteger("channel-count");
        if (this.inputChannelCount != 1 && this.inputChannelCount != 2) {
            throw new UnsupportedOperationException("Input channel count (" + this.inputChannelCount + ") not supported.");
        } else if (this.outputChannelCount == 1 || this.outputChannelCount == 2) {
            if (this.inputChannelCount > this.outputChannelCount) {
                this.audioRemixer = AudioRemixer.LESS_REMIXER;
            } else if (this.inputChannelCount < this.outputChannelCount) {
                this.audioRemixer = AudioRemixer.MORE_REMIXER;
            } else {
                this.audioRemixer = AudioRemixer.COPY_REMIXER;
            }
            this.bufferWrap.presentationTimeUs = 0;
        } else {
            throw new UnsupportedOperationException("Output channel count (" + this.outputChannelCount + ") not supported.");
        }
    }

    public void enqueueBuffer(int i, long j) {
        ShortBuffer shortBuffer = null;
        if (this.inputFormat == null) {
            throw new RuntimeException("Buffer received before bitspersample!");
        }
        ByteBuffer b = i == -1 ? null : CodecUtil.getOutputBuffer(this.inputCodec, i);
        BufferWrap bufferWrap = (BufferWrap) this.dirtyQueue.poll();
        if (bufferWrap == null) {
            bufferWrap = new BufferWrap();
        }
        bufferWrap.bufferIndex = i;
        bufferWrap.presentationTimeUs = j;
        if (b != null) {
            shortBuffer = b.asShortBuffer();
        }
        bufferWrap.buffer = shortBuffer;
        if (this.bufferWrap.buffer == null) {
            this.bufferWrap.buffer = ByteBuffer.allocateDirect(b.capacity()).order(ByteOrder.nativeOrder()).asShortBuffer();
            this.bufferWrap.buffer.clear().flip();
        }
        this.dataQueue.add(bufferWrap);
    }

    public boolean encodeData(long j) {
        boolean z = this.bufferWrap.buffer != null && this.bufferWrap.buffer.hasRemaining();
        if (this.dataQueue.isEmpty() && !z) {
            return false;
        }
        int dequeueInputBuffer = this.outputCodec.dequeueInputBuffer(j);
        if (dequeueInputBuffer < 0) {
            return false;
        }
        ShortBuffer asShortBuffer = CodecUtil.getInputBuffer(this.outputCodec, dequeueInputBuffer).asShortBuffer();
        if (z) {
            this.outputCodec.queueInputBuffer(dequeueInputBuffer, 0, asShortBuffer.position() * 2, getCurPresentationTimeUs(asShortBuffer), 0);
            return true;
        }
        BufferWrap bufferWrap = (BufferWrap) this.dataQueue.poll();
        if (bufferWrap.bufferIndex == -1) {
            this.outputCodec.queueInputBuffer(dequeueInputBuffer, 0, 0, 0, 4);
            return false;
        }
        this.outputCodec.queueInputBuffer(dequeueInputBuffer, 0, asShortBuffer.position() * 2, resampleBuffer(bufferWrap, asShortBuffer), 0);
        this.inputCodec.releaseOutputBuffer(bufferWrap.bufferIndex, false);
        this.dirtyQueue.add(bufferWrap);
        return true;
    }

    private static long getPerFrameTime(int i, int i2, int i3) {
        return (((long) i) / (((long) i2) * 1000000)) / ((long) i3);
    }

    private long getCurPresentationTimeUs(ShortBuffer shortBuffer) {
        ShortBuffer shortBuffer2 = this.bufferWrap.buffer;
        int limit = shortBuffer2.limit();
        int remaining = shortBuffer2.remaining();
        long a = this.bufferWrap.presentationTimeUs + AudioChannel.getPerFrameTime(shortBuffer2.position(), this.sampleRate, this.outputChannelCount);
        shortBuffer.clear();
        shortBuffer2.limit(shortBuffer.capacity());
        shortBuffer.put(shortBuffer2);
        if (remaining >= shortBuffer.capacity()) {
            shortBuffer2.clear().limit(0);
        } else {
            shortBuffer2.limit(limit);
        }
        return a;
    }

    private long resampleBuffer(BufferWrap bufferWrap, ShortBuffer shortBuffer) {
        ShortBuffer shortBuffer2 = bufferWrap.buffer;
        ShortBuffer shortBuffer3 = this.bufferWrap.buffer;
        shortBuffer.clear();
        shortBuffer2.clear();
        if (shortBuffer2.remaining() > shortBuffer.remaining()) {
            shortBuffer2.limit(shortBuffer.capacity());
            this.audioRemixer.mixBuffer(shortBuffer2, shortBuffer);
            shortBuffer2.limit(shortBuffer2.capacity());
            long a = AudioChannel.getPerFrameTime(shortBuffer2.position(), this.sampleRate, this.inputChannelCount);
            this.audioRemixer.mixBuffer(shortBuffer2, shortBuffer3);
            shortBuffer3.flip();
            this.bufferWrap.presentationTimeUs = a + bufferWrap.presentationTimeUs;
        } else {
            this.audioRemixer.mixBuffer(shortBuffer2, shortBuffer);
        }
        return bufferWrap.presentationTimeUs;
    }
}
