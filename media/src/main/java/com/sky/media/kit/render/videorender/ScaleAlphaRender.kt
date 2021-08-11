package com.sky.media.kit.render.videorender

import android.opengl.GLES20
import com.sky.media.image.core.base.BaseRender

open class ScaleAlphaRender : BaseRender() {
    private val UNIFORM_ALPHA = "alpha"
    private val UNIFORM_OFFSET = "offset"
    private val UNIFORM_SCALE = "scale"
    private var mAlpha = 1.0f
    private var mAlphaHandle = 0
    private var mOffset = FloatArray(2)
    private var mOffsetHandle = 0
    private var mScale = 1.0f
    private var mScaleHandle = 0

    init {
        mVertexShader = """attribute vec4 position;
                            attribute vec4 inputTextureCoordinate;
                            uniform float scale;
                            uniform vec2 offset;
                            varying vec2 textureCoordinate;
                            void main() {
                                gl_Position = position;
                                textureCoordinate = inputTextureCoordinate.xy * scale + offset;
                            }"""

        mFragmentShader = """precision lowp float;
                            varying highp vec2 textureCoordinate;
                            uniform sampler2D inputImageTexture;
                            uniform float alpha;
                            void main(){
                                vec4 outputColor = texture2D(inputImageTexture, textureCoordinate);
                                outputColor.a = alpha;
                                gl_FragColor = outputColor;
                            }"""
    }

    override fun initShaderHandles() {
        super.initShaderHandles()
        mScaleHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_SCALE)
        mOffsetHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_OFFSET)
        mAlphaHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_ALPHA)
    }

    override fun bindShaderValues() {
        super.bindShaderValues()
        GLES20.glUniform1f(mScaleHandle, mScale)
        GLES20.glUniform2fv(mOffsetHandle, 1, mOffset, 0)
        GLES20.glUniform1f(mAlphaHandle, mAlpha)
    }

    fun setScale(f: Float) {
        mScale = f
    }

    fun setOffset(fArr: FloatArray) {
        mOffset = fArr
    }

    public override var alpha: Float
        get() = super.alpha
        set(f) {
            mAlpha = f
        }
}