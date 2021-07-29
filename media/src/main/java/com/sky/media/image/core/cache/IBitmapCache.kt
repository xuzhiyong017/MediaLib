package com.sky.media.image.core.cache

import android.graphics.Bitmap

/**
 * @author: xuzhiyong
 * @date: 2021/7/29  上午9:22
 * @Email: 18971269648@163.com
 * @description:
 */
interface IBitmapCache {
    fun clear()
    fun put(string: String,bitmap: Bitmap)
    fun get(string: String):Bitmap?
}