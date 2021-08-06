package com.sky.media.image.core.render

import com.sky.media.image.core.base.BaseRender
import com.sky.media.image.core.base.TextureOutRender
import java.util.concurrent.CopyOnWriteArrayList

class GroupRender : BaseRender() {
    var filters: MutableList<BaseRender> = CopyOnWriteArrayList<BaseRender>()
    var initialFilters: MutableList<BaseRender> = CopyOnWriteArrayList<BaseRender>()
    var terminalFilters: MutableList<BaseRender> = CopyOnWriteArrayList<BaseRender>()

    override fun destroy() {
        super.destroy()
        for (destroy in filters) {
            destroy.destroy()
        }
    }

    override fun reInitialize() {
        for (reInitialize in filters) {
            reInitialize.reInitialize()
        }
    }

    override fun dealNextTexture(
        textureOutRender: TextureOutRender,
        textureId: Int,
        needDraw: Boolean
    ) {
        runAll(this.mRunOnDraw)
        if (terminalFilters.contains(textureOutRender)) {
            setWidth(textureOutRender.getWidth())
            setHeight(textureOutRender.getHeight())
            synchronized(mLock) {
                for (newTextureReady in nextRenders) {
                    newTextureReady.dealNextTexture(this,textureId, needDraw)
                }
            }
            return
        }
        for (newTextureReady2 in initialFilters) {
            newTextureReady2.dealNextTexture(textureOutRender,textureId, needDraw)
        }
    }

    fun registerFilter(basicRender: BaseRender) {
        if (!filters.contains(basicRender)) {
            filters.add(basicRender)
        }
    }

    fun registerInitialFilter(basicRender: BaseRender) {
        initialFilters.add(basicRender)
        registerFilter(basicRender)
    }

    fun registerTerminalFilter(basicRender: BaseRender) {
        terminalFilters.add(basicRender)
        registerFilter(basicRender)
    }


    override fun setRenderSize(i: Int, i2: Int) {
        for (renderSize in filters) {
            renderSize.setRenderSize(i, i2)
        }
    }
}