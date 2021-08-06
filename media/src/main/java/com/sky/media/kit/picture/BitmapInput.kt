package com.sky.media.kit.picture

import android.graphics.Bitmap
import android.opengl.GLES20
import com.sky.media.image.core.base.TextureOutRender
import com.sky.media.image.core.extra.FpsTest
import com.sky.media.image.core.util.TextureBindUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * @author: xuzhiyong
 * @date: 2021/7/27  下午5:04
 * @Email: 18971269648@163.com
 * @description:
 */
class BitmapInput : TextureOutRender() {

    open var inputBitmap: Bitmap? = null
        set(value) {
            field = value
            hasInited = true
        }
    private var hasInited = false

    override fun drawFrame() {
        if(hasInited){
            bindTextures()
        }
        super.drawFrame()
        FpsTest.getInstance().countFps()
    }

    override fun handleSizeChange() {
        super.handleSizeChange()
        initTexture()
    }

    private fun initTexture() {
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

    private fun bindTextures() {
        if (texture_in != 0) {
            GLES20.glDeleteTextures(1, intArrayOf(texture_in), 0)
        }
        texture_in = TextureBindUtil.bindBitmap(inputBitmap)
        hasInited = false
        markNeedDraw()
    }

    override fun destroy() {
        super.destroy()
        if (texture_in != 0) {
            GLES20.glDeleteTextures(1, intArrayOf(texture_in), 0)
        }
        hasInited = true
    }
}