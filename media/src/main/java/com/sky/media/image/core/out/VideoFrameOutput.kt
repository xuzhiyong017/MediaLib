package com.sky.media.image.core.out

import com.sky.media.image.core.base.BufferOutput
import java.nio.ByteBuffer
import java.nio.ByteOrder

class VideoFrameOutput : BufferOutput<ByteBuffer>() {
    private var mOutputCallback: VideoFrameOutputCallback? = null

    interface VideoFrameOutputCallback {
        fun videoFrameOutput(bArr: ByteArray?)
    }

    fun setVideoFrameOutputCallback(videoFrameOutputCallback: VideoFrameOutputCallback?) {
        mOutputCallback = videoFrameOutputCallback
    }

    override fun initBuffer(i: Int, i2: Int): ByteBuffer {
        return ByteBuffer.allocate(i * i2 * 4)
    }

    override fun bufferOutput(byteBuffer: ByteBuffer) {
        if (mOutputCallback != null) {
            mOutputCallback!!.videoFrameOutput(byteBuffer.array())
        }
    }

    init {
        textureVertices = arrayOfNulls(4)
        var fArr = floatArrayOf(0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f)
        textureVertices[0] = ByteBuffer.allocateDirect(fArr.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        textureVertices[0]!!.put(fArr).position(0)
        fArr = floatArrayOf(1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f)
        textureVertices[1] =
            ByteBuffer.allocateDirect(fArr.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        textureVertices[1]!!.put(fArr).position(0)
        fArr = floatArrayOf(1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f)
        textureVertices[2] =
            ByteBuffer.allocateDirect(fArr.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        textureVertices[2]!!.put(fArr).position(0)
        fArr = floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f)
        textureVertices[3] =
            ByteBuffer.allocateDirect(fArr.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        textureVertices[3]!!.put(fArr).position(0)
    }
}