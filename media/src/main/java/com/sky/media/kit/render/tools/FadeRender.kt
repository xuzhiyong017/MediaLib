package com.sky.media.kit.render.tools

import android.opengl.GLES20
import com.sky.media.image.core.base.BaseRender
import com.sky.media.image.core.filter.IAdjustable

class FadeRender : BaseRender(), IAdjustable {

    private var mFadeValue = 0f
    private var mFadeHandle = 0

    init {
        mFragmentShader = """precision mediump float;
                            varying vec2 textureCoordinate;
                            uniform sampler2D inputImageTexture;
                            uniform float u_Fade;
                            void main() {
                                vec4 color = texture2D(inputImageTexture, textureCoordinate);
                                float r = u_Fade + (1.0 - u_Fade) * color.r;
                                float g = u_Fade + (1.0 - u_Fade) * color.g;
                                float b = u_Fade + (1.0 - u_Fade) * color.b;
                                gl_FragColor = vec4(r, g, b, 1.0);
                            }
                                 """
    }

    override fun initShaderHandles() {
        super.initShaderHandles()
        mFadeHandle = GLES20.glGetUniformLocation(programHandle, "u_Fade")
    }

    override fun bindShaderValues() {
        super.bindShaderValues()
        GLES20.glUniform1f(mFadeHandle, mFadeValue)
    }


    override fun adjust(i: Int, i2: Int, i3: Int) {
        mFadeValue = i.toFloat() * 1.0f / (i3 - i2).toFloat() / 3.0f
    }
}