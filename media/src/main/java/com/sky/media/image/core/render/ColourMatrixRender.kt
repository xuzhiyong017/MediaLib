package com.sky.media.image.core.render

import android.opengl.GLES20
import com.sky.media.image.core.base.BaseRender

open class ColourMatrixRender(fArr: FloatArray, f: Float) : BaseRender() {

    private var colorMatrix: FloatArray
    private var colorMatrixHandle = 0
    private var intensity: Float
    private var intensityHandle = 0

    init {
        mFragmentShader = """precision mediump float;
                            uniform sampler2D inputImageTexture;
                            varying vec2 textureCoordinate;
                            uniform float u_Intensity;
                            uniform mat4 u_ColorMatrix;
                            void main(){
                                vec4 color = texture2D(inputImageTexture,textureCoordinate);
                                vec4 matrixResult = vec4(color.rgb, 1.0) * u_ColorMatrix;
                                vec4 colorResult = u_Intensity * matrixResult + (1.0 - u_Intensity) * color;
                                gl_FragColor = vec4(colorResult.rgb, color.a);
                            }"""
    }

    override fun initShaderHandles() {
        super.initShaderHandles()
        colorMatrixHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_COLOR_MATRIX)
        intensityHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_INTENSITY)
    }

    override fun bindShaderValues() {
        super.bindShaderValues()
        GLES20.glUniformMatrix4fv(colorMatrixHandle, 1, false, colorMatrix, 0)
        GLES20.glUniform1f(intensityHandle, intensity)

    }

    fun setColourMatrix(fArr: FloatArray) {
        colorMatrix = fArr
    }

    fun setIntensity(f: Float) {
        intensity = f
    }

    companion object {
        private const val UNIFORM_COLOR_MATRIX = "u_ColorMatrix"
        private const val UNIFORM_INTENSITY = "u_Intensity"
    }

    init {
        var f2 = 1.0f
        var f3 = 0.0f
        colorMatrix = fArr
        if (f >= 0.0f) {
            f3 = f
        }
        if (f3 <= 1.0f) {
            f2 = f3
        }
        intensity = f2
    }
}