package com.sky.media.kit.filter

import com.sky.media.image.core.filter.Adjuster
import com.sky.media.kit.model.FilterExt
import com.sky.media.kit.render.FaceWhiteningRender

/**
 * @author: xuzhiyong
 * @date: 2021/7/28  下午5:58
 * @Email: 18971269648@163.com
 * @description:
 */
class WhiteningTool : FilterExt() {

    private val values = intArrayOf(0, 40, 65, 80, 90, 100)

    init {
        name = "美白"
        adjuster= (object : Adjuster(FaceWhiteningRender()) {
            override fun adjust(i: Int) {
                super.adjust(values[i])
            }
        })
    }
}