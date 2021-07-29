package com.sky.media.image.core.base

import com.sky.media.image.GLRender
import com.sky.media.image.core.util.LogUtils
import com.sky.media.image.core.pipeline.RenderPipeline

/**
 * @author: xuzhiyong
 * @date: 2021/7/27  下午6:15
 * @Email: 18971269648@163.com
 * @description:
 */

const val TAG = "OnScreenEndPoint"
class OnScreenEndPoint(val renderPipeline: RenderPipeline):GLRender(),TextureInRender {

    override fun initWithGLContext() {
        setRenderSize(renderPipeline.width,renderPipeline.height)
        super.initWithGLContext()
    }

    override fun dealNextTexture(
        textureOutRender: TextureOutRender,
        textureId: Int,
        needDraw: Boolean
    ) {

        texture_in = textureId
        width = textureOutRender.width
        height = textureOutRender.height
        LogUtils.logi(TAG,"OnScreenEndPoint widthxheight=$width x $height")
        onDrawFrame()
    }
}