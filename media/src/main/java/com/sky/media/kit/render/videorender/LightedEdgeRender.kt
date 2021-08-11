package com.sky.media.kit.render.videorender

import android.opengl.GLES20
import com.sky.media.image.core.filter.IAdjustable
import com.sky.media.image.core.filter.IRequireProgress
import com.sky.media.image.core.render.EmptyRender
import com.sky.media.image.core.render.GroupRender
import com.sky.media.image.core.render.MultiInputRender

class LightedEdgeRender : GroupRender(), IAdjustable, IRequireProgress {
    private val UNIFORM_DELTA_RADIUS = "deltaRadius"
    private val UNIFORM_IMAGE_SIZE = "imageSize"
    private val mBlendRender: MultiInputRender = object : MultiInputRender(2) {

        override var mFragmentShader: String = """precision highp float;
                    varying highp vec2 textureCoordinate;
                    uniform sampler2D inputImageTexture;
                    uniform sampler2D inputImageTexture2;
                    uniform float deltaRadius;
                    uniform vec2 imageSize;
                    void main(){    
                        vec2 newCoordinate = vec2(textureCoordinate.x * imageSize.x, textureCoordinate.y * imageSize.y);
                        float dis2center = distance(newCoordinate, vec2(0.5 * imageSize.x, 0.5 * imageSize.y));
                        vec4 texColor = texture2D(inputImageTexture, textureCoordinate);
                        vec4 lightTexColor = texture2D(inputImageTexture2, textureCoordinate);
                        if (lightTexColor.r > 0.5) {
                            if (dis2center > -0.3 + deltaRadius && dis2center < -0.15 + deltaRadius) {
                                texColor = mix(texColor, vec4(0.4118, 0.9921, 0.9921, 1.0), abs(dis2center - deltaRadius + 0.225) / 0.075);
                            }else if (dis2center > 0.0 + deltaRadius && dis2center < 0.15 + deltaRadius){
                                texColor = mix(texColor, vec4(0.4118, 0.9921, 0.9921, 1.0), abs(dis2center - deltaRadius - 0.075) / 0.075);
                            }else if (dis2center > 0.3 + deltaRadius && dis2center < 0.45 + deltaRadius){
                                texColor = mix(texColor, vec4(0.4118, 0.9921, 0.9921, 1.0), abs(dis2center - deltaRadius - 0.375) / 0.075);
                            } else {
                                texColor = vec4(0.4118, 0.9921, 0.9921, 1.0);
                            }
                        }
                        gl_FragColor = texColor;
                    }"""

        override fun initShaderHandles() {
            super.initShaderHandles()
            mDeltaRadiusHandler =
                GLES20.glGetUniformLocation(this.programHandle, UNIFORM_DELTA_RADIUS)
            mImageSizeHandler = GLES20.glGetUniformLocation(this.programHandle, UNIFORM_IMAGE_SIZE)
        }


        override fun bindShaderValues() {
            super.bindShaderValues()
            GLES20.glUniform1f(mDeltaRadiusHandler, mDeltaRadius)
            val max = Math.max(getWidth(), getHeight()) as Float
            mImageSize[0] = getWidth() as Float / max
            mImageSize[1] = getHeight() as Float / max
            GLES20.glUniform2fv(mImageSizeHandler, 1, mImageSize, 0)
        }
    }
    private var mDeltaRadius = 0f
    private var mDeltaRadiusHandler = 0
    private val mEmptyRender: EmptyRender = EmptyRender()
    private val mImageSize = FloatArray(2)
    private var mImageSizeHandler = 0
    private val mSedRender: SobelEdgeDetectionRender = SobelEdgeDetectionRenderExt()

    internal inner class SobelEdgeDetectionRenderExt : SobelEdgeDetectionRender() {
        override fun handleSizeChange() {
            super.handleSizeChange()
            texelWidth = 3.0f / getWidth().toFloat()
            texelHeight = 3.0f / getHeight().toFloat()
        }
    }

    override fun adjust(i: Int, i2: Int, i3: Int) {
        mDeltaRadius = (i - i2).toFloat() * 1.0f / (i3 - i2).toFloat() * 0.3f
    }

    override fun setProgress(f: Float) {
        mDeltaRadius = 0.3f * f
    }

    override fun getDuration(): Long {
        return 1800
    }

    init {
        registerInitialFilter(mEmptyRender)
        registerInitialFilter(mSedRender)
        registerTerminalFilter(mBlendRender)
        mEmptyRender.addNextRender(mBlendRender)
        mSedRender.addNextRender(mBlendRender)
        mBlendRender.registerFilterLocation(mEmptyRender, 0)
        mBlendRender.registerFilterLocation(mSedRender, 1)
        mBlendRender.addNextRender(this)
    }
}