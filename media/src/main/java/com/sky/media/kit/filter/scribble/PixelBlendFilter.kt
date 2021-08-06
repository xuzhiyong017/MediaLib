package com.sky.media.kit.filter.scribble

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils
import com.sky.media.image.core.base.BaseRender
import com.sky.media.image.core.base.TextureOutRender

class PixelBlendFilter : BaseRender() {

    private val inputCounts = 2
    private val textureHandles = IntArray(inputCounts)
    private val textures = IntArray(inputCounts)
    private val bitmaps = arrayOfNulls<Bitmap>(inputCounts)

    init {
        mFragmentShader = """ precision mediump float;
                             uniform sampler2D inputImageTexture;
                             uniform sampler2D inputImageTexture1;
                             uniform sampler2D inputImageTexture2;
                             varying vec2 textureCoordinate;
                             void main(){
                                highp vec2 textureCoord = vec2(textureCoordinate.x,1.0-textureCoordinate.y);
                                vec4 originalImageColor = texture2D(inputImageTexture, textureCoordinate);
                                vec4 maskImageColor = texture2D(inputImageTexture1, textureCoord );
                                vec4 maskImage2Color = texture2D(inputImageTexture2, textureCoord );
                                if(maskImageColor.b > 0.0 && maskImageColor.b < 0.8){
                                    gl_FragColor = maskImage2Color;
                                }else{
                                    gl_FragColor = originalImageColor;
                                }
                            }
                                 """
    }

    override fun destroy() {
        super.destroy()
        for (i in textures.indices) {
            if (textures[i] != 0) {
                GLES20.glDeleteTextures(1, intArrayOf(textures[i]), 0)
                textures[i] = 0
            }
        }
    }

    override fun dealNextTexture(gLTextureOutputRenderer: TextureOutRender,i: Int,  z: Boolean) {
        if (z) {
            markNeedDraw()
        }
        texture_in = i
        var i2 = 0
        while (i2 < textures.size) {
            if (textures[i2] != 0) {
                GLES20.glDeleteTextures(1, intArrayOf(textures[i2]), 0)
            }
            if (!(bitmaps[i2] == null || bitmaps[i2]!!.isRecycled)) {
                textures[i2] = bindBitmap(bitmaps[i2])
            }
            i2++
        }
        setWidth(gLTextureOutputRenderer.getWidth())
        setHeight(gLTextureOutputRenderer.getHeight())
        onDrawFrame()
    }

    override fun initShaderHandles() {
        super.initShaderHandles()
        for (i in 0 until inputCounts) {
            textureHandles[i] =
                GLES20.glGetUniformLocation(programHandle, "inputImageTexture" + (i + 1))
        }
    }

    override fun bindShaderValues() {
        super.bindShaderValues()
        for (i in 0 until inputCounts) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1 + i)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[i])
            GLES20.glUniform1i(textureHandles[i], i + 1)
        }
    }

    fun setSourceBitmap(bitmap: Bitmap?, bitmap2: Bitmap?) {
        if (!(bitmap == null || bitmap.isRecycled)) {
            bitmaps[0] = bitmap
        }
        if (bitmap2 != null && !bitmap2.isRecycled) {
            bitmaps[1] = bitmap2
        }
    }

    companion object {
        fun bindBitmap(bitmap: Bitmap?): Int {
            val iArr = IntArray(1)
            GLES20.glGenTextures(1, iArr, 0)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, iArr[0])
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT)
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
            return iArr[0]
        }
    }
}