package com.sky.media.image.core.render

import android.opengl.GLES20

class GaussianBlurRender(var blurSize: Float) : TwoPassMultiPixelRender() {

    init {
        mFragmentShader = """precision mediump float;
                            uniform sampler2D inputImageTexture;
                            varying vec2 textureCoordinate;
                            uniform float u_BlurSize;
                            uniform float u_TexelWidth;
                            uniform float u_TexelHeight;
                            void main(){
                                vec2 singleStepOffset = vec2(u_TexelWidth*u_BlurSize, u_TexelHeight*u_BlurSize);
                                int multiplier = 0;
                                vec2 blurStep = vec2(0,0);
                                vec2 blurCoordinates[9];   
                                for(int i = 0; i < 9; i++) {
                                    multiplier = (i - 4);
                                    blurStep = float(multiplier) * singleStepOffset;
                                    blurCoordinates[i] = textureCoordinate.xy + blurStep;
                                }
                                vec3 sum = vec3(0,0,0);
                                vec4 color = texture2D(inputImageTexture, blurCoordinates[4]);
                                sum += texture2D(inputImageTexture, blurCoordinates[0]).rgb * 0.05;
                                sum += texture2D(inputImageTexture, blurCoordinates[1]).rgb * 0.09;
                                sum += texture2D(inputImageTexture, blurCoordinates[2]).rgb * 0.12;
                                sum += texture2D(inputImageTexture, blurCoordinates[3]).rgb * 0.15;
                                sum += color.rgb * 0.18;
                                sum += texture2D(inputImageTexture, blurCoordinates[5]).rgb * 0.15;
                                sum += texture2D(inputImageTexture, blurCoordinates[6]).rgb * 0.12;
                                sum += texture2D(inputImageTexture, blurCoordinates[7]).rgb * 0.09;
                                sum += texture2D(inputImageTexture, blurCoordinates[8]).rgb * 0.05;
                                gl_FragColor = vec4(sum, color.a);
                            }"""
    }
    private var blurSizeHandle = 0

    override fun initShaderHandles() {
        super.initShaderHandles()
        blurSizeHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_BLUR_SIZE)
    }

    override fun bindShaderValues() {
        super.bindShaderValues()
        GLES20.glUniform1f(blurSizeHandle, blurSize)
    }

    companion object {
        private const val UNIFORM_BLUR_SIZE = "u_BlurSize"
    }
}