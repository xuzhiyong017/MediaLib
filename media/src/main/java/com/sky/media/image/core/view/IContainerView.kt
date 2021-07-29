package com.sky.media.image.core.view

import android.content.Context

interface IContainerView {
    fun getContext(): Context?
    fun getPreviewHeight() : Int
    fun getPreviewWidth() : Int
    fun requestLayout()
    fun setAspectRatio(f: Float, i: Int, i2: Int): Boolean
    fun setRotate90Degrees(i: Int): Boolean
}