package com.sky.media.image.core.render

import com.sky.media.image.core.base.BaseRender
import com.sky.media.image.core.base.TextureOutRender
import java.util.ArrayList

abstract class CompositeMultiPixelRender(numOfInputs: Int) : MultiInputPixelRender(numOfInputs) {

    private val filters: MutableList<TextureOutRender> = ArrayList<TextureOutRender>()
    private val initialFilters: MutableList<BaseRender> = ArrayList<BaseRender>()
    private val inputOutputFilters: MutableList<TextureOutRender> = ArrayList<TextureOutRender>()
    private val terminalFilters: MutableList<TextureOutRender> = ArrayList<TextureOutRender>()
    override fun destroy() {
        super.destroy()
        for (destroy in filters) {
            destroy.destroy()
        }
    }

   override fun dealNextTexture(gLTextureOutputRenderer: TextureOutRender,i: Int,  z: Boolean) {
        if (inputOutputFilters.contains(gLTextureOutputRenderer)) {
            if (!texturesReceived.contains(gLTextureOutputRenderer)) {
                super.dealNextTexture(gLTextureOutputRenderer,i, z)
                for (newTextureReady in initialFilters) {
                    newTextureReady.dealNextTexture(gLTextureOutputRenderer,i, z)
                }
            }
        } else if (terminalFilters.contains(gLTextureOutputRenderer)) {
            super.dealNextTexture(gLTextureOutputRenderer,i,  z)
        } else {
            for (newTextureReady2 in initialFilters) {
                newTextureReady2.dealNextTexture(gLTextureOutputRenderer,i,  z)
            }
        }
    }

    private fun registerFilter(gLTextureOutputRenderer: TextureOutRender) {
        if (!filters.contains(gLTextureOutputRenderer)) {
            filters.add(gLTextureOutputRenderer)
        }
    }

    protected fun registerInitialFilter(basicRender: BaseRender) {
        initialFilters.add(basicRender)
        registerFilter(basicRender)
    }

    protected fun registerInputOutputFilter(gLTextureOutputRenderer: TextureOutRender) {
        inputOutputFilters.add(gLTextureOutputRenderer)
        registerFilter(gLTextureOutputRenderer)
    }

    protected fun registerTerminalFilter(gLTextureOutputRenderer: TextureOutRender) {
        terminalFilters.add(gLTextureOutputRenderer)
        registerFilter(gLTextureOutputRenderer)
    }

    override fun setRenderSize(i: Int, i2: Int) {
        for (renderSize in filters) {
            renderSize.setRenderSize(i, i2)
        }
        super.setRenderSize(i, i2)
    }
}