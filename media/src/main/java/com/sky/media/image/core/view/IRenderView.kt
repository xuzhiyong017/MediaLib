package com.sky.media.image.core.view

import android.content.Context
import com.sky.media.image.core.pipeline.RenderPipeline

interface IRenderView {
    fun getContext(): Context?
    fun getHeight(): Int
    fun getWidth(): Int
    fun initPipeline(): RenderPipeline?
    fun queueEvent(runnable: Runnable?)
    fun requestRender()
}