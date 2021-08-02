package com.sky.media.kit.render.tools

import android.opengl.GLES20
import com.sky.media.image.core.base.BaseRender
import com.sky.media.image.core.filter.IAdjustable

class WhiteBalanceRender(private var mTemperatureValue: Float, private val mTintValue: Float) :
    BaseRender(), IAdjustable {
    private var mTemperatureHandle = 0
    private var mTintHandle = 0

    init {
        mFragmentShader = """ uniform sampler2D inputImageTexture;
                             varying highp vec2 textureCoordinate;
                             uniform lowp float u_Temperature;
                             uniform lowp float u_Tint;
                             const lowp vec3 warmFilter = vec3(0.93, 0.54, 0.0);
                             const mediump mat3 RGBtoYIQ = mat3(0.299, 0.587, 0.114, 0.596, -0.274, -0.322, 0.212, -0.523, 0.311);
                             const mediump mat3 YIQtoRGB = mat3(1.0, 0.956, 0.621, 1.0, -0.272, -0.647, 1.0, -1.105, 1.702);
                             void main(){
                                 lowp vec4 source = texture2D(inputImageTexture, textureCoordinate);
                                 mediump vec3 yiq = RGBtoYIQ * source.rgb; //adjusting tint
                                 yiq.b = clamp(yiq.b + u_Tint*0.5226*0.1, -0.5226, 0.5226);
                                 lowp vec3 rgb = YIQtoRGB * yiq;
                                 lowp vec3 processed = vec3(
                                     (rgb.r < 0.5 ? (2.0 * rgb.r * warmFilter.r) : (1.0 - 2.0 * (1.0 - rgb.r) * (1.0 - warmFilter.r))), //adjusting temperature
                                     (rgb.g < 0.5 ? (2.0 * rgb.g * warmFilter.g) : (1.0 - 2.0 * (1.0 - rgb.g) * (1.0 - warmFilter.g))), 
                                     (rgb.b < 0.5 ? (2.0 * rgb.b * warmFilter.b) : (1.0 - 2.0 * (1.0 - rgb.b) * (1.0 - warmFilter.b))));
                                gl_FragColor = vec4(mix(rgb, processed, u_Temperature), source.a);
                            }
                            """
    }

    override fun initShaderHandles() {
        super.initShaderHandles()
        mTemperatureHandle = GLES20.glGetUniformLocation(programHandle, "u_Temperature")
        mTintHandle = GLES20.glGetUniformLocation(programHandle, "u_Tint")
    }

    override fun bindShaderValues() {
        super.bindShaderValues()
        GLES20.glUniform1f(mTemperatureHandle, mTemperatureValue)
        GLES20.glUniform1f(mTintHandle, mTintValue)
    }

    override fun adjust(i: Int, i2: Int, i3: Int) {
        mTemperatureValue = i.toFloat() * 1.0f / (i3 - i2).toFloat()
    }
}