package com.sky.media.kit.render

import android.opengl.GLES20
import com.sky.media.image.core.base.BaseRender
import com.sky.media.image.core.filter.IAdjustable

/**
 * @author: xuzhiyong
 * @date: 2021/7/28  下午6:15
 * @Email: 18971269648@163.com
 * @description:
 */
class HighlightShadowRender(var hightligntValue:Float,var shadowValue:Float) : BaseRender(),IAdjustable {

    private var mHighlightHandle = 0
    private var mShadowHandle = 0

    init {
        mFragmentShader = """precision mediump float;
                            uniform sampler2D inputImageTexture;
                            uniform float u_Highlight;
                            uniform float u_Shadow;
                            varying vec2 textureCoordinate;
                            const vec3 luminanceWeighting = vec3(0.3, 0.3, 0.3);
                            void main(){
                              vec4 texColour = texture2D(inputImageTexture,textureCoordinate);
                              float luminance = dot(texColour.rgb, luminanceWeighting);
                              float s = clamp((pow(luminance, 1.0/(u_Shadow+1.0)) + (-0.36)*pow(luminance, 2.0/(u_Shadow+1.0))) - luminance, 0.0, 1.0);
                              float h = clamp((1.0 - (pow(1.0-luminance, 1.0/(2.0-u_Highlight)) + (-0.8)*pow(1.0-luminance, 2.0/(2.0-u_Highlight)))) - luminance, -1.0, 0.0);
                              vec3 result = vec3(0.0, 0.0, 0.0) + ((luminance + s + h) - 0.0) * ((texColour.rgb - vec3(0.0, 0.0, 0.0))/(luminance - 0.0));
                              gl_FragColor = vec4(result, texColour.a);
                            }
                            """
    }

    override fun initShaderHandles() {
        super.initShaderHandles()
        mHighlightHandle = GLES20.glGetUniformLocation(programHandle, "u_Highlight")
        mShadowHandle = GLES20.glGetUniformLocation(programHandle, "u_Shadow")
    }

    override fun bindShaderValues() {
        super.bindShaderValues()
        GLES20.glUniform1f(mHighlightHandle, hightligntValue)
        GLES20.glUniform1f(mShadowHandle, shadowValue)
    }


    override fun adjust(cur: Int, start: Int, end: Int) {
        shadowValue = cur.toFloat() * 1.0f / (end - start).toFloat() / 2.5f
    }
}