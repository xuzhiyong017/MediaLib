package com.sky.medialib.ui.editvideo.process

import android.net.Uri
import com.sky.media.image.core.view.IContainerView
import com.sky.media.image.core.view.IRenderView
import com.sky.media.kit.model.FilterExt
import com.sky.media.kit.video.VideoProcess

class VideoProcessExt(iContainerView: IContainerView?, iRenderView: IRenderView?) : VideoProcess(
    iContainerView!!, iRenderView!!
) {
    var switchFilter: FilterExt? = null
        private set
    val videoWidth: Int
        get() = if (mInput == null || mInput!!.getMediaPlayer() == null) {
            0
        } else mInput!!.getMediaPlayer()!!.videoWidth
    val duration: Int
        get() = if (mInput == null || mInput!!.getMediaPlayer() == null) {
            0
        } else mInput!!.getMediaPlayer()!!.duration

    val playUri: Uri?
        get() = if (mInput != null) {
            mInput!!.getPlayUri()
        } else null

    fun closeVoice() {
        mInput!!.setVolumeLeftAndRight(0.0f, 0.0f)
    }

    fun openVoice() {
        mInput!!.setVolumeLeftAndRight(1.0f, 1.0f)
    }

    fun switchFilter(filterExt: FilterExt) {
        if (switchFilter !== filterExt) {
            if (switchFilter != null) {
                mUsedFilters.remove(switchFilter)
            }
            switchFilter = filterExt
            addFilter(filterExt)
        }
    }
}