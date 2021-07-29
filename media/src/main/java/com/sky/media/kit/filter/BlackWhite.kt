package com.sky.media.kit.filter

import android.content.Context
import android.graphics.Color
import com.sky.media.R
import com.sky.media.image.core.filter.Adjuster
import com.sky.media.kit.model.FilterExt
import com.sky.media.kit.render.BlackWhiteRender

/**
 * @author: xuzhiyong
 * @date: 2021/7/29  上午10:54
 * @Email: 18971269648@163.com
 * @description:
 */
class BlackWhite(context: Context) : FilterExt() {
    init {
        adjuster = Adjuster(BlackWhiteRender(context)).apply {
            initProgress = 100
        }
        name = "黑白"
        mIconResource = R.drawable.filter_icon_0205
        mNameBackgroundColor = Color.parseColor("#81310C")
        mId = 211
    }

}