package com.sky.media.kit.filter.tools

import com.sky.media.R
import com.sky.media.image.core.filter.Adjuster
import com.sky.media.kit.model.FilterExt
import com.sky.media.kit.render.tools.*

/**
 * @author: xuzhiyong
 * @date: 2021/8/2  下午5:28
 * @Email: 18971269648@163.com
 * @description:
 */
class WhiteBalance : FilterExt() {

    init {
        name = "暖色"
        mIconResource = R.drawable.adjust_warm_selector
        adjuster = Adjuster(WhiteBalanceRender(0.0f,0.0f)).apply {
            start = -100
            end = 100
            initProgress = 0
        }
    }
}