package com.sky.media.kit.picture

import android.graphics.Bitmap
import android.graphics.Rect
import com.sky.media.image.core.base.OnScreenEndPoint
import com.sky.media.image.core.pipeline.RenderPipeline
import com.sky.media.image.core.process.base.BaseOnscreenProcess
import com.sky.media.image.core.view.IContainerView
import com.sky.media.image.core.view.IRenderView

/**
 * @author: xuzhiyong
 * @date: 2021/7/28  上午9:32
 * @Email: 18971269648@163.com
 * @description:
 */
open class ImageProcess(iContainerView: IContainerView,iRenderView: IRenderView) : BaseOnscreenProcess<BitmapInput>(iContainerView,iRenderView) {

    interface onSizeChangeListener {
        fun onSizeChange(width: Int, height: Int)
    }

    fun getSourceBitmap(): Bitmap? {
        return mInput?.inputBitmap
    }

    fun initInputBitmap(
        bitmap: Bitmap,
        screenWidth: Int,
        screenHeight: Int,
        onSizeChangeListener: onSizeChangeListener?
    ): Boolean {
        checkIsMainThread()
        val aspectRatio = mContainerView.setAspectRatio(
            bitmap.width.toFloat() * 1.0f / bitmap.height
                .toFloat(), screenWidth, screenHeight
        )
        mPipeline!!.pauseRendering()
        if (mInput == null) {
            mInput = BitmapInput()
            mPipeline!!.setRootRenderer(mInput)
        }
        if (mOnscreenEndpoint == null) {
            mOnscreenEndpoint = OnScreenEndPoint(mPipeline!!)
            mInput!!.addNextRender(mOnscreenEndpoint!!)
        }
        mInput!!.inputBitmap = bitmap
        mPipeline!!.startRendering()
        if (aspectRatio) {
            mPipeline!!.addOnSizeChangedListener(object : RenderPipeline.OnSizeChangedListener {
                override fun onSizeChanged(i: Int, i2: Int) {
                    mInput!!.setRenderSize(
                        mContainerView.getPreviewWidth(),
                        mContainerView.getPreviewHeight()
                    )
                    mOnscreenEndpoint!!.setRenderSize(
                        mContainerView.getPreviewWidth(),
                        mContainerView.getPreviewHeight()
                    )
                    resetRenderSize()
                    iRenderView.requestRender()
                    onSizeChangeListener?.onSizeChange(
                        mContainerView.getPreviewWidth(),
                        mContainerView.getPreviewHeight()
                    )
                }

                override fun getSize(): Rect{
                    return Rect(
                        0,
                        0,
                        mContainerView.getPreviewWidth(),
                        mContainerView.getPreviewHeight()
                    )
                }

            })
        } else {
            mInput!!.setRenderSize(
                mContainerView.getPreviewWidth(),
                mContainerView.getPreviewHeight()
            )
            mOnscreenEndpoint!!.setRenderSize(
                mContainerView.getPreviewWidth(),
                mContainerView.getPreviewHeight()
            )
            iRenderView.requestRender()
            onSizeChangeListener?.onSizeChange(
                mContainerView.getPreviewWidth(),
                mContainerView.getPreviewHeight()
            )
        }
        return aspectRatio
    }

    fun renderBitmap(bitmap: Bitmap, width: Int, height: Int): Boolean {
        return initInputBitmap(bitmap, width, height, null)
    }
}