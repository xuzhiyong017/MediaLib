package com.sky.media.kit.render.tools

import android.opengl.GLES20
import com.sky.media.image.core.base.BaseRender
import com.sky.media.image.core.filter.IAdjustable

class ContrastRender : BaseRender(), IAdjustable {

    private var mContrastValue = 1.0f
    private var mContrastHandle = 0

    init {
        mFragmentShader = """ precision mediump float;
                             uniform sampler2D inputImageTexture;
                             varying vec2 textureCoordinate;
                             uniform float u_Contrast;
                             void main(){
                                vec4 color = texture2D(inputImageTexture,textureCoordinate);
                                gl_FragColor = vec4(((color.rgb - vec3(0.5)) * u_Contrast + vec3(0.5)), color.a);
                            }"""
    }

    override fun initShaderHandles() {
        super.initShaderHandles()
        mContrastHandle = GLES20.glGetUniformLocation(programHandle, "u_Contrast")
    }

    override fun bindShaderValues() {
        super.bindShaderValues()
        GLES20.glUniform1f(mContrastHandle, mContrastValue)
    }


    override fun adjust(i: Int, i2: Int, i3: Int) {
        mContrastValue = i.toFloat() * 1.0f / (i3 - i2).toFloat() + 1.0f
    }
}