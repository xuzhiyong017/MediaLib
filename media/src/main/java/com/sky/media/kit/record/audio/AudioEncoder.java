package com.sky.media.kit.record.audio;

import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaFormat;
import android.util.Log;


import com.sky.media.kit.util.AacUtil;
import com.sky.media.kit.util.CodecUtil;
import com.sky.media.kit.util.MediaHelper;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;

public class AudioEncoder {
    private MediaCodec mMediaCodec;
    private FileOutputStream fileOutputStream;
    private String mAudioPath;
    private int mSimpleRate = MediaHelper.DEFAULT_SIMPLE_HZ;
    private int channelCount = MediaHelper.Default_ChannelCount;

    public AudioEncoder(String str) {
        this.mAudioPath = str;
    }

    public void startAudioEncoder(int sampleRate, int channelCount) throws Exception {
        this.mSimpleRate = sampleRate;
        this.channelCount = channelCount;
        this.fileOutputStream = new FileOutputStream(this.mAudioPath);
        MediaFormat b = MediaHelper.getAudioFormat(sampleRate, channelCount);
        this.mMediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC);
        this.mMediaCodec.configure(b, null, null,  MediaCodec.CONFIGURE_FLAG_ENCODE);
        this.mMediaCodec.start();
    }

    public void encodeData(byte[] bArr, long j) {
        ByteBuffer byteBuffer;
        int dequeueInputBuffer = this.mMediaCodec.dequeueInputBuffer(-1);
        if (dequeueInputBuffer >= 0) {
            byteBuffer = CodecUtil.getInputBuffer(this.mMediaCodec, dequeueInputBuffer);
            byteBuffer.clear();
            byteBuffer.put(bArr);
            this.mMediaCodec.queueInputBuffer(dequeueInputBuffer, 0, bArr.length, j, 2);
        }
        BufferInfo bufferInfo = new BufferInfo();
        dequeueInputBuffer = this.mMediaCodec.dequeueOutputBuffer(bufferInfo, 0);
        while (dequeueInputBuffer >= 0) {
            byteBuffer = CodecUtil.getOutputBuffer(this.mMediaCodec, dequeueInputBuffer);
            int i = bufferInfo.size;
            int i2 = i + 7;
            byte[] buffer = new byte[i2];
            AacUtil.addADTSHeader(buffer, i2, this.mSimpleRate, this.channelCount);
            byteBuffer.get(buffer, 7, i);
            try {
                this.fileOutputStream.write(buffer, 0, buffer.length);
            } catch (Throwable e) {
                Log.w("AudioEncoder", Log.getStackTraceString(e));
            }
            this.mMediaCodec.releaseOutputBuffer(dequeueInputBuffer, false);
            dequeueInputBuffer = this.mMediaCodec.dequeueOutputBuffer(bufferInfo, 0);
        }
    }

    public void release() {
        if (this.mMediaCodec != null) {
            this.mMediaCodec.stop();
            this.mMediaCodec.release();
        }
        if (this.fileOutputStream != null) {
            try {
                this.fileOutputStream.flush();
                this.fileOutputStream.close();
            } catch (Throwable e) {
                Log.w("AudioEncoder", Log.getStackTraceString(e));
            }
        }
    }
}
