package com.sky.media.image.core.out

import android.graphics.Bitmap
import com.sky.media.image.core.base.BufferOutput
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer

/**
 * @author: xuzhiyong
 * @date: 2021/7/27  下午5:35
 * @Email: 18971269648@163.com
 * @description:
 */
class BitmapOutput :BufferOutput<IntBuffer>(){

    var mCallback: BitmapOutputCallback? = null
    var mConfig: Bitmap.Config? = null

    init {
        mConfig = Bitmap.Config.ARGB_8888
        textureVertices = arrayOfNulls(4)
        var fArr = floatArrayOf(
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f)
        textureVertices[0] = ByteBuffer.allocateDirect(fArr.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        textureVertices[0]?.put(fArr)?.position(0)

        fArr = floatArrayOf(
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            0.0f, 0.0f)
        textureVertices[1] = ByteBuffer.allocateDirect(fArr.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        textureVertices[1]?.put(fArr)?.position(0)

        fArr = floatArrayOf(
            1.0f, 0.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f)
        textureVertices[2] = ByteBuffer.allocateDirect(fArr.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        textureVertices[2]?.put(fArr)?.position(0)

        fArr = floatArrayOf(
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f)
        textureVertices[3] = ByteBuffer.allocateDirect(fArr.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        textureVertices[3]?.put(fArr)?.position(0)
    }

    interface BitmapOutputCallback {
        fun bitmapOutput(bitmap: Bitmap?)
    }

    override fun bufferOutput(intBuffer: IntBuffer) {
        val width: Int = width
        val height: Int = height
        if (width > 0 && height > 0) {
            val array: IntArray = intBuffer.array()
            for (i in array.indices) {
                array[i] = -16777216 or (array[i] and -0xff0100) or (array[i] shr 16 and 255) or (array[i] shl 16 and 0x00ff0000)
            }
            try {
                val createBitmap = Bitmap.createBitmap(array, width, height, mConfig)
                if (mCallback != null) {
                    mCallback!!.bitmapOutput(createBitmap)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                if (mCallback != null) {
                    mCallback!!.bitmapOutput(null)
                }
            }
        } else if (mCallback != null) {
            mCallback!!.bitmapOutput(null)
        }
    }

    override fun initBuffer(width: Int, height: Int): IntBuffer {
        val wrap = IntBuffer.wrap(IntArray(width * height))
        wrap.position(0)
        return wrap
    }
}