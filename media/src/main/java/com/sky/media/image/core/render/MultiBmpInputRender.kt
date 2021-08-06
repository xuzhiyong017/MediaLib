package com.sky.media.image.core.render

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLES20
import android.text.TextUtils
import com.sky.media.image.core.base.BaseRender
import com.sky.media.image.core.base.TextureOutRender
import com.sky.media.image.core.cache.IBitmapCache
import com.sky.media.image.core.util.BitmapUtil
import com.sky.media.image.core.util.TextureBindUtil
import java.util.*

/**
 * @author: xuzhiyong
 * @date: 2021/7/29  上午10:18
 * @Email: 18971269648@163.com
 * @description:
 */
open class MultiBmpInputRender(val mBitmapCache:IBitmapCache):BaseRender() {

    protected open var mContext: Context? = null
    protected var mPaths: Array<String?>? = null
    protected var mResources: IntArray? = null
    protected var mTextureHandles: IntArray? = null
    protected var mTextureNum = 1
    protected var mTextures: IntArray? = null

    fun setImages(context: Context?, iArr: IntArray?) {
        mContext = context
        if (iArr != null && !Arrays.equals(iArr, mResources)) {
            mTextureNum = iArr.size + 1
            mTextureHandles = IntArray(iArr.size)
            mTextures = IntArray(iArr.size)
            mResources = iArr
        }
    }

    fun setImages(context: Context?, strArr: Array<String?>?) {
        mContext = context
        if (strArr != null && !Arrays.equals(strArr, mPaths)) {
            mTextureNum = strArr.size + 1
            mTextureHandles = IntArray(strArr.size)
            mTextures = IntArray(strArr.size)
            mPaths = strArr
        }
    }

    protected fun destroyTextures() {
        if (mTextures != null) {
            for (i in mTextures!!.indices) {
                if (mTextures!![i] != 0) {
                    GLES20.glDeleteTextures(1, intArrayOf(mTextures!![i]), 0)
                    mTextures!![i] = 0
                }
            }
        }
    }

    override fun destroy() {
        super.destroy()
        destroyTextures()
    }

    override fun dealNextTexture(gLTextureOutputRenderer: TextureOutRender, i: Int, z: Boolean) {
        if (z) {
            markNeedDraw()
        }
        texture_in = i
        if (mTextureNum > 1) {
            for (i2 in mTextures!!.indices) {
                if (mTextures!![i2] == 0) {
                    mTextures!![i2] = TextureBindUtil.bindBitmap(getBitmap(i2))
                }
            }
        }
        setWidth(gLTextureOutputRenderer.getWidth())
        setHeight(gLTextureOutputRenderer.getHeight())
        onDrawFrame()
    }

    protected fun getBitmap(i: Int): Bitmap? {
        var str = ""
        if (mResources != null) {
            str = BitmapUtil.Scheme.DRAWABLE.wrap("" + mResources!![i])
        } else if (mPaths != null) {
            str = mPaths!![i].toString()
        }
        if (mBitmapCache == null) {
            return BitmapUtil.loadBitmap(mContext!!, str)
        }
        var bitmap: Bitmap? = mBitmapCache.get(str)
        if (bitmap != null && !bitmap.isRecycled) {
            return bitmap
        }
        bitmap = BitmapUtil.loadBitmap(mContext!!, str)
        if (bitmap == null) {
            return bitmap
        }
        mBitmapCache.put(str, bitmap)
        return bitmap
    }

    override fun initShaderHandles() {
        super.initShaderHandles()
        for (i in 0 until mTextureNum - 1) {
            mTextureHandles?.set(i,
                GLES20.glGetUniformLocation(programHandle, "inputImageTexture" + (i + 2))
            )
        }
    }

    override fun bindShaderValues() {
        super.bindShaderValues()
        for (i in 0 until mTextureNum - 1) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1 + i)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures!![i])
            GLES20.glUniform1i(mTextureHandles!![i], i + 1)
        }
    }

}