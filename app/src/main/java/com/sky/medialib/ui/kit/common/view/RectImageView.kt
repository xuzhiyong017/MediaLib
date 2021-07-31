package com.sky.medialib.ui.kit.common.view

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.sky.medialib.R
import com.sky.medialib.ui.kit.common.view.RectImageView
import android.graphics.RectF
import android.util.AttributeSet
import px

class RectImageView : AppCompatImageView {

    companion object {
        private val DEFAULT_BOUND_COLOR = Color.parseColor("#FFD5D2")
        private const val DEFAULT_BOUND_FILL_COLOR = 0
        private val DEFAULT_BOUND_WIDTH: Float = 1.0f.px
        const val STYLE_HEIGHT_CUBE = 2
        const val STYLE_WIDTH_CUBE = 1
        const val STYLE_WIDTH_NORMAL = 0
    }

    private var mBoundColor = 0
    private var mBoundFillPaint: Paint? = null
    private var mBoundPaint: Paint? = null
    private var mBoundWidth = 0
    private var mFillColor = 0
    private var mIsShowBound = false
    private var mIsShowMask = false
    private var mMaskColor = 0
    private var mStyle = STYLE_WIDTH_NORMAL

    constructor(context: Context?) : super(context!!) {
        init(context, null)
    }

    constructor(context: Context?, attributeSet: AttributeSet?) : super(
        context!!, attributeSet
    ) {
        init(context, attributeSet)
    }

    constructor(context: Context?, attributeSet: AttributeSet?, i: Int) : super(
        context!!, attributeSet, i
    ) {
        init(context, attributeSet)
    }

    private fun init(context: Context?, attributeSet: AttributeSet?) {
        if (!(context == null || attributeSet == null)) {
            val obtainStyledAttributes =
                context.obtainStyledAttributes(attributeSet, R.styleable.RectImageView)
            mBoundWidth = obtainStyledAttributes.getDimension(R.styleable.RectImageView_boundWidth, DEFAULT_BOUND_WIDTH).toInt()
            mBoundColor = obtainStyledAttributes.getColor(R.styleable.RectImageView_boundColor, DEFAULT_BOUND_COLOR)
            mFillColor = obtainStyledAttributes.getColor(R.styleable.RectImageView_boundFillColor, DEFAULT_BOUND_FILL_COLOR)
            mIsShowBound = obtainStyledAttributes.getBoolean(R.styleable.RectImageView_showBound, false)
            mIsShowMask = obtainStyledAttributes.getBoolean(R.styleable.RectImageView_showMask, false)
            mMaskColor = obtainStyledAttributes.getColor(R.styleable.RectImageView_maskColor, 0)
            obtainStyledAttributes.recycle()
        }
        mBoundPaint = Paint()
        mBoundPaint!!.color = mBoundColor
        mBoundPaint!!.strokeWidth = mBoundWidth.toFloat()
        mBoundPaint!!.style = Paint.Style.STROKE
        mBoundFillPaint = Paint()
        mBoundFillPaint!!.color = mFillColor
        mBoundFillPaint!!.strokeWidth = mBoundWidth.toFloat()
        mBoundFillPaint!!.style = Paint.Style.FILL
    }

    override fun onMeasure(i: Int, i2: Int) {
        super.onMeasure(i, i2)
        when (mStyle) {
            STYLE_WIDTH_CUBE -> {
                setMeasuredDimension(measuredWidth, measuredWidth)
                return
            }
            STYLE_HEIGHT_CUBE -> {
                setMeasuredDimension(measuredHeight, measuredHeight)
                return
            }
            else -> return
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mIsShowMask) {
            mBoundFillPaint!!.color = mMaskColor
            canvas.drawRect(
                RectF(0.0f, 0.0f, measuredWidth.toFloat(), measuredHeight.toFloat()),
                mBoundFillPaint!!
            )
        }
        if (mIsShowBound) {
            mBoundPaint!!.color = mBoundColor
            mBoundFillPaint!!.color = mFillColor
            val rectF = RectF()
            rectF.left = (mBoundWidth / 2).toFloat()
            rectF.top = (mBoundWidth / 2).toFloat()
            rectF.right = (measuredWidth - mBoundWidth / 2).toFloat()
            rectF.bottom = (measuredHeight - mBoundWidth / 2).toFloat()
            canvas.drawRect(rectF, mBoundFillPaint!!)
            canvas.drawRect(rectF, mBoundPaint!!)
        }
    }

    fun showBound(z: Boolean) {
        if (z != mIsShowBound) {
            mIsShowBound = z
            invalidate()
        }
    }

    fun showMask(z: Boolean) {
        if (z != mIsShowMask) {
            mIsShowMask = z
            invalidate()
        }
    }

    fun setStyle(i: Int) {
        mStyle = i
    }


}