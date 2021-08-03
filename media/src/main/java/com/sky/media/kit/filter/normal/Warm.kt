package com.sky.media.kit.filter.normal

import android.content.Context
import com.sky.media.R
import com.sky.media.image.core.cache.ImageBitmapCache
import com.sky.media.image.core.filter.Adjuster
import com.sky.media.image.core.render.LookupRender
import com.sky.media.kit.model.FilterExt

/**
 * @author: xuzhiyong
 * @date: 2021/8/2  下午5:28
 * @Email: 18971269648@163.com
 * @description:
 */
class Warm(context: Context) : FilterExt() {

    init {
        name = "温暖"
        mId = 8
        mIconResource = R.drawable.filter_icon_0008
        adjuster = Adjuster(LookupRender(context, R.drawable.wennuan, ImageBitmapCache.getInstance())).apply {
            initProgress = 100
        }
    }
}