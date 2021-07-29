package com.sky.media.kit.filter

import com.sky.media.R
import com.sky.media.image.core.filter.Adjuster
import com.sky.media.kit.model.FilterExt
import com.sky.media.kit.render.HighlightShadowRender

/**
 * @author: xuzhiyong
 * @date: 2021/7/28  下午6:22
 * @Email: 18971269648@163.com
 * @description:
 */
class HighlightShadow : FilterExt() {
    init {
        name = "光影"
        mIconResource = R.drawable.adjust_gaoguang_selector
        adjuster = Adjuster(HighlightShadowRender(1.0f,0.0f)).apply {
            initProgress = 0
        }

    }
}