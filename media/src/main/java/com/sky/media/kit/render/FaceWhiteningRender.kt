package com.sky.media.kit.render

import android.opengl.GLES20
import com.sky.media.image.core.base.BaseRender
import com.sky.media.image.core.filter.IAdjustable

/**
 * @author: xuzhiyong
 * @date: 2021/7/28  下午6:00
 * @Email: 18971269648@163.com
 * @description:
 */
class FaceWhiteningRender: BaseRender(),IAdjustable {

    private val UNIFORM_WHITENING_LEVEL = "u_mix"
    private var mWhiteningLevel = 0f
    private var mWhiteningLevelHandle = 0

    init {
        mFragmentShader = """
            varying highp vec2 textureCoordinate;
            uniform sampler2D inputImageTexture;
            uniform lowp float u_mix; 
            void main(){
               lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);   
               gl_FragColor = vec4((textureColor.rgb + vec3(u_mix*0.1)), textureColor.a);
            }
            """
    }

    override fun initShaderHandles() {
        super.initShaderHandles()
        mWhiteningLevelHandle = GLES20.glGetUniformLocation(programHandle,UNIFORM_WHITENING_LEVEL)
    }

    override fun bindShaderValues() {
        super.bindShaderValues()
        GLES20.glUniform1f(mWhiteningLevelHandle,mWhiteningLevel)
    }

    override fun adjust(i: Int, start: Int, end: Int) {
        mWhiteningLevel = (i - start).toFloat() * 1.0f / (end - start).toFloat()
    }
}