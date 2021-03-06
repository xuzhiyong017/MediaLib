package com.sky.medialib.ui.kit.manager

import android.content.Context
import com.sky.media.image.core.filter.Adjuster
import com.sky.media.kit.filter.BuffingTool
import com.sky.media.kit.filter.WhiteningTool
import com.sky.media.kit.filter.normal.*
import com.sky.media.kit.filter.scribble.ScribbleTool
import com.sky.media.kit.filter.tools.*
import com.sky.media.kit.model.FilterExt
import com.sky.medialib.ui.kit.filter.MagicFilterExt
import com.sky.medialib.ui.kit.filter.OriginNormalFilter

/**
 * @author: xuzhiyong
 * @date: 2021/7/29  下午5:26
 * @Email: 18971269648@163.com
 * @description:
 */
object ToolFilterManager {


    //picture
    val whiteningTool = WhiteningTool()
    val buffingTool = BuffingTool()
    //camera
    var cameraWhiteningTool: WhiteningTool? = null
    var cameraBuffingTool: BuffingTool? = null
    //editvideo
    var videoWhiteningTool: WhiteningTool? = null
    var videoBuffingTool: BuffingTool? = null
    var clipScribbleTool :ScribbleTool? = null
    var paintScribbleTool :ScribbleTool? = null


    val mToolFilters: MutableList<FilterExt> = mutableListOf()
    val normalFilters: MutableList<FilterExt> = mutableListOf()
    val cacheFilterList: MutableList<FilterExt> = mutableListOf()
    val editVideoFilterList: MutableList<FilterExt> = mutableListOf()
    val magicFilterList: MutableList<MagicFilterExt> = mutableListOf()

    fun initEditPicture(context: Context){
        var adjuster: Adjuster? = null

    }

    fun initPictureEditFilter(context: Context){
        if(normalFilters.isEmpty()){
            addNormalFilters(initNormalFilter(context,false))
        }else{
            for (normalFilter in normalFilters) {
                normalFilter.adjuster?.resetAdjust()
            }
        }

        MagicManager.parseJsonList()

        for (magicFilter in magicFilterList) {
            magicFilter.adjuster?.resetAdjust()
        }

        buffingTool?.adjuster?.resetAdjust()
        whiteningTool?.adjuster?.resetAdjust()

        initToolsFilter(context)
    }

    private fun initToolsFilter(context: Context) {
        clipScribbleTool = ScribbleTool(context,0)
        paintScribbleTool = ScribbleTool(context,1)

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

    private fun addEditVideoFilters(initNormalFilter: List<FilterExt>) {
        editVideoFilterList.clear()
        editVideoFilterList.addAll(initNormalFilter)
    }

    fun addMagicFilters(list: List<MagicFilterExt>) {
        magicFilterList.clear()
        magicFilterList.addAll(list)
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

    fun initEditVideoFilter(context: Context){
        if(editVideoFilterList.isEmpty()){
            addEditVideoFilters(initNormalFilter(context,true))
        }else{
            for (adjuster in editVideoFilterList) {
                adjuster.adjuster?.resetAdjust()
            }
        }

        videoBuffingTool?.adjuster?.resetAdjust()
        videoWhiteningTool?.adjuster?.resetAdjust()

        initEditVideoBeautyTool()
    }

    private fun initEditVideoBeautyTool() {
        videoBuffingTool = BuffingTool()
        videoWhiteningTool = WhiteningTool()
    }

    fun initCameraFilter(context: Context) {
        if(cacheFilterList.isEmpty()){
            addCacheFilters(initNormalFilter(context, true))
        }else{
            for (adjuster in cacheFilterList) {
                adjuster.adjuster?.resetAdjust()
            }
        }

       cameraBuffingTool?.adjuster?.resetAdjust()
       cameraWhiteningTool?.adjuster?.resetAdjust()

        initCameraBeautyTool()
    }

    private fun initCameraBeautyTool() {
        cameraBuffingTool = BuffingTool()
        cameraWhiteningTool = WhiteningTool()
    }

    private fun addCacheFilters(list: List<FilterExt>) {
        cacheFilterList.clear()
        cacheFilterList.addAll(list)
    }

    fun getCacheFilterById(i: Int): FilterExt? {
        val h = switchId(i)
        for (filterExt in cacheFilterList) {
            if (h == filterExt.mId) {
                return filterExt
            }
        }
        return null
    }

    fun getEditVideoFilterById(i: Int): FilterExt? {
        val h = switchId(i)
        for (filterExt in cacheFilterList) {
            if (h == filterExt.mId) {
                return filterExt
            }
        }
        return null
    }


    fun switchId(i: Int): Int {
        return when (i) {
            205 -> 211
            else -> i
        }
    }

}