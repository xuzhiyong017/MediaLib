package com.sky.media.kit.filter

import com.sky.media.image.core.filter.Adjuster
import com.sky.media.image.core.render.GaussianBlurRender
import com.sky.media.kit.model.FilterExt

/**
 * @author: xuzhiyong
 * @date: 2021/7/29  下午3:03
 * @Email: 18971269648@163.com
 * @description:
 */
class GaussianBlur:FilterExt() {

    init {
        name = "GaussianBlur"
        adjuster = Adjuster(GaussianBlurRender(5f))
    }

}