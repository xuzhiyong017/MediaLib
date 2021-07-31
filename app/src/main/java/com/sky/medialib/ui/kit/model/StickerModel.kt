package com.sky.medialib.ui.kit.model

import androidx.annotation.DrawableRes

/**
 * @author: xuzhiyong
 * @date: 2021/7/31  下午4:31
 * @Email: 18971269648@163.com
 * @description:
 */
data class StickerModel(
    val id:Int,
    val showRect: ShowRect,
    @DrawableRes val btnIcon:Int,
    @DrawableRes val imageRes:Int,
    val btnTitle:String,
)
