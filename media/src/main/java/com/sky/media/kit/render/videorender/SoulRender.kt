package com.sky.media.kit.render.videorender

import com.sky.media.image.core.filter.IAdjustable
import com.sky.media.image.core.filter.IRequireProgress
import com.sky.media.image.core.render.EmptyRender
import com.sky.media.image.core.render.GaussianBlurRender
import com.sky.media.image.core.render.GroupRender
import com.sky.media.image.core.render.MultiInputRender


class SoulRender : GroupRender(), IAdjustable, IRequireProgress {
    private val mBlendRender: MultiInputRender = object : MultiInputRender(2) {
        override var mFragmentShader: String = """varying highp vec2 textureCoordinate;
                                                    uniform sampler2D inputImageTexture;
                                                    uniform sampler2D inputImageTexture2;
                                                    void main() {
                                                        lowp vec4 c2 = texture2D(inputImageTexture, textureCoordinate);
                                                        lowp vec4 c1 = texture2D(inputImageTexture2, textureCoordinate);
                                                        lowp vec4 outputColor;
                                                        //     outputColor.r = c1.r + c2.r * c2.a * (1.0 - c1.a);
                                                        //     outputColor.g = c1.g + c2.g * c2.a * (1.0 - c1.a);
                                                        //     outputColor.b = c1.b + c2.b * c2.a * (1.0 - c1.a);
                                                        //     outputColor.a = c1.a + c2.a * (1.0 - c1.a);
                                                        lowp float a = c1.a + c2.a * (1.0 - c1.a);
                                                        lowp float alphaDivisor = a + step(a, 0.0); // Protect against a divide-by-zero blacking out things in the output
                                                        outputColor.r = (c1.r * c1.a + c2.r * c2.a * (1.0 - c1.a))/alphaDivisor;
                                                        outputColor.g = (c1.g * c1.a + c2.g * c2.a * (1.0 - c1.a))/alphaDivisor;
                                                        outputColor.b = (c1.b * c1.a + c2.b * c2.a * (1.0 - c1.a))/alphaDivisor;
                                                        outputColor.a = a;
                                                        gl_FragColor = outputColor;
                                                    }"""
    }
    private val mBlurRender: GaussianBlurRender = GaussianBlurRender(1.0f)
    private val mNormalRender: EmptyRender = EmptyRender()
    private var mProgress = 0f
    private val mScaleAlphaRender: ScaleAlphaRender = ScaleAlphaRenderExt()

    internal inner class ScaleAlphaRenderExt : ScaleAlphaRender() {
        override fun beforeDrawFrame() {
            val soulScale = soulScale(mProgress)
            mScaleAlphaRender.setScale(1.0f - soulScale)
            val f = soulScale / 2.0f
            mScaleAlphaRender.setOffset(floatArrayOf(f, f))
            mScaleAlphaRender.alpha = 0.65f - soulScale
        }

        private fun soulScale(f: Float): Float {
            if (f.toDouble() <= 0.15) {
                return 0.1f * Math.sin(f.toDouble() * 10.0 / 3.0 * 3.14).toFloat()
            }
            return if (f.toDouble() > 0.5) {
                0.65f
            } else f
        }
    }

    override fun adjust(i: Int, i2: Int, i3: Int) {
        mProgress = (i - i2).toFloat() * 1.0f / (i3 - i2).toFloat()
    }

    override fun getDuration(): Long {
        return 600
    }

    override fun setProgress(f: Float) {
        mProgress = f
    }

    init {
        registerInitialFilter(mNormalRender)
        registerInitialFilter(mBlurRender)
        registerFilter(mScaleAlphaRender)
        registerTerminalFilter(mBlendRender)
        mBlurRender.addNextRender(mScaleAlphaRender)
        mScaleAlphaRender.addNextRender(mBlendRender)
        mNormalRender.addNextRender(mBlendRender)
        mBlendRender.registerFilterLocation(mNormalRender, 0)
        mBlendRender.registerFilterLocation(mScaleAlphaRender, 1)
        mBlendRender.addNextRender(this)
    }
}