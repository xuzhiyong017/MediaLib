package com.sky.media.kit.filter.normal

import android.content.Context
import com.sky.media.R
import com.sky.media.image.core.filter.Adjuster
import com.sky.media.image.core.render.LookupRender
import com.sky.media.kit.model.FilterExt

/**
 * @author: xuzhiyong
 * @date: 2021/8/2  下午2:27
 * @Email: 18971269648@163.com
 * @description:
 */
class Documentary(context: Context) : FilterExt() {

    init {
        adjuster = Adjuster(LookupRender(context, R.drawable.jishi)).apply {
            initProgress = 100
        }
        mId = 14
        name = "纪实"
    }

    override fun getIconResource(): Int {
        return R.drawable.filter_icon_0014
    }
}