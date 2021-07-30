package com.sky.media.kit.filter

import com.sky.media.image.core.filter.Adjuster
import com.sky.media.kit.model.FilterExt
import com.sky.media.kit.render.FaceBuffingRender
/**
 * @author: xuzhiyong
 * @date: 2021/7/28  下午6:00
 * @Email: 18971269648@163.com
 * @description:
 */
class BuffingTool : FilterExt() {
    companion object {
        private val VALUES = intArrayOf(0, 30, 50, 70, 90, 100)
    }

    init {
        name = "磨皮"
        adjuster = object : Adjuster(FaceBuffingRender()) {
            override fun adjust(i: Int) {
                super.adjust(VALUES[i])
            }
        }
    }
}