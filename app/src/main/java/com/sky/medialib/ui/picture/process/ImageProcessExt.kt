package com.sky.medialib.ui.picture.process

import com.sky.media.image.core.filter.Adjuster
import com.sky.media.image.core.filter.Filter
import com.sky.media.image.core.process.base.ImageProcess
import com.sky.media.image.core.render.EmptyRender
import com.sky.media.image.core.view.IContainerView
import com.sky.media.image.core.view.IRenderView
import com.sky.media.kit.model.FilterExt
import com.sky.medialib.ui.kit.filter.MagicFilterExt
import com.sky.media.kit.model.ShiftShaft

/**
 * @author: xuzhiyong
 * @date: 2021/7/28  上午9:46
 * @Email: 18971269648@163.com
 * @description:
 */
class ImageProcessExt(iContainerView: IContainerView,iRenderView: IRenderView) : ImageProcess(iContainerView,iRenderView) {

    private var normalFilter: FilterExt? = null
    private var magicFilter: MagicFilterExt? = null

    fun getUserFilters(): List<Filter?>? {
        return mUsedFilters
    }

    fun isShiftShaft(filter: Filter?): Boolean {
        if (filter == null) {
            return false
        }
        val adjuster: Adjuster? = filter.adjuster
        if (!mUsedFilters.contains(filter) || adjuster == null) {
            return false
        }
        return filter is ShiftShaft || adjuster.progress !== 0
    }

    override fun addFilter(filter: Filter?) {
        super.addFilter(filter)
    }

    fun undoFilter(filter: Filter) {
        filter.undoTool()
        refreshAllFilters()
    }

    fun getNormalFilter(): FilterExt? {
        return normalFilter
    }

    fun isRealFilter(): Boolean {
        if (normalFilter == null) {
            return false
        }
        val adjuster: Adjuster? = normalFilter!!.adjuster
        return !(adjuster == null || adjuster.mRender is EmptyRender)
    }

    fun replaceNormalFilter(filterExt: FilterExt) {
        if (normalFilter !== filterExt) {
            if (normalFilter != null) {
                mUsedFilters.remove(normalFilter)
            }
            normalFilter = filterExt
            addFilter(filterExt)
        }
    }

    fun getMagicFilter(): MagicFilterExt? {
        return magicFilter
    }

    fun isMagicFilter(): Boolean {
        if (magicFilter == null) {
            return false
        }
        val adjuster: Adjuster? = magicFilter!!.adjuster
        return !(adjuster == null || adjuster.mRender is EmptyRender)
    }

    fun replaceMagicFilter(magicFilterExt: MagicFilterExt): Boolean {
        if (magicFilter === magicFilterExt) {
            return false
        }
        if (magicFilter != null) {
            mUsedFilters.remove(magicFilter)
        }
        magicFilter = magicFilterExt
        addFilter(magicFilterExt)
        return true
    }

    fun clearAllFilters() {
        normalFilter = null
        magicFilter = null
        mUsedFilters.clear()
    }

}