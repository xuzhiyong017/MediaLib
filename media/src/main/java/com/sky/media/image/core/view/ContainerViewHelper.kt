package com.sky.media.image.core.view

import com.sky.media.image.core.util.LogUtils.Companion.logi

abstract class ContainerViewHelper : IContainerView {
    private var mAngle = 0
    var aspectRatio = 1.0f
        private set
    private var mMaxHeight = 0
    private var mMaxWidth = 0
    private var mPreviewHeight = 0
    private var mPreviewWidth = 0
    private var mScaleType = ScaleType.FIT_CENTER

    enum class ScaleType {
        CENTER_CROP, FIT_CENTER, FIT_WIDTH, FIT_HEIGHT
    }

    fun setScaleType(scaleType: ScaleType) {
        mScaleType = scaleType
    }

    override fun setAspectRatio(f: Float, i: Int, i2: Int): Boolean {
        return if (f.toDouble() <= 0.0 || i < 0 || i2 < 0) {
            throw IllegalArgumentException()
        } else if (aspectRatio == f && mMaxWidth == i && mMaxHeight == i2) {
            false
        } else {
            aspectRatio = f
            mMaxWidth = i
            mMaxHeight = i2
            if (!calculatePreviewSize(0, 0)) {
                return false
            }
            requestLayout()
            true
        }
    }

    @JvmOverloads
    fun rotate(i: Int = 1) {
        var i = i
        while (i < 0) {
            i += 4
        }
        mAngle += i * 90
        if (calculatePreviewSize(0, 0)) {
            requestLayout()
        }
    }

    override fun setRotate90Degrees(i: Int): Boolean {
        var i = i
        while (i < 0) {
            i += 4
        }
        mAngle = i * 90
        return calculatePreviewSize(0, 0)
    }

    val rotation90Degrees: Int
        get() = mAngle / 90

    fun resetRotate() {
        mAngle = 0
        if (calculatePreviewSize(0, 0)) {
            requestLayout()
        }
    }

    override fun getPreviewWidth(): Int {
        return mPreviewWidth
    }

    override fun getPreviewHeight(): Int {
        return mPreviewHeight
    }

