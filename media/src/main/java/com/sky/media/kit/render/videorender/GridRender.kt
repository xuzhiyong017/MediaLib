package com.sky.media.kit.render.videorender

import android.opengl.GLES20
import com.sky.media.image.core.base.BaseRender
import com.sky.media.image.core.filter.IAdjustable
import com.sky.media.image.core.filter.IRequireProgress

open class GridRender : BaseRender(), IAdjustable, IRequireProgress {
    private val UNIFORM_NUM_GRID = "numGrid"
    private var mNumGrid = 2
    private var mNumGridHandle = 0
    private var mProgress = 0f

    override var mFragmentShader: String = """precision highp float;
                                                varying highp vec2 textureCoordinate;
                                                uniform sampler2D inputImageTexture;
                                                uniform int numGrid;
                                                void main(){    
                                                    lowp vec2 newCoordinate;
                                                    lowp float numGridf = float(numGrid);
                                                    lowp float gridWidth = 1.0 / numGridf;
                                                    int i = int(textureCoordinate.x / gridWidth);
                                                    int j = int(textureCoordinate.y / gridWidth);
                                                    newCoordinate = vec2((textureCoordinate.x - gridWidth * float(i)) * numGridf, (textureCoordinate.y - gridWidth * float(j)) * numGridf);
                                                    gl_FragColor = texture2D(inputImageTexture, newCoordinate);
                                                }
                                                    """

    override fun initShaderHandles() {
        super.initShaderHandles()
        mNumGridHandle = GLES20.glGetUniformLocation(this.programHandle, "numGrid")
    }

    override fun bindShaderValues() {
        super.bindShaderValues()
        GLES20.glUniform1i(mNumGridHandle, mNumGrid)
    }


    override fun adjust(i: Int, i2: Int, i3: Int) {
        mProgress = (i - i2).toFloat() * 1.0f / (i3 - i2).toFloat()
        mNumGrid = (mProgress.toDouble() * 3.0).toInt() + 1
    }

    override fun setProgress(f: Float) {
        mProgress = f
        mNumGrid = (mProgress.toDouble() * 3.0).toInt() + 1
    }

    override fun getDuration(): Long {
        return 2000
    }
}