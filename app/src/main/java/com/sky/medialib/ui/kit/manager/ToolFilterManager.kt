package com.sky.medialib.ui.kit.manager

import android.content.Context
import com.sky.media.image.core.filter.Adjuster
import com.sky.media.kit.filter.BuffingTool
import com.sky.media.kit.filter.WhiteningTool

/**
 * @author: xuzhiyong
 * @date: 2021/7/29  下午5:26
 * @Email: 18971269648@163.com
 * @description:
 */
object ToolFilterManager {

    val whiteningTool = WhiteningTool()
    val buffingTool = BuffingTool()

    fun initEditPicture(context: Context){
        var adjuster: Adjuster? = null

    }
}