package com.sky.media.kit.filter.tools

import com.sky.media.R
import com.sky.media.image.core.filter.Adjuster
import com.sky.media.kit.model.FilterExt
import com.sky.media.kit.render.tools.BrightnessRender
import com.sky.media.kit.render.tools.ContrastRender
import com.sky.media.kit.render.tools.ExposureRender
import com.sky.media.kit.render.tools.SaturationRender

/**
 * @author: xuzhiyong
 * @date: 2021/8/2  下午5:28
 * @Email: 18971269648@163.com
 * @description:
 */
class Saturation : FilterExt() {

    init {
        name = "饱和度"
        mIconResource = R.drawable.adjust_saturation_selector
        adjuster = Adjuster(SaturationRender()).apply {
            start = -100
            end = 100
            initProgress = 0
        }
    }
}