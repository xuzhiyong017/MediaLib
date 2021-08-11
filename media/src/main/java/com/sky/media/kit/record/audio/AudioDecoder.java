package com.sky.media.kit.record.audio;

import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaFormat;


import com.sky.media.kit.util.CodecUtil;

import java.io.IOException;
import java.nio.ByteBuffer;

public class AudioDecoder {
    private MediaCodec mMediaCodec;
    private OnDecodeCallback mCallback;

    public interface OnDecodeCallback {
        void onDecodeData(byte[] bArr, long j);
    }

    public AudioDecoder(String str, MediaFormat mediaFormat) throws IOException {
        this.mMediaCodec = MediaCodec.createDecoderByType(str);
        this.mMediaCodec.configure(mediaFormat, null, null,0);
        this.mMediaCodec.start();
    }

    public void setOnDecodeCallback(OnDecodeCallback onDecodeCallback) {
        this.mCallback = onDecodeCallback;
    }

    public void decodeData(byte[] bArr, long j) {
        int dequeueInputBuffer = this.mMediaCodec.dequeueInputBuffer(-1);
        if (dequeueInputBuffer >= 0) {
            ByteBuffer a = CodecUtil.getInputBuffer(this.mMediaCodec, dequeueInputBuffer);
            a.clear();
            a.put(bArr);
            this.mMediaCodec.queueInputBuffer(dequeueInputBuffer, 0, bArr.length, j, 0);
        }
        BufferInfo bufferInfo = new BufferInfo();
        int dequeueOutputBuffer = this.mMediaCodec.dequeueOutputBuffer(bufferInfo, 0);
        while (dequeueOutputBuffer >= 0) {
            byte[] bArr2 = new byte[bufferInfo.size];
            CodecUtil.getOutputBuffer(this.mMediaCodec, dequeueOutputBuffer).get(bArr2);
            if (this.mCallback != null) {
                this.mCallback.onDecodeData(bArr2, j);
            }
            this.mMediaCodec.releaseOutputBuffer(dequeueOutputBuffer, false);
            dequeueOutputBuffer = this.mMediaCodec.dequeueOutputBuffer(bufferInfo, 0);
        }
    }

    public void stop() {
        if (this.mMediaCodec != null) {
            this.mMediaCodec.stop();
            this.mMediaCodec.release();
        }
    }
}
