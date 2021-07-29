package com.sky.media.image.core.base

/**
 * @author: xuzhiyong
 * @date: 2021/7/27  下午5:14
 * @Email: 18971269648@163.com
 * @description:
 */
abstract class BaseRender : TextureOutRender(),TextureInRender {
    override fun dealNextTexture(
        textureOutRender: TextureOutRender,
        textureId: Int,
        needDraw: Boolean
    ) {
       if(needDraw){
           markNeedDraw()
       }
        texture_in = textureId
        width = textureOutRender.width
        height = textureOutRender.height
        onDrawFrame()
    }

}