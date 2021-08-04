package com.sky.media.image.core.filter

import android.content.Context
import com.sky.media.image.core.base.BaseRender

open class Adjuster(open var mRender: BaseRender?) {

    open var context: Context? = null
    var end = 100
    @JvmField
    protected var mInitProgress = 0
    protected var mLastProgress = 0
    open var progress = 0
    var start = 0
    val progressText: String
        get() {
            if (progress == 0) {
                return ""
            }
            return if (progress > 0) {
                "+$progress"
            } else "" + progress
        }
    var initProgress: Int
        get() = mInitProgress
        set(i) {
            mInitProgress = i
            progress = i
        }

    open fun setRender(render:BaseRender){
        mRender = render
    }

    open fun startAdjust() {
        mLastProgress = progress
    }

    open fun undoAdjust() {
        adjust(mLastProgress)
    }

    open fun resetAdjust() {
        if (mRender != null) {
            adjust(mInitProgress)
            mRender!!.clearNextRenders()
            mRender!!.reInitialize()
        }
    }

    open fun initAdjust() {
        if (mRender != null) {
            adjust(mInitProgress)
        }
    }

    open fun adjust(i: Int) {
        progress = i
        if (mRender is IAdjustable) {
            (mRender as IAdjustable?)!!.adjust(i, start, end)
        }
    }
}