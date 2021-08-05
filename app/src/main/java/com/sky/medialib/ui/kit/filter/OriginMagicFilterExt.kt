package com.sky.medialib.ui.kit.filter

import android.content.Context
import com.sky.media.image.core.render.EmptyRender
import com.sky.medialib.R
import com.sky.medialib.ui.kit.adjust.AdjusterExt

/**
 * @author: xuzhiyong
 * @date: 2021/8/2  下午1:00
 * @Email: 18971269648@163.com
 * @description:
 */
class OriginMagicFilterExt(context: Context) : MagicFilterExt(context,null) {

    init {
        name = "原图"
        adjustExt = AdjusterExt(EmptyRender())
        adjuster = adjustExt
        mId = 1000000
        icon = "drawable://" + R.drawable.filter_icon_0000
    }

    override fun getMirrorPos(i: Int): Int {
        return if (i == 1000000) {
            0
        } else -1
    }
}