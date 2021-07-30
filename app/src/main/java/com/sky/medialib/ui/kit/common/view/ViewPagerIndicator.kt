package com.sky.medialib.ui.kit.common.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.sky.media.image.core.util.LogUtils.Companion.loge
import com.sky.medialib.R
import px

class ViewPagerIndicator : LinearLayout {
    private var littleDotRes = 0
    private var littleDotResSelect = 0
    private var dividerRes = 0
    private var dividerLength = 15.0f
    var count = 0
        private set
    var currentPosition = 0
        private set

    @SuppressLint("NewApi")
    constructor(context: Context, attributeSet: AttributeSet?, i: Int) : super(
        context,
        attributeSet,
        i
    ) {
        init(context, attributeSet)
    }

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
        init(context, attributeSet)
    }

    private fun init(context: Context, attributeSet: AttributeSet?) {
        if (attributeSet != null) {
            val obtainStyledAttributes = context.theme.obtainStyledAttributes(
                attributeSet,
                R.styleable.ViewPagerIndicator,
                0,
                0
            )
            try {
                littleDotRes = obtainStyledAttributes.getResourceId(
                    R.styleable.ViewPagerIndicator_littleDotRes,
                    0
                )
                littleDotResSelect = obtainStyledAttributes.getResourceId(
                    R.styleable.ViewPagerIndicator_littleDotResSelect,
                    0
                )
                dividerRes = obtainStyledAttributes.getResourceId(
                    R.styleable.ViewPagerIndicator_dividerRes,
                    -1
                )
                dividerLength = obtainStyledAttributes.getDimension(
                    R.styleable.ViewPagerIndicator_dividerLength,
                    1.0f.px
                )
            } catch (e: Throwable) {
                e.printStackTrace()
            } finally {
                obtainStyledAttributes.recycle()
            }
        }
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        count = 0
        currentPosition = 0
    }

    fun setLittleDotRes(i: Int) {
        littleDotRes = i
    }

    override fun onMeasure(i: Int, i2: Int) {
        super.onMeasure(i, i2)
    }

    fun setCount(size: Int, z: Boolean) {
        if (z || size != count) {
            removeAllViews()
            if (size <= 0) {
                count = 0
                return
            }
            for (i in 0 until size) {
                if (i == 0) {
                    addView(createImageView(littleDotResSelect))
                    addView(divider)
                } else if (i == size - 1) {
                    addView(createImageView(littleDotRes))
                } else {
                    addView(createImageView(littleDotRes))
                    addView(divider)
                }
            }
            count = size
            visibility = if (size <= 1) {
                INVISIBLE
            } else {
                VISIBLE
            }
        }
    }

    private val divider: ImageView
        private get() = if (dividerRes > 0) {
            createImageView(dividerRes)
        } else createDivideView(dividerLength.toInt())

    private fun createImageView(resId: Int): ImageView {
        val imageView = ImageView(context)
        imageView.layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        imageView.setImageResource(resId)
        return imageView
    }

    private fun createDivideView(width: Int): ImageView {
        val imageView = ImageView(context)
        imageView.layoutParams = LayoutParams(width, 1)
        return imageView
    }

    fun setCurrentItem(pos: Int) {
        if (pos >= count) {
            loge("ViewpagerIndicator", "\t illigal position ~")
            return
        }
        updateImageStatus(currentPosition, false)
        updateImageStatus(pos, true)
        currentPosition = pos
    }

    private fun updateImageStatus(i: Int, z: Boolean) {
        if (getImageView(i) != null) {
            getImageView(i)!!.setImageResource(if (z) littleDotResSelect else littleDotRes)
        }
    }

    private fun getImageView(pos: Int): ImageView? {
        val index = pos * 2
        return if (index >= childCount) {
            null
        } else getChildAt(index) as ImageView
    }
}