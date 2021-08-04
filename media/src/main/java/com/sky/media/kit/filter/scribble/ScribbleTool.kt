package com.sky.media.kit.filter.scribble

import android.content.Context
import com.sky.media.kit.model.FilterExt
import com.sky.media.kit.filter.scribble.ScribbleAdjuster
import com.sky.media.R
import com.sky.media.image.core.filter.Adjuster

class ScribbleTool(context: Context?, i: Int) : FilterExt() {

    private val type: Int = i
    private val mScribbleAdjuster: ScribbleAdjuster = ScribbleAdjuster(context, i)
    override var name: String?
        get() = "马赛克"
        set(name) {
            super.name = name
        }
    override var icon: String?
        get() = "drawable://" + R.drawable.selector_tumo
        set(icon) {
            super.icon = icon
        }
    override var adjuster: Adjuster?
        get() = mScribbleAdjuster
        set(adjuster) {
            super.adjuster = adjuster
        }

}