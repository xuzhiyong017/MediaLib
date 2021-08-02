package com.sky.media.kit.render.tools

import com.sky.media.image.core.filter.IAdjustable
import com.sky.media.image.core.render.RGBRender
import kotlin.math.pow

/**
 * @author: xuzhiyong
 * @date: 2021/8/2  下午5:38
 * @Email: 18971269648@163.com
 * @description:
 */
class ExposureRender(val value:Float) : RGBRender(
    2.0.pow(value.toDouble()).toFloat(),
    2.0.pow(value.toDouble()).toFloat(),
    2.0.pow(value.toDouble()).toFloat()
),IAdjustable {

    override fun adjust(cur: Int, start: Int, end: Int) {
        val f = cur * 1.0f / (end - start)
        adjustRGB(
            2.0.pow(f.toDouble()).toFloat(),
            2.0.pow(f.toDouble()).toFloat(),
            2.0.pow(f.toDouble()).toFloat()
        )
    }
}