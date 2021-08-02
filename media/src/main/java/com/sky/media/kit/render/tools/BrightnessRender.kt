package com.sky.media.kit.render.tools

import com.sky.media.image.core.filter.IAdjustable
import com.sky.media.image.core.render.ColourMatrixRender

/**
 * @author: xuzhiyong
 * @date: 2021/8/2  下午5:50
 * @Email: 18971269648@163.com
 * @description:
 */
class BrightnessRender : ColourMatrixRender(
    floatArrayOf(
        1.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        1.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        1.0f,
        0.0f,
        0.0f,
        0.0f,
        0.0f,
        1.0f
    ), 1.0f),IAdjustable {

    override fun adjust(cur: Int, start: Int, end: Int) {
        val f = cur * 1.0f / (end - start) * 2.0f / 3.0f
        setColourMatrix(
            floatArrayOf(
                1.0f,
                0.0f,
                0.0f,
                f,
                0.0f,
                1.0f,
                0.0f,
                f,
                0.0f,
                0.0f,
                1.0f,
                f,
                0.0f,
                0.0f,
                0.0f,
                1.0f
            )
        )
    }
}