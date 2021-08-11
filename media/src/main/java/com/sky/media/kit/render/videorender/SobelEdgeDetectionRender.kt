package com.sky.media.kit.render.videorender

import com.sky.media.image.core.render.CompositeMultiPixelRender

open class SobelEdgeDetectionRender : CompositeMultiPixelRender(1) {

    init {
        val greyScaleRender = GreyScaleRender()
        greyScaleRender.addNextRender(this)
        registerFilterLocation(greyScaleRender)
        registerInitialFilter(greyScaleRender)
        registerTerminalFilter(greyScaleRender)

        mFragmentShader = """precision mediump float;
                            uniform sampler2D inputImageTexture;
                            varying vec2 textureCoordinate;
                            uniform float u_TexelWidth;
                            uniform float u_TexelHeight;
                            void main(){
                                vec2 up = vec2(0.0, u_TexelHeight);
                                vec2 right = vec2(u_TexelWidth, 0.0);
                                float bottomLeftIntensity = texture2D(inputImageTexture, textureCoordinate - up - right).r;
                                float topRightIntensity = texture2D(inputImageTexture, textureCoordinate + up + right).r;
                                float topLeftIntensity = texture2D(inputImageTexture, textureCoordinate + up - right).r;
                                float bottomRightIntensity = texture2D(inputImageTexture, textureCoordinate - up + right).r;
                                float leftIntensity = texture2D(inputImageTexture, textureCoordinate - right).r;
                                float rightIntensity = texture2D(inputImageTexture, textureCoordinate + right).r;
                                float bottomIntensity = texture2D(inputImageTexture, textureCoordinate - up).r;
                                float topIntensity = texture2D(inputImageTexture, textureCoordinate + up).r;
                                float h = -topLeftIntensity - 2.0 * topIntensity - topRightIntensity + bottomLeftIntensity + 2.0 * bottomIntensity + bottomRightIntensity;
                                float v = -bottomLeftIntensity - 2.0 * leftIntensity - topLeftIntensity + bottomRightIntensity + 2.0 * rightIntensity + topRightIntensity;
                                float mag = length(vec2(h, v));
                                gl_FragColor = vec4(vec3(mag), 1.0);
                            }"""
        }
}