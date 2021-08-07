package com.sky.medialib.ui.kit.filter

import com.sky.media.image.core.filter.Adjuster
import com.sky.media.image.core.render.EmptyRender
import com.sky.media.kit.model.FilterExt
import com.sky.medialib.R

/**
 * @author: xuzhiyong
 * @date: 2021/8/2  下午2:24
 * @Email: 18971269648@163.com
 * @description:
 */
class OriginNormalFilter(str:String) : FilterExt() {

    init {
        adjuster = Adjuster(EmptyRender())
        mId = 0
        icon = "drawable://" + R.drawable.filter_icon_0000
        mIconResource = R.drawable.filter_icon_0000
        name = str
    }

    fun undercarriage():Boolean{
        return false
    }
}