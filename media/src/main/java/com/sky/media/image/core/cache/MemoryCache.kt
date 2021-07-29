package com.sky.media.image.core.cache

import android.graphics.Bitmap

/**
 * @author: xuzhiyong
 * @date: 2021/7/29  上午9:49
 * @Email: 18971269648@163.com
 * @description:
 */
interface MemoryCache {
    /**
     * Puts value into cache by key
     *
     * @return **true** - if value was put into cache successfully, **false** - if value was **not** put into
     * cache
     */
    fun put(key: String?, value: Bitmap): Boolean

    /** Returns value by key. If there is no value for key then null will be returned.  */
    operator fun get(key: String?): Bitmap?

    /** Removes item by key  */
    fun remove(key: String?): Bitmap?

    /** Returns all keys of cache  */
    fun keys(): Collection<String>

    /** Remove all items from cache  */
    fun clear()
}