package com.sky.medialib.ui.kit.filter

import android.content.Context
import com.sky.media.kit.model.FilterExt
import com.sky.medialib.ui.kit.adjust.AdjusterExt
import com.sky.medialib.ui.kit.adjust.MagicDataFilter
import com.sky.medialib.ui.kit.model.json.magic.JsonMirror
import com.sky.medialib.ui.kit.model.json.magic.JsonTemplate

/**
 * @author: xuzhiyong
 * @date: 2021/7/28  上午11:11
 * @Email: 18971269648@163.com
 * @description:
 */
open class MagicFilterExt(override var context: Context?, template: JsonTemplate?) : FilterExt() {

    protected var adjustExt: AdjusterExt? = null
    private var template: JsonTemplate? = null
    private var filter: MagicDataFilter? = null

    init {
        if(template != null){
            filter = MagicDataFilter(context, template.mirrors)
            adjustExt = AdjusterExt(filter!!)
            adjustExt!!.initProgress = 100
            adjuster = adjustExt
            mId = template.mirrors[(mAdjuster as? AdjusterExt)?.currentMirrorPos ?:0].mid
            icon = template.icon_large
            name = template.name
        }
    }

    fun isOff(): Boolean {
        return "0" == template?.status
    }

    fun isOff(i: Int): Boolean {
        return i >= 0 && i < template?.mirrors?.size ?:0 && "0" == (template?.mirrors?.get(i))?.status ?:0
    }

    open fun getMirrorPos(i: Int): Int {
        val list: MutableList<JsonMirror>? = template?.mirrors
        if (list != null) {
            for (i in list.indices) {
                if (list[i].mid == i) {
                    return i
                }
            }
        }
        return 0
    }
}