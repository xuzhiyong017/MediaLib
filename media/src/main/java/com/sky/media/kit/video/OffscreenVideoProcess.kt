package com.sky.media.kit.video

import com.sky.media.image.core.base.BaseRender
import com.sky.media.image.core.filter.IRequireProgress
import com.sky.media.image.core.process.base.BaseOffscreenProcess
import com.sky.media.image.core.render.GroupRender
import com.sky.media.kit.video.VideoSequenceHelper.BaseSequence
import java.util.*

class OffscreenVideoProcess @JvmOverloads constructor(i: Int = 0, i2: Int = 0) :
    BaseOffscreenProcess<OffscreenVideoRender?>(i, i2) {

    private val mVideoSequenceHelper: VideoSequenceHelper
    private var mRender: BaseRender? = null

    fun addAllSequence(list: List<BaseSequence>) {
        mVideoSequenceHelper.replaceList(list)
    }

    fun onDrawFilterTime(j: Long) {
        if (mInput != null) {
            val sequence = mVideoSequenceHelper.getSequence(j)
            val render = mVideoSequenceHelper.render
            if (render == null) {
                if (mGroupRender != null) {
                    if (mRender != null) {
                        mGroupRender!!.addNextRender(mOffscreenEndpoint)
                        mGroupRender!!.removeRenderIn(mRender)
                        mRender!!.removeRenderIn(mOffscreenEndpoint)
                    }
                } else if (mRender != null) {
                    mInput?.addNextRender(mOffscreenEndpoint)
                    mInput?.removeRenderIn(mRender)
                    mRender?.removeRenderIn(mOffscreenEndpoint)
                }
            } else if (render != mRender) {
                if (mGroupRender != null) {
                    if (mRender != null) {
                        mGroupRender?.addNextRender(render)
                        render.addNextRender(mOffscreenEndpoint)
                        mGroupRender?.removeRenderIn(mRender)
                        mRender?.removeRenderIn(mOffscreenEndpoint)
                    } else {
                        mGroupRender?.addNextRender(render)
                        render.addNextRender(mOffscreenEndpoint)
                        mGroupRender?.removeRenderIn(mOffscreenEndpoint)
                    }
                } else if (mRender != null) {
                    mInput?.addNextRender(render)
                    render.addNextRender(mOffscreenEndpoint)
                    mInput?.removeRenderIn(mRender)
                    mRender?.removeRenderIn(mOffscreenEndpoint)
                } else {
                    mInput?.addNextRender(render)
                    render.addNextRender(mOffscreenEndpoint)
                    mInput?.removeRenderIn(mOffscreenEndpoint)
                }
            }
            mRender = render
            if (sequence != null) {
                val arrayList: MutableList<BaseRender> = ArrayList<BaseRender>()
                if (mGroupRender is GroupRender) {
                    arrayList.addAll((mGroupRender as GroupRender).filters)
                } else if (mGroupRender != null) {
                    arrayList.add(mGroupRender!!)
                }
                if (mRender is GroupRender) {
                    arrayList.addAll((mRender as GroupRender).filters)
                } else if (mRender != null) {
                    arrayList.add(mRender!!)
                }
                for (basicRender in arrayList) {
                    if (basicRender is IRequireProgress) {
                        val duration = (j - sequence.start) * 1.0f / 1000.0f / (basicRender as IRequireProgress).getDuration()
                        (basicRender as IRequireProgress).setProgress(duration - duration.toInt().toFloat())
                    }
                }
            }
        }
    }

    val offscreenVideoRender: OffscreenVideoRender
        get() {
            if (mGroupRender != null) {
                mInput?.removeRenderIn(mGroupRender)
            }
            mGroupRender = createGroupRender()
            mGroupRender?.addNextRender(mOffscreenEndpoint)
            mInput?.addNextRender(mGroupRender)
            return mInput!!
        }

    init {
        mInput = OffscreenVideoRender(i, i2)
        mVideoSequenceHelper = VideoSequenceHelper(i, i2)
    }
}