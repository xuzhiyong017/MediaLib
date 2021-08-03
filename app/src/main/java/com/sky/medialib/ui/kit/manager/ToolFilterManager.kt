package com.sky.medialib.ui.kit.manager

import android.content.Context
import com.sky.media.image.core.filter.Adjuster
import com.sky.media.kit.filter.BuffingTool
import com.sky.media.kit.filter.WhiteningTool
import com.sky.media.kit.filter.normal.*
import com.sky.media.kit.filter.tools.*
import com.sky.media.kit.model.FilterExt
import com.sky.medialib.ui.kit.filter.OriginNormalFilter

/**
 * @author: xuzhiyong
 * @date: 2021/7/29  下午5:26
 * @Email: 18971269648@163.com
 * @description:
 */
object ToolFilterManager {

    val whiteningTool = WhiteningTool()
    val buffingTool = BuffingTool()

    val normalFilters: MutableList<FilterExt> = mutableListOf()
    val mToolFilters: MutableList<FilterExt> = mutableListOf()

    fun initEditPicture(context: Context){
        var adjuster: Adjuster? = null

    }

    fun initPictureEditFilter(context: Context){
        if(normalFilters.isEmpty()){
            addNormalFilters(initNormalFilter(context,true))
        }else{
            for (adjuster in normalFilters) {
                adjuster.adjuster?.resetAdjust()
            }
        }

        buffingTool?.adjuster?.resetAdjust()
        whiteningTool?.adjuster?.resetAdjust()

        initToolsFilter(context)
    }

    private fun initToolsFilter(context: Context) {
        mToolFilters.clear()
        addToolFiler(Exposure())
        addToolFiler(Contrast())
        addToolFiler(Brightness())
        addToolFiler(Saturation())
        addToolFiler(WhiteBalance())
        addToolFiler(Fade())
    }

    private fun addToolFiler(filterExt: FilterExt?) {
        if (filterExt != null && !mToolFilters.contains(filterExt)) {
            mToolFilters.add(filterExt)
        }
    }

    private fun addNormalFilters(initNormalFilter: List<FilterExt>) {
        normalFilters.clear()
        normalFilters.addAll(initNormalFilter)
    }

    private fun initNormalFilter(context: Context, b: Boolean): List<FilterExt> {
        val array = arrayListOf<FilterExt>()
        array.add(OriginNormalFilter(if (b) "原视频" else "原图"))
        array.add(Fashion(context))
        array.add(Documentary(context))
        array.add(Retro(context))
        array.add(Cartridge(context))
        array.add(Warm(context))
        array.add(Summer(context))
        return array
    }

}