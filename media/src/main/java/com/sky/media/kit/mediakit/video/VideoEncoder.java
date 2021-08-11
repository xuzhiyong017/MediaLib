package com.sky.media.kit.mediakit.video;

import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;

import com.sky.media.kit.transfer.QueuedMuxer;
import com.sky.media.kit.util.CodecUtil;

import java.nio.ByteBuffer;

public class VideoEncoder {
    private final BufferInfo bufferInfo = new BufferInfo();
    private MediaCodec mediaCodec;
    private ByteBuffer[] byteBuffers;
    private boolean hasStarted;
    private android.media.MediaFormat mediaFormat;
    private QueuedMuxer queuedMuxer;
    private onBufferListener bufferListener;

    public VideoEncoder(android.media.MediaFormat mediaFormat, QueuedMuxer queuedMuxer) throws Exception {
        this.queuedMuxer = queuedMuxer;
        this.mediaCodec = MediaCodec.createEncoderByType(mediaFormat.getString("mime"));
        this.mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        this.mediaCodec.start();
        this.hasStarted = true;
        this.byteBuffers = this.mediaCodec.getOutputBuffers();
    }

    public boolean dequeueInputBuffer(byte[] bArr, int i, long j) {
        boolean z = false;
        int dequeueInputBuffer = this.mediaCodec.dequeueInputBuffer(-1);
        if (dequeueInputBuffer >= 0) {
            ByteBuffer a = CodecUtil.getInputBuffer(this.mediaCodec, dequeueInputBuffer);
            a.clear();
            a.put(bArr, 0, i);
            this.mediaCodec.queueInputBuffer(dequeueInputBuffer, 0, bArr.length, j, 2);
        }
        while (dequeueOutputBuffer() != 0) {
            z = true;
        }
        return z;
    }

    public void setBufferListener(onBufferListener onBufferListener) {
        this.bufferListener = onBufferListener;
    }

    public void release() {
        if (this.mediaCodec != null) {
            if (this.hasStarted) {
                this.mediaCodec.stop();
            }
            this.mediaCodec.release();
            this.mediaCodec = null;
        }
        this.bufferListener = null;
    }

    private int dequeueOutputBuffer() {
        int dequeueOutputBuffer = this.mediaCodec.dequeueOutputBuffer(this.bufferInfo, 0);
        switch (dequeueOutputBuffer) {
            case -3:
                this.byteBuffers = this.mediaCodec.getOutputBuffers();
                return 1;
            case -2:
                if (this.mediaFormat != null) {
                    throw new RuntimeException("Video output bitspersample changed twice.");
                }
                this.mediaFormat = this.mediaCodec.getOutputFormat();
                this.queuedMuxer.executeTask(QueuedMuxer.MediaType.VIDEO, this.mediaFormat);
                return 1;
            case -1:
                return 0;
            default:
                if (this.mediaFormat == null) {
                    throw new RuntimeException("Could not determine actual output bitspersample.");
                }
                if ((this.bufferInfo.flags & 4) != 0) {
                    this.bufferInfo.set(0, 0, 0, this.bufferInfo.flags);
                }
                if (this.bufferListener != null) {
                    this.bufferListener.onDealData(this.byteBuffers[dequeueOutputBuffer], this.bufferInfo);
                }
                this.mediaCodec.releaseOutputBuffer(dequeueOutputBuffer, false);
                return 2;
        }
    }
}
