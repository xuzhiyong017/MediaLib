package com.sky.media.image.core.filter

import android.content.Context
import com.sky.media.image.core.base.BaseRender

open class Adjuster(var render: BaseRender?) {
    var context: Context? = null
    var end = 100
    @JvmField
    protected var mInitProgress = 0
    protected var mLastProgress = 0
    var progress = 0
        protected set
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

    open fun startAdjust() {
        mLastProgress = progress
    }

    open fun undoAdjust() {
        adjust(mLastProgress)
    }

    open fun resetAdjust() {
        if (render != null) {
            adjust(mInitProgress)
            render!!.clearNextRenders()
            render!!.reInitialize()
        }
    }

    open fun initAdjust() {
        if (render != null) {
            adjust(mInitProgress)
        }
    }

    open fun adjust(i: Int) {
        progress = i
        if (render is IAdjustable) {
            (render as IAdjustable?)!!.adjust(i, start, end)
        }
    }
}