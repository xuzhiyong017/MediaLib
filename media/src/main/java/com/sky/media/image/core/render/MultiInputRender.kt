package com.sky.media.image.core.render

import android.opengl.GLES20
import com.sky.media.image.core.base.BaseRender
import com.sky.media.image.core.base.TextureOutRender
import java.util.*

abstract class MultiInputRender(private val numOfInputs: Int) : BaseRender() {
    protected var texture: IntArray
    var textureHandler = IntArray(numOfInputs)
    protected var filterLocations: MutableList<TextureOutRender>
    protected var texturesReceived: MutableList<TextureOutRender>

    fun clearRegisteredFilterLocations() {
        filterLocations.clear()
    }

    override fun initShaderHandles() {
        super.initShaderHandles()
        for (i in 0 until numOfInputs - 1) {
            textureHandler[i] =
                GLES20.glGetUniformLocation(programHandle, "inputImageTexture" + (i + 2))
        }
    }

    override fun dealNextTexture(
        textureOutRender: TextureOutRender,
        textureId: Int,
        needDraw: Boolean
    ) {
        if (!texturesReceived.contains(textureOutRender)) {
            texturesReceived.add(textureOutRender)
            if (needDraw) {
                markNeedDraw()
            }
        }
        val lastIndexOf = filterLocations.lastIndexOf(textureOutRender)
        if (lastIndexOf <= 0) {
            texture_in = textureId
        } else {
            texture[lastIndexOf - 1] = textureId
        }
        if (texturesReceived.size == numOfInputs) {
            setWidth(textureOutRender.getWidth())
            setHeight(textureOutRender.getHeight())
            onDrawFrame()
            texturesReceived.clear()
        }
    }

    override fun bindShaderValues() {
        super.bindShaderValues()
        for (i in 0 until numOfInputs - 1) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1 + i)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[i])
            GLES20.glUniform1i(textureHandler[i], i + 1)
        }
    }

    fun registerFilterLocation(gLTextureOutputRenderer: TextureOutRender?) {
        if (!filterLocations.contains(gLTextureOutputRenderer)) {
            if (gLTextureOutputRenderer != null) {
                filterLocations.add(gLTextureOutputRenderer)
            }
        }
    }

    fun registerFilterLocation(gLTextureOutputRenderer: TextureOutRender?, i: Int) {
        if (filterLocations.contains(gLTextureOutputRenderer)) {
            filterLocations.remove(gLTextureOutputRenderer)
        }
        if (filterLocations.size >= i) {
            if (gLTextureOutputRenderer != null) {
                filterLocations.add(i, gLTextureOutputRenderer)
            }
        }
    }

    init {
        textureHandler = IntArray(numOfInputs - 1)
        texture = IntArray(numOfInputs - 1)
        texturesReceived = ArrayList<TextureOutRender>(numOfInputs)
        filterLocations = ArrayList<TextureOutRender>(numOfInputs)
    }
}