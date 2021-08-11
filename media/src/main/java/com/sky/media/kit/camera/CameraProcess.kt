package com.sky.media.kit.camera

import android.graphics.Rect
import android.hardware.Camera
import com.sky.media.image.core.base.OnScreenEndPoint
import com.sky.media.image.core.out.VideoFrameOutput
import com.sky.media.image.core.pipeline.RenderPipeline
import com.sky.media.image.core.process.base.BaseOnscreenProcess
import com.sky.media.image.core.view.IContainerView
import com.sky.media.image.core.view.IRenderView

/**
 * @author: xuzhiyong
 * @date: 2021/8/6  上午9:41
 * @Email: 18971269648@163.com
 * @description:
 */
open class CameraProcess(iContainerView: IContainerView, iRenderView: IRenderView):BaseOnscreenProcess<CameraInput>(iContainerView,iRenderView) {

    fun processCamera(camera: Camera){
        if(camera == null){
            throw IllegalArgumentException("camera must not be null!")
        }

        mPipeline?.pauseRendering()
        if(mInput != null){
            mInput!!.clearNextRenders()
            mPipeline?.addFilterToDestroy(mInput)
        }else{
            mOnscreenEndpoint = OnScreenEndPoint(mPipeline!!)
        }
        mInput = CameraInput(iRenderView,camera)

        if(mGroupRender != null){
            mInput!!.addNextRender(mGroupRender!!)
        }else{
            mInput!!.addNextRender(mOnscreenEndpoint!!)
        }
        mPipeline?.setRootRenderer(mInput)
        mPipeline?.addOnSizeChangedListener(object :RenderPipeline.OnSizeChangedListener{
            override fun getSize(): Rect {
                return Rect(0, 0, mContainerView.getPreviewWidth(), mContainerView.getPreviewHeight())
            }

            override fun onSizeChanged(i: Int, i2: Int) {
                mOnscreenEndpoint!!.setRenderSize(
                    mContainerView.getPreviewWidth(),
                    mContainerView.getPreviewHeight()
                )
                resetRenderSize()
                iRenderView.requestRender()
            }
        })

        mPipeline?.startRendering()
        iRenderView.requestRender()
    }

    open fun startRecord() {
        mRecording = true
        if (mVideoFrameOutput != null) {
            if (mGroupRender == null) {
                refreshAllFilters()
            }
            mGroupRender!!.addNextRender(mVideoFrameOutput)
            iRenderView.requestRender()
        }
    }

    open fun stopRecord() {
        mRecording = false
        if (mVideoFrameOutput != null) {
            if (mGroupRender == null) {
                refreshAllFilters()
            }
            mGroupRender!!.removeRenderIn(mVideoFrameOutput)
            iRenderView.requestRender()
        }
    }


    fun setVideoFrameOutput(videoFrameOutputCallback: VideoFrameOutput.VideoFrameOutputCallback?, i: Int, i2: Int) {
        requireNotNull(mInput) { "input must not be null!" }
        if (mVideoFrameOutput == null) {
            mVideoFrameOutput = VideoFrameOutput()
        }
        mVideoFrameOutput!!.setRenderSize(i, i2)
        mVideoFrameOutput!!.setVideoFrameOutputCallback(videoFrameOutputCallback)
        if (mRecording) {
            if (mGroupRender == null) {
                refreshAllFilters()
            }
            mGroupRender!!.addNextRender(this.mVideoFrameOutput)
            iRenderView.requestRender()
        }
    }


}