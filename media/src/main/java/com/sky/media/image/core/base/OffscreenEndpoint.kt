package com.sky.media.image.core.base

import com.sky.media.image.GLRender

class OffscreenEndpoint(i: Int, i2: Int) : GLRender(), TextureInRender {

    override fun dealNextTexture(
        textureOutRender: TextureOutRender,
        textureId: Int,
        needDraw: Boolean
    ) {
        texture_in = textureId
        setWidth(textureOutRender.getWidth())
        setHeight(textureOutRender.getHeight())
        onDrawFrame()
    }

    init {
        setRenderSize(i, i2)
    }
}