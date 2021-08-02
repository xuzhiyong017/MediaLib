package com.sky.medialib.ui.kit.adjust

import com.sky.media.image.core.base.BaseRender
import com.sky.media.image.core.filter.Adjuster

/**
 * @author: xuzhiyong
 * @date: 2021/8/2  上午11:36
 * @Email: 18971269648@163.com
 * @description:
 */
class AdjusterExt(basicRender: BaseRender) : Adjuster(basicRender) {
    private var endTimes = IntArray(1)
    var currentMirrorPos = 0

    override fun startAdjust() {
        this.mLastProgress = endTimes[currentMirrorPos % endTimes.size]
    }

    override fun resetAdjust() {
        var i = 0
        if (this.mRender != null) {
            setMirrorPos(0)
            while (i < endTimes.size) {
                endTimes[i] = end
                i++
            }
            this.mRender?.clearNextRenders()
            this.mRender?.reInitialize()
        }
    }

    override fun setRender(basicRender: BaseRender) {
        super.setRender(basicRender)
        if (this.mRender is MagicDataFilter) {
            endTimes =
                IntArray((basicRender as MagicDataFilter).getJsonMirrorsSize())
            for (i in endTimes.indices) {
                endTimes[i] = end
            }
        }
    }

    fun setMirrorPos(pos: Int) {
        if (this.mRender is MagicDataFilter) {
            currentMirrorPos = pos % endTimes.size
            (this.mRender as MagicDataFilter).setCurJsonMirrors(
                currentMirrorPos
            )
        }
    }

    override fun adjust(i: Int) {
        endTimes[currentMirrorPos % endTimes.size] = i
        super.adjust(i)
    }

    override var progress: Int = 0
        get() = endTimes[currentMirrorPos % endTimes.size]

    init {
        setRender(basicRender)
    }
}