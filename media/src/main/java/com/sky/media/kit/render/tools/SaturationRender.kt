package com.sky.media.kit.render.tools

import android.opengl.GLES20
import com.sky.media.image.core.base.BaseRender
import com.sky.media.image.core.filter.IAdjustable

class SaturationRender : BaseRender, IAdjustable {

    private var mSaturationValue: Float
    private var mSaturationHandle = 0

    constructor(f: Float) {
        var f = f
        if (f < 0.0f) {
            f = 0.0f
        }
        mSaturationValue = f
    }

    constructor() {
        mSaturationValue = 1.0f
    }

    init {
        mFragmentShader = """precision mediump float;
                            uniform sampler2D inputImageTexture;
                            varying vec2 textureCoordinate;
                            uniform float u_Saturation;
                            const vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);
                            void main(){
                                vec4 color = texture2D(inputImageTexture,textureCoordinate);
                                float luminance = dot(color.rgb, luminanceWeighting);
                                vec3 greyScaleColor = vec3(luminance);
                                gl_FragColor = vec4(mix(greyScaleColor, color.rgb, u_Saturation), color.a);
                            }"""

    }

    override fun initShaderHandles() {
        super.initShaderHandles()
        mSaturationHandle = GLES20.glGetUniformLocation(programHandle, "u_Saturation")
    }

    override fun bindShaderValues() {
        super.bindShaderValues()
        GLES20.glUniform1f(mSaturationHandle, mSaturationValue)
    }

    override fun adjust(i: Int, i2: Int, i3: Int) {
        mSaturationValue = i.toFloat() * 1.0f / (i3 - i2).toFloat() + 1.0f
    }
}