package com.sky.media.kit.filter.scribble

import android.content.Context
import android.graphics.*
import android.opengl.GLES20
import android.opengl.GLUtils
import com.sky.media.R
import com.sky.media.image.core.base.BaseRender
import com.sky.media.image.core.base.TextureOutRender

class PixelReplaceFilter(context: Context?) : BaseRender() {
    private val texturesHandle = IntArray(2)
    private val textures = IntArray(2)
    private val bitmaps: Array<Bitmap?>? = arrayOfNulls<Bitmap>(2)
    private val textureCount = 3
    private var bitmapWidth = 0
    private var bitmapHeight = 0
    private var mWidthRValue = 0f
    private var mHeightRValue = 0f
    private val mBrushValue: Float
    private var mWidthRHandle = 0
    private var mHeightRhandle = 0
    private var mBrushNumHandle = 0
    private val U_WIDTHR = "u_widthr"
    private val U_HEIGHTR = "u_heightr"
    private val U_BRUSH_NUM = "u_brush_num"


    init {
        mFragmentShader = """precision highp float;
                            uniform sampler2D inputImageTexture;
                            uniform sampler2D inputImageTexture1;
                            uniform sampler2D inputImageTexture2;
                            uniform float u_widthr;
                            uniform float u_heightr;
                            uniform float u_brush_num;
                            varying vec2 textureCoordinate;
                            void main(){
                                highp vec2 textureCoord = vec2(textureCoordinate.x,1.0-textureCoordinate.y);
                                highp vec2 brushCoord = vec2(fract(textureCoord.x*u_widthr),textureCoord.y*u_heightr);
                                brushCoord.x = clamp(brushCoord.x,0.01,1.0);
                                vec4 originalImageColor = texture2D(inputImageTexture, textureCoordinate);
                                vec4 maskImageColor = texture2D(inputImageTexture2, textureCoord );
                                if(maskImageColor.r > 0.0){
                                    if(maskImageColor.r > 0.8){
                                        brushCoord.x= (6.0+brushCoord.x)/u_brush_num;
                                    }else if(maskImageColor.r > 0.3){
                                        brushCoord.x= (5.0+brushCoord.x)/u_brush_num;
                                    }else{
                                        brushCoord.x= (4.0+brushCoord.x)/u_brush_num;
                                    }  
                                }else if(maskImageColor.g > 0.0){
                                    if(maskImageColor.g > 0.8){
                                        brushCoord.x= (3.0+brushCoord.x)/u_brush_num;
                                    }else if(maskImageColor.g > 0.3){
                                        brushCoord.x= (2.0+brushCoord.x)/u_brush_num;
                                    }else{
                                        brushCoord.x= (1.0+brushCoord.x)/u_brush_num;
                                    }  
                                }else if(maskImageColor.b > 0.8){
                                    brushCoord.x= (brushCoord.x)/u_brush_num;
                                }else{
                                    // 橡皮擦
                                    gl_FragColor = originalImageColor;
                                    return;   
                                }
                                gl_FragColor = texture2D(inputImageTexture1, brushCoord);
                            }
                                
                            """
    }

    override fun destroy() {
        super.destroy()
        for (i in textures.indices) {
            if (textures[i] != 0) {
                GLES20.glDeleteTextures(1, intArrayOf(textures[i]), 0)
                textures[i] = 0
            }
        }
    }

    override fun dealNextTexture(gLTextureOutputRenderer: TextureOutRender,i: Int,  z: Boolean) {
        if (z) {
            markNeedDraw()
        }
        this.texture_in = i
        var i2 = 0
        while (i2 < textures.size) {
            if (textures[i2] != 0) {
                GLES20.glDeleteTextures(1, intArrayOf(textures[i2]), 0)
            }
            if (!(bitmaps!![i2] == null || bitmaps[i2]?.isRecycled == true)) {
                textures[i2] = bindBitmap(bitmaps[i2])
            }
            i2++
        }
        mWidthRValue = width * 1.0f / bitmapWidth.toFloat()
        mHeightRValue = height * 1.0f / bitmapHeight.toFloat()
        width = gLTextureOutputRenderer.width
        height = gLTextureOutputRenderer.height
        onDrawFrame()
    }

    override fun initShaderHandles() {
        super.initShaderHandles()
        for (i in 0 until textureCount - 1) {
            texturesHandle[i] =
                GLES20.glGetUniformLocation(this.programHandle, "inputImageTexture" + (i + 1))
        }
        mWidthRHandle = GLES20.glGetUniformLocation(this.programHandle, U_WIDTHR)
        mHeightRhandle = GLES20.glGetUniformLocation(this.programHandle, U_HEIGHTR)
        mBrushNumHandle = GLES20.glGetUniformLocation(this.programHandle, U_BRUSH_NUM)
    }

    override fun bindShaderValues() {
        super.bindShaderValues()
        for (i in 0 until textureCount - 1) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1 + i)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[i])
            GLES20.glUniform1i(texturesHandle[i], i + 1)
        }
        GLES20.glUniform1f(mWidthRHandle, mWidthRValue)
        GLES20.glUniform1f(mHeightRhandle, mHeightRValue)
        GLES20.glUniform1f(mBrushNumHandle, mBrushValue)
    }


    fun setSourceBitmap(bitmap: Bitmap?) {
        if (bitmap != null && !bitmap.isRecycled) {
            bitmaps!![1] = bitmap
        }
    }

    companion object {
        fun bindBitmap(bitmap: Bitmap?): Int {
            val iArr = IntArray(1)
            GLES20.glGenTextures(1, iArr, 0)
            GLES20.glBindTexture(3553, iArr[0])
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT)
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
            return iArr[0]
        }
    }

    init {
        val iArr = intArrayOf(
            R.drawable.brush_aixin,
            R.drawable.brush_caisedian,
            R.drawable.brush_houzi,
            R.drawable.brush_shuye,
            R.drawable.brush_songshu,
            R.drawable.brush_tanhao,
            R.drawable.brush_xingxing
        )
        mBrushValue = iArr.size.toFloat()
        var paint: Paint? = null
        var canvas: Canvas? = null
        var bitmap: Bitmap? = null
        var i = 0
        while (i.toFloat() < mBrushValue) {
            val decodeResource: Bitmap =
                BitmapFactory.decodeResource(context?.resources, iArr[i], null)
            if (bitmap == null) {
                bitmapWidth = decodeResource.width
                bitmapHeight = decodeResource.height
                bitmap = Bitmap.createBitmap(
                    bitmapWidth * iArr.size,
                    bitmapHeight,
                    Bitmap.Config.ARGB_8888
                )
                canvas = Canvas(bitmap)
                paint = Paint()
            }
            if (bitmaps != null) {
                canvas!!.drawBitmap(
                    decodeResource,
                    null,
                    Rect(bitmapWidth * i, 0, bitmapWidth * (i + 1), bitmapHeight),
                    paint
                )
            }
            i++
        }
        bitmaps!![0] = bitmap
    }
}