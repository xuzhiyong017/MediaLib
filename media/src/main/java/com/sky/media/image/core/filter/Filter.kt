package com.sky.media.image.core.filter

import android.content.Context
import java.util.*

open class Filter {
    protected var mAdjuster: Adjuster? = null
    open var context: Context? = null
    protected var mExtra: MutableMap<String, String> = HashMap<String, String>()
    open var icon: String? = null
    open var name: String? = null
    open var adjuster: Adjuster?
        get() = mAdjuster
        set(adjuster) {
            mAdjuster = adjuster
            if (mAdjuster != null) {
                mAdjuster!!.initAdjust()
            }
        }

    fun putExtra(str: String, str2: String) {
        mExtra[str] = str2
    }

    fun getExtra(str: String): String? {
        return mExtra[str]
    }

    fun clearExtra() {
        mExtra.clear()
    }

    fun startTool() {
        if (mAdjuster != null) {
            mAdjuster!!.startAdjust()
        }
    }

    fun undoTool() {
        if (mAdjuster != null) {
            mAdjuster!!.undoAdjust()
        }
    }
}