package com.sky.media.image.core.render

import android.opengl.GLES20
import com.sky.media.image.core.base.BaseRender
import com.sky.media.image.core.filter.IAdjustable
import java.util.*

class SwitchRender : GroupRender(), IAdjustable {
    private val UNIFORM_PROGRESS = "progress"

    private val mBlendRender: MultiInputRender = object : MultiInputRender(2) {

        init {
            mFragmentShader = """precision highp float;
                                varying highp vec2 textureCoordinate;
                                uniform sampler2D inputImageTexture;
                                uniform sampler2D inputImageTexture2;
                                uniform float progress;
                                void main(){
                                    if (textureCoordinate.x < progress){
                                        gl_FragColor = texture2D(inputImageTexture, textureCoordinate);    
                                    } else{
                                        gl_FragColor = texture2D(inputImageTexture2, textureCoordinate);  
                                    }
                                }
                                """
        }
        override fun initShaderHandles() {
            super.initShaderHandles()
            mProgressHandler = GLES20.glGetUniformLocation(programHandle, "progress")
        }

        override fun bindShaderValues() {
            super.bindShaderValues()
            GLES20.glUniform1f(mProgressHandler, mProgress)
        }
    }
    private var mLeftRender: BaseRender? = null
    private var mProgress = 1.0f
    private var mProgressHandler = 0
    private var mRightRender: BaseRender? = null
    fun setRenders(basicRender: BaseRender?, basicRender2: BaseRender?): ArrayList<BaseRender?>? {
        if (mLeftRender == basicRender && mRightRender == basicRender2 || basicRender == null || basicRender2 == null) {
            return null
        }
        mBlendRender.clearRegisteredFilterLocations()
        val arrayList: ArrayList<BaseRender?> = ArrayList<BaseRender?>()
        if (mLeftRender != null) {
            initialFilters.remove(mLeftRender)
            mLeftRender!!.removeRenderIn(mBlendRender)
            if (!(mLeftRender == basicRender || mLeftRender == basicRender2)) {
                arrayList.add(mLeftRender)
            }
        }
        if (mRightRender != null) {
            initialFilters.remove(mRightRender)
            mRightRender!!.removeRenderIn(mBlendRender)
            if (!(mRightRender == basicRender || mRightRender == basicRender2)) {
                arrayList.add(mRightRender)
            }
        }
        registerInitialFilter(basicRender)
        registerInitialFilter(basicRender2)
        basicRender.addNextRender(mBlendRender)
        basicRender2.addNextRender(mBlendRender)
        mBlendRender.registerFilterLocation(basicRender, 0)
        mBlendRender.registerFilterLocation(basicRender2, 1)
        mLeftRender = basicRender
        mRightRender = basicRender2
        return arrayList
    }

    val currentRender: BaseRender?
        get() = if (mProgress > 0.0f) {
            mLeftRender
        } else mRightRender

    override fun adjust(i: Int, i2: Int, i3: Int) {
        mProgress = (i - i2).toFloat() * 1.0f / (i3 - i2).toFloat()
    }

    init {
        registerTerminalFilter(mBlendRender)
        mBlendRender.addNextRender(this)
    }
}