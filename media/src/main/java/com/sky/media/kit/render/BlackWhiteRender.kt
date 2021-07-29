package com.sky.media.kit.render

import android.content.Context
import android.opengl.GLES20
import com.sky.media.image.core.cache.ImageBitmapCache
import com.sky.media.image.core.filter.IAdjustable
import com.sky.media.image.core.render.MultiBmpInputRender

/**
 * @author: xuzhiyong
 * @date: 2021/7/29  上午9:01
 * @Email: 18971269648@163.com
 * @description:
 */
class BlackWhiteRender(context: Context): MultiBmpInputRender(ImageBitmapCache.getInstance()),IAdjustable {
    private var mMixValue: Float = 0.0f
    private var mUMixHandle: Int = 0

    init {
        setImages(context, arrayOf("file:///android_asset/filter/localFilter/205/bw2.png"))
        mFragmentShader = """precision lowp float;
                                uniform lowp float u_mix;
                                varying highp vec2 textureCoordinate;
                                uniform sampler2D inputImageTexture;
                                uniform sampler2D inputImageTexture2;
                                void main(){
                                    lowp vec4 sourceImageColor = texture2D(inputImageTexture, textureCoordinate);
                                    vec3 texel = texture2D(inputImageTexture, textureCoordinate).rgb;
                                    texel = vec3(dot(vec3(0.3, 0.6, 0.1), texel));
                                    texel = vec3(texture2D(inputImageTexture2, vec2(texel.r, .16666)).r);
                                    mediump vec4 fragColor = vec4(texel, 1.0);
                                    gl_FragColor = mix(sourceImageColor, fragColor, u_mix);
                                
                                }"""
    }

    override fun initShaderHandles() {
        super.initShaderHandles()
        this.mUMixHandle = GLES20.glGetUniformLocation(programHandle, "u_mix")
    }

    override fun bindShaderValues() {
        super.bindShaderValues()
        GLES20.glUniform1f(mUMixHandle,mMixValue)
    }

    override fun adjust(cur: Int, start: Int, end: Int) {
        mMixValue = cur.toFloat() * 1.0f / (end - start).toFloat()
    }
}