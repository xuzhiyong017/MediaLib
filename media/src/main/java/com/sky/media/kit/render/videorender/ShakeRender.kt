package com.sky.media.kit.render.videorender

import android.opengl.GLES20
import com.sky.media.image.core.base.BaseRender
import com.sky.media.image.core.filter.IAdjustable
import com.sky.media.image.core.filter.IRequireProgress


class ShakeRender : BaseRender(), IAdjustable, IRequireProgress {
    private val UNIFORM_PROGRESS = "progress"
    private var mProgress = 0f
    private var mProgressHandler = 0

    override var mFragmentShader: String = """precision mediump float;
                                            uniform sampler2D inputImageTexture;
                                            varying vec2 textureCoordinate;
                                            uniform float progress;
                                            void main() {
                                                vec2 uv = textureCoordinate.xy;
                                                float amount = (1.0 + sin(progress * 6.0)) * 0.5;
                                                amount *= 1.0 + sin(progress * 16.0) * 0.5;
                                                amount *= 1.0 + sin(progress * 19.0) * 0.5;
                                                amount *= 1.0 + sin(progress * 27.0) * 0.5;
                                                amount = pow(amount, 3.0);
                                                amount *= 0.05;
                                                vec3 fragColor = vec3(texture2D(inputImageTexture, vec2(uv.x + amount, uv.y)).r,texture2D(inputImageTexture, uv).g, texture2D(inputImageTexture, vec2(uv.x - amount, uv.y)).b);
                                                fragColor *= (1.0 - amount * 0.5);
                                                gl_FragColor = vec4(fragColor, 1.0);
                                            }"""


    override fun initShaderHandles() {
        super.initShaderHandles()
        mProgressHandler = GLES20.glGetUniformLocation(this.programHandle, "progress")
    }

    override fun bindShaderValues() {
        super.bindShaderValues()
        GLES20.glUniform1f(mProgressHandler, mProgress)
    }

    override fun adjust(i: Int, i2: Int, i3: Int) {
        mProgress = (i - i2).toFloat() * 1.0f / (i3 - i2).toFloat() * getDuration().toFloat()
    }

    override fun setProgress(f: Float) {
        mProgress = getDuration().toFloat() * f / 1000.0f
    }

    override fun getDuration(): Long {
        return 5000
    }
}