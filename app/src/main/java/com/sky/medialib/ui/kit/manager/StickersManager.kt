package com.sky.medialib.ui.kit.manager

import com.sky.medialib.R
import com.sky.medialib.ui.kit.model.ShowRect
import com.sky.medialib.ui.kit.model.StickerModel

/**
 * @author: xuzhiyong
 * @date: 2021/7/31  下午4:37
 * @Email: 18971269648@163.com
 * @description:
 */
object StickersManager {

    fun getStaticStickers():MutableList<StickerModel>{
        return arrayListOf(
            StickerModel(0,ShowRect(
            240,174,214,262
            ), R.drawable.sticker_special_go_icon,
               R.drawable.sticker_special_go,"出发"),
            StickerModel(1,ShowRect(
                180,180,267,140
            ), R.drawable.sticker_special_timefly_icon,
                R.drawable.sticker_special_timefly,"匆匆那年"),
            StickerModel(2,ShowRect(
                152,174,351,293
            ), R.drawable.sticker_special_cheese_icon,
                R.drawable.sticker_special_cheese,"微笑"),
            StickerModel(3,ShowRect(
                240,174,178,280
            ), R.drawable.sticker_special_keepcalm_icon,
                R.drawable.sticker_special_keepcalm,"文艺"),
        )
    }
}