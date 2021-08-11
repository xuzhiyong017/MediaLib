package com.sky.media.kit.mediakit.video;

import android.media.MediaCodec.BufferInfo;

import java.nio.ByteBuffer;

public interface onBufferListener {
    void onDealData(ByteBuffer byteBuffer, BufferInfo bufferInfo);
}
