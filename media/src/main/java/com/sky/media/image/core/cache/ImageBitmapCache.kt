package com.sky.media.image.core.cache

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.graphics.Bitmap
import android.os.Build
import com.sky.media.kit.BaseMediaApplication

/**
 * @author: xuzhiyong
 * @date: 2021/7/29  上午9:23
 * @Email: 18971269648@163.com
 * @description:
 */
object ImageBitmapCache : IBitmapCache{

    fun getInstance():ImageBitmapCache{
        return this
    }

    private val bitmapPool: MemoryCache = getMemoryCache(BaseMediaApplication.sContext,0)

    private fun getMemoryCache(context:Context,memoryCacheSize:Int): MemoryCache {
        var size = memoryCacheSize
        if (size == 0) {
            val am:ActivityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            var memoryClass = am.memoryClass
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && (context.applicationInfo.flags and ApplicationInfo.FLAG_LARGE_HEAP != 0)
            ) {
                memoryClass = am.largeMemoryClass
            }
            size = 1024 * 1024 * memoryClass / 8
        }

        return LruMemoryCache(size)
    }

    override fun clear() {
        bitmapPool.clear()
    }

    override fun put(string: String, bitmap: Bitmap) {
        bitmapPool.put(string,bitmap)
    }

    override fun get(string: String): Bitmap? {
        return bitmapPool[string]
    }
}