    fun calculatePreviewSize(width: Int, height: Int): Boolean {
        var realWidth = width
        var realHeight = height
        var sizeHasChange = false
        if (mScaleType != ScaleType.FIT_CENTER) {
            if (mScaleType == ScaleType.CENTER_CROP) {
                if (mAngle / 90 % 2 == 1) {
                    if (mMaxWidth == 0 || mMaxHeight == 0) {
                        if (!(realWidth == 0 || realHeight == 0)) {
                            if (aspectRatio > realHeight.toFloat() * 1.0f / realWidth.toFloat()) {
                                realHeight = ((realWidth.toFloat() * aspectRatio).toDouble() + 0.5).toInt()
                            } else {
                                realWidth = ((realHeight.toFloat() / aspectRatio).toDouble() + 0.5).toInt()
                            }
                        }
                    } else if (aspectRatio > mMaxHeight.toFloat() * 1.0f / mMaxWidth.toFloat()) {
                        realHeight = ((mMaxWidth.toFloat() * aspectRatio).toDouble() + 0.5).toInt()
                        realWidth = mMaxWidth
                    } else {
                        realWidth = ((mMaxHeight.toFloat() / aspectRatio).toDouble() + 0.5).toInt()
                        realHeight = mMaxHeight
                    }
                } else if (mMaxWidth == 0 || mMaxHeight == 0) {
                    if (!(realWidth == 0 || realHeight == 0)) {
                        if (aspectRatio > realWidth.toFloat() * 1.0f / realHeight.toFloat()) {
                            realWidth = ((realHeight.toFloat() * aspectRatio).toDouble() + 0.5).toInt()
                        } else {
                            realHeight = ((realWidth.toFloat() / aspectRatio).toDouble() + 0.5).toInt()
                        }
                    }
                } else if (aspectRatio > mMaxWidth.toFloat() * 1.0f / mMaxHeight.toFloat()) {
                    realHeight = mMaxHeight
                    realWidth = ((mMaxHeight.toFloat() * aspectRatio).toDouble() + 0.5).toInt()
                } else {
                    realWidth = mMaxWidth
                    realHeight = ((mMaxWidth.toFloat() / aspectRatio).toDouble() + 0.5).toInt()
                }
            } else if (mScaleType == ScaleType.FIT_WIDTH) {
                if (mAngle / 90 % 2 == 1) {
                    if (mMaxWidth != 0 && mMaxHeight != 0) {
                        realHeight = ((mMaxWidth.toFloat() * aspectRatio).toDouble() + 0.5).toInt()
                        realWidth = mMaxWidth
                    } else if (!(realWidth == 0 || realHeight == 0)) {
                        realHeight = ((realWidth.toFloat() * aspectRatio).toDouble() + 0.5).toInt()
                    }
                } else if (mMaxWidth != 0 && mMaxHeight != 0) {
                    realWidth = mMaxWidth
                    realHeight = ((mMaxWidth.toFloat() / aspectRatio).toDouble() + 0.5).toInt()
                } else if (!(realWidth == 0 || realHeight == 0)) {
                    realHeight = ((realWidth.toFloat() / aspectRatio).toDouble() + 0.5).toInt()
                }
            } else if (mScaleType == ScaleType.FIT_HEIGHT) {
                if (mAngle / 90 % 2 == 1) {
                    if (mMaxWidth != 0 && mMaxHeight != 0) {
                        realWidth = ((mMaxHeight.toFloat() / aspectRatio).toDouble() + 0.5).toInt()
                        realHeight = mMaxHeight
                    } else if (!(realWidth == 0 || realHeight == 0)) {
                        realWidth = ((realHeight.toFloat() / aspectRatio).toDouble() + 0.5).toInt()
                    }
                } else if (mMaxWidth != 0 && mMaxHeight != 0) {
                    realHeight = mMaxHeight
                    realWidth = ((mMaxHeight.toFloat() * aspectRatio).toDouble() + 0.5).toInt()
                } else if (!(realWidth == 0 || realHeight == 0)) {
                    realWidth = ((realHeight.toFloat() * aspectRatio).toDouble() + 0.5).toInt()
                }
            }
            realHeight = 0
            realWidth = 0
        } else if (mAngle / 90 % 2 == 1) {
            if (mMaxWidth != 0 && mMaxHeight != 0) {
                realWidth = mMaxWidth
                realHeight = mMaxHeight
                if (realHeight.toFloat() > realWidth.toFloat() * aspectRatio) {
                    realHeight = ((realWidth.toFloat() * aspectRatio).toDouble() + 0.5).toInt()
                } else {
                    realWidth = ((realHeight.toFloat() / aspectRatio).toDouble() + 0.5).toInt()
                }
            } else if (realHeight.toFloat() > realWidth.toFloat() * aspectRatio) {
                realHeight = ((realWidth.toFloat() * aspectRatio).toDouble() + 0.5).toInt()
            } else {
                realWidth = ((realHeight.toFloat() / aspectRatio).toDouble() + 0.5).toInt()
            }
        } else if (mMaxWidth != 0 && mMaxHeight != 0) {
            realWidth = mMaxWidth
            realHeight = mMaxHeight
            if (realWidth.toFloat() > realHeight.toFloat() * aspectRatio) {
                realWidth = ((realHeight.toFloat() * aspectRatio).toDouble() + 0.5).toInt()
            } else {
                realHeight = ((realWidth.toFloat() / aspectRatio).toDouble() + 0.5).toInt()
            }
        } else if (realWidth.toFloat() > realHeight.toFloat() * aspectRatio) {
            realWidth = ((realHeight.toFloat() * aspectRatio).toDouble() + 0.5).toInt()
        } else {
            realHeight = ((realWidth.toFloat() / aspectRatio).toDouble() + 0.5).toInt()
        }
        if (!(realWidth == mPreviewWidth && realHeight == mPreviewHeight)) {
            sizeHasChange = true
        }
        mPreviewWidth = realWidth
        mPreviewHeight = realHeight
        logi("ContainerViewHelper", "calculatePreviewSize:" + mPreviewWidth + "x" + mPreviewHeight)
        return sizeHasChange
    }
}