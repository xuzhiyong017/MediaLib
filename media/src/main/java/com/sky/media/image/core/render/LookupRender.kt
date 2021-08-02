package com.sky.media.image.core.render

import android.content.Context
import android.opengl.GLES20
import com.sky.media.image.core.cache.IBitmapCache
import com.sky.media.image.core.cache.ImageBitmapCache
import com.sky.media.image.core.filter.IAdjustable

/**
 * @author: xuzhiyong
 * @date: 2021/8/2  下午2:28
 * @Email: 18971269648@163.com
 * @description:
 */
class LookupRender(context: Context?, mLutId: Int, iBitmapCache: IBitmapCache = ImageBitmapCache.getInstance()) : MultiBmpInputRender(iBitmapCache),IAdjustable {

    private val UNIFORM_MIX = "u_mix"
    private var mix = 0f
    private var mixHandle = 0

    init {
        setImages(context, intArrayOf(mLutId))
        mFragmentShader = """precision highp float;
                            varying highp vec2 textureCoordinate;
                            uniform sampler2D inputImageTexture;
                            uniform sampler2D inputImageTexture2;
                            uniform mediump float u_mix;
                                // lookup.jpg 会丢失一些精度，格子边界的值可能会跳到下一个格子，需要用minLimit和maxLimit限制一下
                            const highp float minLimit = 0.01;
                            const highp float maxLimit = 0.99;
                            const highp float size = 12.0;
                            const highp float aa = 1.0 / size;
                            const highp float bb = 0.5 / 1728.0;
                            void main()
                            {
                                highp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);
                                highp float blueColor = clamp(textureColor.b, minLimit, maxLimit) * (size * size - 1.0);
                                highp vec2 quad1;
                                quad1.y = floor(floor(blueColor) * aa);
                                quad1.x = floor(blueColor) - (quad1.y * size);
                                highp vec2 quad2;
                                quad2.y = floor(ceil(blueColor) * aa);
                                quad2.x = ceil(blueColor) - (quad2.y * size);
                                highp vec2 texPos1;
                                texPos1.x = (quad1.x * aa) + bb + ((aa - bb * 2.0) * clamp(textureColor.r, minLimit, maxLimit));
                                texPos1.y = (quad1.y * aa) + bb + ((aa - bb * 2.0) * clamp(textureColor.g, minLimit, maxLimit));
                                highp vec2 texPos2;
                                texPos2.x = (quad2.x * aa) + bb + ((aa - bb * 2.0) * clamp(textureColor.r, minLimit, maxLimit));
                                texPos2.y = (quad2.y * aa) + bb + ((aa - bb * 2.0) * clamp(textureColor.g, minLimit, maxLimit));
                                highp vec4 newColor1 = texture2D(inputImageTexture2, texPos1);
                                highp vec4 newColor2 = texture2D(inputImageTexture2, texPos2);
                                highp vec4 newColor = mix(newColor1, newColor2, fract(blueColor));
                                highp vec4 fragColor = vec4(newColor.rgb, textureColor.w);
                                gl_FragColor = mix(textureColor, fragColor, u_mix);
                            }"""
    }


    override fun initShaderHandles() {
        super.initShaderHandles()
        mixHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_MIX)
    }

    override fun bindShaderValues() {
        super.bindShaderValues()
        GLES20.glUniform1f(mixHandle, mix)
    }



    override fun adjust(i: Int, i2: Int, i3: Int) {
        mix = (i - i2).toFloat() * 1.0f / (i3 - i2).toFloat()
    }
}