package com.sky.media.image.core.filter

import com.sky.media.image.core.base.BaseRender

class MultiAdjuster(basicRender: BaseRender?, i: Int) : Adjuster(basicRender) {
    var ends: IntArray
    protected var mInitProgresses: IntArray
    protected var mLastProgresses: IntArray
    var progresses: IntArray
        protected set
    var starts: IntArray
    var initProgresses: IntArray
        get() = mInitProgresses
        set(iArr) {
            mInitProgresses = iArr
            progresses = iArr
        }

    override fun startAdjust() {
        super.startAdjust()
        mLastProgresses = progresses
    }

    override fun undoAdjust() {
        super.undoAdjust()
        adjust(mLastProgresses)
    }

    override fun resetAdjust() {
        if (render != null) {
            if (mInitProgress != 0) {
                adjust(mInitProgress)
            }
            adjust(mInitProgresses)
            render!!.clearNextRenders()
            render!!.reInitialize()
        }
    }

    override fun initAdjust() {
        if (render != null) {
            if (mInitProgress != 0) {
                adjust(mInitProgress)
            }
            adjust(mInitProgresses)
        }
    }

    fun adjust(iArr: IntArray) {
        progresses = iArr
        if (render is IMultiAdjustable) {
            (render as IMultiAdjustable?)!!.adjust(iArr, starts, ends)
        }
    }

    init {
        progresses = IntArray(i)
        mInitProgresses = IntArray(i)
        mLastProgresses = IntArray(i)
        starts = IntArray(i)
        ends = IntArray(i)
        for (i2 in 0 until i) {
            ends[i2] = 100
        }
    }
}