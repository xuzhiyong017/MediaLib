package com.sky.media.image.core.process.base

import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import com.sky.media.image.core.base.BaseRender
import com.sky.media.image.core.base.OnScreenEndPoint
import com.sky.media.image.core.base.TextureOutRender
import com.sky.media.image.core.filter.Adjuster
import com.sky.media.image.core.filter.Filter
import com.sky.media.image.core.out.BitmapOutput
import com.sky.media.image.core.out.BitmapOutput.*
import com.sky.media.image.core.out.VideoFrameOutput
import com.sky.media.image.core.pipeline.RenderPipeline
import com.sky.media.image.core.render.EmptyRender
import com.sky.media.image.core.render.GroupRender
import com.sky.media.image.core.render.MultiInputRender
import com.sky.media.image.core.view.IContainerView
import com.sky.media.image.core.view.IRenderView
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.util.ArrayList
import java.util.concurrent.CountDownLatch

/**
 * @author: xuzhiyong
 * @date: 2021/7/27  下午5:33
 * @Email: 18971269648@163.com
 * @description:
 */
abstract class BaseOnscreenProcess<T : TextureOutRender>(val mContainerView:IContainerView,val iRenderView:IRenderView) :
    BaseProcess() {
    var bitmapOutput:BitmapOutput? = null
    private var mBitmapOutputRender: TextureOutRender? = null
    private var mCapturing = false
    private var mConfig = Bitmap.Config.ARGB_8888
    protected var mGroupRender: BaseRender? = null
    open var mInput: T? = null
    protected var mMainHandler = Handler(Looper.getMainLooper())
    protected var mOnscreenEndpoint: OnScreenEndPoint? = null
    protected var mPipeline: RenderPipeline? = null
    protected var mRecording = false
    protected var mVideoFrameOutput: VideoFrameOutput? = null

    init {
        mPipeline = iRenderView.initPipeline()
    }

    interface OnRotateListener {
        fun onRotate(i: Int, i2: Int)
    }

    open fun updateInputRenderSize(i: Int, i2: Int) {
        if (mInput != null) {
            mInput!!.setRenderSize(i, i2)
        }
    }

    open fun rotate() {
        rotate(1)
    }

    open fun rotate(i: Int) {
        if (setRotate90Degrees(getRotation90Degrees() + i)) {
            requestLayout()
        }
    }

    open fun requestLayout() {
        checkIsMainThread()
        this.mContainerView.requestLayout()
    }

    protected open fun checkIsMainThread() {
        if (Looper.getMainLooper().thread.id != Thread.currentThread().id) {
            throw IllegalThreadStateException("This method must be executed at the main thread!")
        }
    }

    open fun setRotate90Degrees(i: Int): Boolean {
        return setRotate90Degrees(i, null)
    }

    open fun setRotate90Degrees(i: Int, onRotateListener: OnRotateListener?): Boolean {
        val rotate90Degrees: Boolean = mContainerView.setRotate90Degrees(i)
        mPipeline!!.pauseRendering()
        if (mInput != null) {
            mInput!!.resetRotate()
            mInput!!.setRotate90Degrees(i)
        }
        mPipeline!!.startRendering()
        if (rotate90Degrees) {
            mPipeline!!.addOnSizeChangedListener(object : RenderPipeline.OnSizeChangedListener {

                override fun getSize(): Rect {
                    return Rect(
                        0,
                        0,
                        mContainerView.getPreviewWidth(),
                        mContainerView.getPreviewHeight()
                    )
                }

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
                    mMainHandler.post{
                        onRotateListener?.onRotate(
                            mContainerView.getPreviewWidth(),
                            mContainerView.getPreviewHeight()
                        )
                    }
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
            onRotateListener?.onRotate(
                mContainerView.getPreviewWidth(),
                mContainerView.getPreviewHeight()
            )
        }
        return rotate90Degrees
    }

    fun resetRenderSize() {
        for (adjuster in mUsedFilters) {
            val adjuster2: Adjuster? = adjuster.adjuster
            if (adjuster2 != null) {
                adjuster2.mRender?.setRenderSize(
                    mContainerView.getPreviewWidth(),
                    mContainerView.getPreviewHeight()
                )
            }
        }
        if (mGroupRender != null) {
            mGroupRender!!.setRenderSize(
                mContainerView.getPreviewWidth(),
                mContainerView.getPreviewHeight()
            )
        }
    }

    open fun getRotation90Degrees(): Int {
        return if (mInput != null) {
            mInput!!.getRotate90Degrees()
        } else 0
    }

    open fun resetRotate() {
        if (setRotate90Degrees(0)) {
            requestLayout()
        }
    }

    open fun setOutputBitmapConfig(config: Bitmap.Config?) {
        mConfig = config!!
    }

    open fun pauseRendering() {
        mPipeline!!.pauseRendering()
    }

    open fun startRendering() {
        mPipeline!!.startRendering()
        iRenderView.requestRender()
    }

    open fun requestRender() {
        iRenderView.requestRender()
    }

    override fun initFilters(list: List<Filter>) {
        super.initFilters(list)
        refreshAllFilters()
    }

    override fun clearFilters() {
        super.clearFilters()
        refreshAllFilters()
    }

    override fun addFilter(filter: Filter?) {
        super.addFilter(filter)
        refreshAllFilters()
    }

    override fun addFilter(i: Int, filter: Filter?) {
        super.addFilter(i, filter)
        refreshAllFilters()
    }

    override fun setFilter(i: Int, filter: Filter?) {
        super.setFilter(i, filter)
        refreshAllFilters()
    }

    override fun removeFilter(i: Int) {
        super.removeFilter(i)
        refreshAllFilters()
    }

    override fun removeFilter(filter: Filter) {
        super.removeFilter(filter)
        refreshAllFilters()
    }

    open fun getPrevRender(filter: Filter): TextureOutRender? {
        val adjuster = filter.adjuster ?: return mInput
        val render: BaseRender? = adjuster.mRender
        val usedRenders: List<BaseRender> = getUsedRenders()
        val indexOf = usedRenders.indexOf(render)
        return if (indexOf < 1 || indexOf > usedRenders.size - 1) {
            mInput
        } else usedRenders[indexOf - 1] as TextureOutRender?
    }

    open fun getNextRender(filter: Filter): TextureOutRender? {
        val adjuster = filter.adjuster ?: return mGroupRender
        val render: BaseRender? = adjuster.mRender
        val usedRenders: List<BaseRender> = getUsedRenders()
        val indexOf = usedRenders.indexOf(render)
        return if (indexOf < 0 || indexOf >= usedRenders.size - 1) {
            mGroupRender
        } else usedRenders[indexOf + 1] as TextureOutRender?
    }

    open fun getUsedRenders(): List<BaseRender> {
        val arrayList: MutableList<BaseRender> = ArrayList<BaseRender>()
        for (adjuster in mUsedFilters) {
            val adjuster2 = adjuster.adjuster
            if (adjuster2 != null) {
                val render: BaseRender? = adjuster2.mRender
                if (!(render == null || arrayList.contains(render))) {
                    arrayList.add(render)
                }
            }
        }
        return arrayList
    }

    open fun getRenderList(basicRender: BaseRender?, list: MutableList<BaseRender>) {
        if (basicRender is GroupRender) {
            for (renderList in basicRender.filters) {
                getRenderList(renderList, list)
            }
        } else if (basicRender != null && !list.contains(basicRender)) {
            list.add(basicRender)
        }
    }

    open fun destroyUselessFilters() {
        val arrayList: MutableList<BaseRender> = ArrayList<BaseRender>()
        getRenderList(mGroupRender, arrayList)
        val arrayList2: MutableList<BaseRender> = ArrayList<BaseRender>()
        for (adjuster in mUsedFilters) {
            val adjuster2 = adjuster.adjuster
            if (adjuster2 != null) {
                getRenderList(adjuster2.mRender, arrayList2)
            }
        }
        arrayList.removeAll(arrayList2)
        for (addFilterToDestroy in arrayList) {
            mPipeline!!.addFilterToDestroy(addFilterToDestroy)
        }
    }

    open fun disableAllFilters() {
        mPipeline!!.pauseRendering()
        if (mGroupRender != null) {
            if (mInput != null) {
                mInput!!.addNextRender(mOnscreenEndpoint!!)
                mGroupRender!!.removeRenderIn(mOnscreenEndpoint!!)
                mInput!!.removeRenderIn(mGroupRender!!)
            }
            destroyUselessFilters()
        }
        val emptyRender: BaseRender = EmptyRender()
        emptyRender.setRenderSize(
            mContainerView.getPreviewWidth(),
            mContainerView.getPreviewHeight()
        )
        mGroupRender = emptyRender
        updateGroupRenderTarget()
        if (this.bitmapOutput != null) {
            if (mCapturing) {
                mBitmapOutputRender?.addNextRender(bitmapOutput!!)
            } else {
                mBitmapOutputRender?.removeRenderIn(bitmapOutput!!)
            }
        }
        if (mInput != null) {
            mInput!!.addNextRender(mGroupRender!!)
            mInput!!.removeRenderIn(mOnscreenEndpoint!!)
        }
        mPipeline!!.startRendering()
        iRenderView.requestRender()
    }

    protected open fun updateGroupRenderTarget() {
        mGroupRender?.addNextRender(mOnscreenEndpoint!!)
    }

    open fun refreshAllFilters() {
        mPipeline!!.pauseRendering()
        if (mGroupRender != null) {
            if (mInput != null) {
                mInput!!.addNextRender(mOnscreenEndpoint!!)
                mGroupRender!!.removeRenderIn(mOnscreenEndpoint!!)
                mInput!!.removeRenderIn(mGroupRender!!)
            }
            destroyUselessFilters()
        }
        mGroupRender = createGroupRender()
        updateGroupRenderTarget()
        if (this.bitmapOutput != null) {
            if (mCapturing) {
                mBitmapOutputRender?.addNextRender(bitmapOutput!!)
            } else {
                mBitmapOutputRender?.removeRenderIn(bitmapOutput!!)
            }
        }
        if (mInput != null) {
            mInput?.addNextRender(mGroupRender!!)
            mInput?.removeRenderIn(mOnscreenEndpoint!!)
        }
        mPipeline!!.startRendering()
        iRenderView.requestRender()
    }

    protected open fun createGroupRender(): BaseRender? {
        var render: BaseRender?
        val arrayList: MutableList<BaseRender> = ArrayList<BaseRender>()
        for (filter in mUsedFilters) {
            if (filter != null) {
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
        }
        var emptyRender: BaseRender
        return when {
            arrayList.isEmpty() -> {
                emptyRender = EmptyRender()
                emptyRender.setRenderSize(
                    mContainerView.getPreviewWidth(),
                    mContainerView.getPreviewHeight()
                )
                emptyRender
            }
            arrayList.size == 1 -> {
                emptyRender = arrayList[0] as BaseRender
                emptyRender.setRenderSize(
                    mContainerView.getPreviewWidth(),
                    mContainerView.getPreviewHeight()
                )
                emptyRender
            }
            arrayList.size == 2 -> {
                val groupRender = GroupRender()
                emptyRender = arrayList[0] as BaseRender
                render = arrayList[1] as BaseRender?
                emptyRender.addNextRender(render!!)
                render.addNextRender(groupRender)
                groupRender.registerInitialFilter(emptyRender)
                groupRender.registerTerminalFilter(render)
                groupRender.setRenderSize(
                    mContainerView.getPreviewWidth(),
                    mContainerView.getPreviewHeight()
                )
                groupRender
            }
            else -> {
                val groupRender2 = GroupRender()
                render = arrayList[arrayList.size - 1] as BaseRender?
                groupRender2.registerInitialFilter(arrayList[0])
                for (i in 0 until arrayList.size - 1) {
                    emptyRender = arrayList[i] as BaseRender
                    emptyRender.addNextRender(arrayList[i + 1])
                    groupRender2.registerFilter(emptyRender)
                }
                render?.addNextRender(groupRender2)
                groupRender2.registerTerminalFilter(render!!)
                groupRender2.setRenderSize(
                    mContainerView.getPreviewWidth(),
                    mContainerView.getPreviewHeight()
                )
                groupRender2
            }
        }
    }


    open fun getOutputBitmap(
        i: Int,
        i2: Int,
        gLTextureOutputRenderer: TextureOutRender?
    ): Bitmap? {
        return if (i <= 0 || i2 <= 0) {
            throw IllegalArgumentException("width and height must not be 0!")
        } else if (mInput == null) {
            throw IllegalStateException("input must not be null!")
        } else {
            val countDownLatch = CountDownLatch(1)
            val arrayList: ArrayList<Bitmap?> = ArrayList<Bitmap?>()
            val bitmapOutput = BitmapOutput()
            bitmapOutput.setRenderSize(i, i2)
            bitmapOutput.mConfig = mConfig
            val gLTextureOutputRenderer2: TextureOutRender? = gLTextureOutputRenderer
            bitmapOutput.mCallback = (object : BitmapOutputCallback {
                override fun bitmapOutput(bitmap: Bitmap?) {
                    gLTextureOutputRenderer2?.removeRenderIn(bitmapOutput)
                    arrayList.add(bitmap)
                    countDownLatch.countDown()
                }
            })
            gLTextureOutputRenderer?.addNextRender(bitmapOutput)
            iRenderView.requestRender()
            try {
                countDownLatch.await()
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            if (arrayList.isEmpty()) {
                null
            } else arrayList[0] as Bitmap
        }
    }

    open fun getOutputBitmap(i: Int, i2: Int): Bitmap? {
        if (mGroupRender == null) {
            refreshAllFilters()
        }
        return getOutputBitmap(i, i2, mGroupRender)
    }

    open fun getOutputBitmap(gLTextureOutputRenderer: TextureOutRender?): Bitmap? {
        if (mInput != null) {
            return getOutputBitmap(
                mContainerView.getPreviewWidth(),
                mContainerView.getPreviewHeight(),
                gLTextureOutputRenderer
            )
        }
        throw IllegalStateException("input must not be null!")
    }

    open fun getOutputBitmap(): Bitmap? {
        if (mInput != null) {
            return getOutputBitmap(
                mContainerView.getPreviewWidth(),
                mContainerView.getPreviewHeight()
            )
        }
        throw IllegalStateException("input must not be null!")
    }

    open fun getOutputBitmap(
        bitmapOutputCallback: BitmapOutputCallback,
        i: Int,
        i2: Int,
        gLTextureOutputRenderer: TextureOutRender?
    ) {
        require(!(i <= 0 || i2 <= 0)) { "width and height must not be 0!" }
        checkNotNull(mInput) { "input must not be null!" }
        if (this.bitmapOutput == null) {
            this.bitmapOutput = BitmapOutput()
        }
        mBitmapOutputRender = gLTextureOutputRenderer
        bitmapOutput!!.setRenderSize(i, i2)
        bitmapOutput!!.mConfig = mConfig
        bitmapOutput!!.mCallback = (object : BitmapOutputCallback {
            override fun bitmapOutput(bitmap: Bitmap?) {
                mCapturing = false
                mBitmapOutputRender?.removeRenderIn(bitmapOutput!!)
                mMainHandler.post {
                    bitmapOutputCallback.bitmapOutput(
                        bitmap
                    )
                }
            }
        })
        mCapturing = true
        mBitmapOutputRender?.addNextRender(bitmapOutput!!)
        iRenderView.requestRender()
    }

    open fun getOutputBitmap(bitmapOutputCallback: BitmapOutputCallback, i: Int, i2: Int) {
        if (mGroupRender == null) {
            refreshAllFilters()
        }
        getOutputBitmap(bitmapOutputCallback, i, i2, mGroupRender)
    }

    open fun getOutputBitmap(
        bitmapOutputCallback: BitmapOutputCallback,
        gLTextureOutputRenderer: TextureOutRender?
    ) {
        checkNotNull(mInput) { "input must not be null!" }
        getOutputBitmap(
            bitmapOutputCallback,
            mContainerView.getPreviewWidth(),
            mContainerView.getPreviewHeight(),
            gLTextureOutputRenderer
        )
    }

    open fun getOutputBitmap(bitmapOutputCallback: BitmapOutputCallback) {
        checkNotNull(mInput) { "input must not be null!" }
        getOutputBitmap(
            bitmapOutputCallback,
            mContainerView.getPreviewWidth(),
            mContainerView.getPreviewHeight()
        )
    }

}