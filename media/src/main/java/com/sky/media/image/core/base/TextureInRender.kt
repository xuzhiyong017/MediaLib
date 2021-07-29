package com.sky.media.image.core.base

/**
 * @author: xuzhiyong
 * @date: 2021/7/27  下午3:56
 * @Email: 18971269648@163.com
 * @description:
 */
interface TextureInRender {
    fun dealNextTexture(textureOutRender:TextureOutRender,textureId:Int,needDraw: Boolean)
}