package com.sky.media.image.core.view

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout

class ProcessRelativeContainer(context: Context?, attributeSet: AttributeSet?) :
    RelativeLayout(context, attributeSet), IContainerView {
    private val mHelper: ContainerViewHelper
    fun setScaleType(scaleType: ContainerViewHelper.ScaleType?) {
        mHelper.setScaleType(scaleType!!)
    }

    override fun setAspectRatio(f: Float, i: Int, i2: Int): Boolean {
        return mHelper.setAspectRatio(f, i, i2)
    }

    val aspectRatio: Float
        get() = mHelper.aspectRatio

    fun rotate() {
        mHelper.rotate()
    }

    fun rotate(i: Int) {
        mHelper.rotate(i)
    }

    override fun setRotate90Degrees(i: Int): Boolean {
        return mHelper.setRotate90Degrees(i)
    }

    val rotation90Degrees: Int
        get() = mHelper.rotation90Degrees

    fun resetRotate() {
        mHelper.resetRotate()
    }

    override fun getPreviewWidth(): Int {
        return mHelper.getPreviewWidth()
    }

    override fun getPreviewHeight(): Int {
        return mHelper.getPreviewHeight()
    }

    private fun calculatePreviewSize(i: Int, i2: Int): Boolean {
        return mHelper.calculatePreviewSize(i, i2)
    }

    override fun onMeasure(i: Int, i2: Int) {
        calculatePreviewSize(MeasureSpec.getSize(i), MeasureSpec.getSize(i2))
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(mHelper.getPreviewWidth(), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(
                mHelper.getPreviewHeight(), MeasureSpec.EXACTLY
            )
        )
    }

    init {
        mHelper = object : ContainerViewHelper() {
            override fun requestLayout() {
                this@ProcessRelativeContainer.requestLayout()
            }

            override fun getContext(): Context? {
                return context
            }
        }
    }
}