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
class Fade : FilterExt() {

    init {
        name = "褪色"
        mIconResource = R.drawable.adjust_fade_selector
        adjuster = Adjuster(FadeRender()).apply {
            initProgress = 0
        }
    }
}