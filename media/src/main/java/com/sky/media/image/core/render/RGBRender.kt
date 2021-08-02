package com.sky.media.image.core.render

/**
 * @author: xuzhiyong
 * @date: 2021/8/2  下午5:33
 * @Email: 18971269648@163.com
 * @description:
 */
open class RGBRender(val R:Float,val G:Float,val B:Float)
    : ColourMatrixRender(floatArrayOf(R, 0.0f, 0.0f, 0.0f, 0.0f, G, 0.0f, 0.0f, 0.0f, 0.0f, B, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f), 1.0f) {


    fun adjustRGB( r:Float, g:Float, b:Float){
        setColourMatrix(
            floatArrayOf(
                r,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                g,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                b,
                0.0f,
                0.0f,
                0.0f,
                0.0f,
                1.0f
            )
        )
    }
}