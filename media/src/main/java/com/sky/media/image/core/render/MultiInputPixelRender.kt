package com.sky.media.image.core.render

import android.opengl.GLES20

abstract class MultiInputPixelRender(numOfInputs: Int) : MultiInputRender(numOfInputs) {
    protected var texelHeight = 0f
    protected var texelWidth = 0f
    private var texelHeightHandle = 0
    private var texelWidthHandle = 0

    override fun handleSizeChange() {
        super.handleSizeChange()
        texelWidth = 1.0f / width.toFloat()
        texelHeight = 1.0f / height.toFloat()
    }

    override fun initShaderHandles() {
        super.initShaderHandles()
        texelWidthHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_TEXELWIDTH)
        texelHeightHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_TEXELHEIGHT)
    }

    override fun bindShaderValues() {
        super.bindShaderValues()
        GLES20.glUniform1f(texelWidthHandle, texelWidth)
        GLES20.glUniform1f(texelHeightHandle, texelHeight)
    }

    companion object {
        protected const val UNIFORM_TEXELHEIGHT = "u_TexelHeight"
        protected const val UNIFORM_TEXELWIDTH = "u_TexelWidth"
    }
}