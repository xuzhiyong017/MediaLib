package com.sky.medialib.ui.camera.process

import android.graphics.Bitmap
import com.sky.media.image.core.filter.Adjuster
import com.sky.media.image.core.filter.Filter
import com.sky.media.image.core.out.BitmapOutput.*
import com.sky.media.image.core.render.EmptyRender
import com.sky.media.image.core.view.IContainerView
import com.sky.media.image.core.view.IRenderView
import com.sky.media.kit.camera.CameraProcess
import com.sky.media.kit.model.FilterExt

/**
 * @author: xuzhiyong
 * @date: 2021/8/6  上午10:07
 * @Email: 18971269648@163.com
 * @description:
 */
class CameraProcessExt(iContainerView: IContainerView, iRenderView: IRenderView)
    :CameraProcess(iContainerView,iRenderView) {

    private var switchFilter: FilterExt? = null
    private var mEffectFilter: FilterExt? = null


    override fun addFilter(filter: Filter?) {
        if (!mUsedFilters.contains(filter)) {
            if (mEffectFilter != null) {
                mUsedFilters.add(mUsedFilters.size - 1, filter)
            } else {
                mUsedFilters.add(filter)
            }
        }
        refreshAllFilters()
    }

    fun takePhoto(bitmapOutputCallback: BitmapOutputCallback, i: Int, i2: Int, z: Boolean) {
        if (z || !isDrawFilter()) {
            getOutputBitmap(object : BitmapOutputCallback {
                override fun bitmapOutput(bitmap: Bitmap?) {
                    bitmapOutputCallback.bitmapOutput(bitmap)
                }
            }, i, i2)
        } else {
            getOutputBitmap(object : BitmapOutputCallback {
                override fun bitmapOutput(bitmap: Bitmap?) {
                    bitmapOutputCallback.bitmapOutput(bitmap)
                }
            }, i, i2, getPrevRender(switchFilter!!))
        }
    }

    fun getSwitchFilter(): FilterExt? {
        return switchFilter
    }

    private fun isDrawFilter(): Boolean {
        if (switchFilter == null) {
            return false
        }
        val adjuster: Adjuster? = switchFilter!!.adjuster
        return !(adjuster == null || adjuster.mRender is EmptyRender)
    }

    fun switchFilter(filterExt: FilterExt) {
        if (switchFilter !== filterExt) {
            if (switchFilter != null) {
                mUsedFilters.remove(switchFilter)
            }
            switchFilter = filterExt
            addFilter(filterExt as Filter)
        }
    }

    fun getEffectFilterExt(): FilterExt? {
        return mEffectFilter
    }

    fun replaceEffectFilter(filterExt: FilterExt) {
        if (mEffectFilter !== filterExt) {
            if (mEffectFilter != null) {
                mUsedFilters.remove(mEffectFilter)
                mEffectFilter = null
            }
            addFilter(filterExt as Filter)
            mEffectFilter = filterExt
        }
    }

    fun cancelEffectFilter() {
        if (mEffectFilter != null) {
            removeFilter(mEffectFilter!!)
        }
        mEffectFilter = null
    }
}