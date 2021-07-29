package com.sky.media.image.core.base

import android.opengl.GLES20
import java.nio.Buffer

/**
 * @author: xuzhiyong
 * @date: 2021/7/27  下午5:18
 * @Email: 18971269648@163.com
 * @description:
 */
abstract class BufferOutput<T : Buffer> : BaseRender() {

    protected var mOutputBuffer: T? = null

    abstract fun bufferOutput(t: T)

    abstract fun initBuffer(width: Int, height: Int): T

    override fun destroy() {
        super.destroy()
        if (mOutputBuffer != null) {
            mOutputBuffer!!.clear()
            mOutputBuffer = null
        }
    }

    override fun afterDrawFrame() {
        if (mOutputBuffer == null || mIsChangeSize) {
            mOutputBuffer = initBuffer(width, height)
        }
        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mOutputBuffer)
        bufferOutput(mOutputBuffer!!)
    }
}