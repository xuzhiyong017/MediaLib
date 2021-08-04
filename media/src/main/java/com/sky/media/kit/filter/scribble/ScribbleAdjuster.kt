package com.sky.media.kit.filter.scribble

import android.content.Context
import com.sky.media.image.core.filter.Adjuster
import android.graphics.Bitmap
import com.sky.media.image.core.base.BaseRender

class ScribbleAdjuster(override var context: Context?,val type: Int) : Adjuster(null) {
    private var startBitmap: Bitmap? = null
    private var mPaintBitmap: Bitmap? = null
    private var mSourceBitmap: Bitmap? = null
    private var mReplaceFilter: PixelReplaceFilter? = null
    private var mBlendFilter: PixelBlendFilter? = null


    fun setBitmap(bitmap: Bitmap?, bitmap2: Bitmap?) {
        mPaintBitmap = bitmap
        mSourceBitmap = bitmap2
        if (type == 0) {
            blendFilter!!.setSourceBitmap(mPaintBitmap, mSourceBitmap)
        } else {
            replaceFilter!!.setSourceBitmap(mPaintBitmap)
        }
    }

    override var mRender : BaseRender? = null
        get() {
            if (mPaintBitmap == null) {
                return null
            }
            return if (type == 0) {
                blendFilter
            } else replaceFilter
        }

    val render: BaseRender?
        get() {
            if (mPaintBitmap == null) {
                return null
            }
            return if (type == 0) {
                blendFilter
            } else replaceFilter
        }
    val replaceFilter: PixelReplaceFilter?
        get() {
            if (mReplaceFilter == null) {
                mReplaceFilter = PixelReplaceFilter(context)
            }
            return mReplaceFilter
        }
    val blendFilter: PixelBlendFilter?
        get() {
            if (mBlendFilter == null) {
                mBlendFilter = PixelBlendFilter()
            }
            return mBlendFilter
        }

    override fun startAdjust() {
        if (mPaintBitmap != null) {
            startBitmap = Bitmap.createBitmap(mPaintBitmap!!)
        }
    }

    override fun undoAdjust() {
        if (startBitmap != null) {
            mPaintBitmap = Bitmap.createBitmap(startBitmap!!)
            if (type == 0) {
                blendFilter!!.setSourceBitmap(mPaintBitmap, mSourceBitmap)
            } else {
                replaceFilter!!.setSourceBitmap(mPaintBitmap)
            }
        }
    }

    override fun resetAdjust() {
        mPaintBitmap = null
        mSourceBitmap = null
        startBitmap = null
        if (mReplaceFilter != null) {
            mReplaceFilter?.clearNextRenders()
            mReplaceFilter?.reInitialize()
        }
        if (mBlendFilter != null) {
            mBlendFilter?.clearNextRenders()
            mBlendFilter?.reInitialize()
        }
    }

}