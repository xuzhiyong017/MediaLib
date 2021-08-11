package com.sky.media.image.core.process.base

import com.sky.media.image.core.base.BaseRender
import com.sky.media.image.core.base.OffscreenEndpoint
import com.sky.media.image.core.base.TextureOutRender
import com.sky.media.image.core.filter.Filter
import com.sky.media.image.core.render.EmptyRender
import com.sky.media.image.core.render.GroupRender
import com.sky.media.image.core.render.MultiInputRender
import java.util.*

abstract class BaseOffscreenProcess<T : TextureOutRender?>(var width: Int, var height: Int) :
    BaseProcess() {
    protected var mGroupRender: BaseRender? = null
    protected var mInput: T? = null
    protected var mOffscreenEndpoint: OffscreenEndpoint = OffscreenEndpoint(width, height)
    fun updateInputRenderSize(i: Int, i2: Int) {
        if (mInput != null) {
            mInput!!.setRenderSize(i, i2)
        }
    }

    fun destroy() {
        if (mGroupRender != null) {
            mGroupRender!!.destroy()
        }
        mGroupRender = null
        mInput?.destroy()
        mInput = null
    }

    protected fun createGroupRender(): BaseRender {
        var render: BaseRender?
        var i = 0
        val arrayList: MutableList<BaseRender> = ArrayList<BaseRender>()
        for (i2 in mUsedFilters.indices) {
            val filter = mUsedFilters[i2] as Filter
            val adjuster = filter.adjuster
            if (adjuster != null) {
                render = adjuster.mRender
                if (!(render == null || arrayList.contains(render))) {
                    render.clearNextRenders()
                    render.reInitialize()
                    if (render is MultiInputRender) {
                        render.clearRegisteredFilterLocations()
                    }
                    arrayList.add(render)
                }
            }
        }
        var emptyRender: BaseRender
        return when {
            arrayList.isEmpty() -> {
                emptyRender = EmptyRender()
                emptyRender.setRenderSize(width, height)
                emptyRender
            }
            arrayList.size == 1 -> {
                render = GroupRender()
                emptyRender = arrayList[0] as BaseRender
                emptyRender.addNextRender(render)
                render.registerInitialFilter(emptyRender)
                render.registerTerminalFilter(emptyRender)
                render.setRenderSize(width, height)
                render
            }
            arrayList.size == 2 -> {
                val groupRender: GroupRender = GroupRender()
                emptyRender = arrayList[0] as BaseRender
                render = arrayList[1] as BaseRender?
                emptyRender.addNextRender(render)
                render!!.addNextRender(groupRender)
                groupRender.registerInitialFilter(emptyRender)
                groupRender.registerTerminalFilter(render)
                groupRender.setRenderSize(width, height)
                groupRender
            }
            else -> {
                val groupRender2: GroupRender = GroupRender()
                render = arrayList[arrayList.size - 1] as BaseRender?
                groupRender2.registerInitialFilter(arrayList[0])
                while (i < arrayList.size - 1) {
                    emptyRender = arrayList[i] as BaseRender
                    emptyRender.addNextRender(arrayList[i + 1] as BaseRender?)
                    groupRender2.registerFilter(emptyRender)
                    i++
                }
                render!!.addNextRender(groupRender2)
                groupRender2.registerTerminalFilter(render)
                groupRender2.setRenderSize(width, height)
                groupRender2
            }
        }
    }
}