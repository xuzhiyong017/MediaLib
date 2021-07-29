package com.sky.media.image.core.cache

import android.graphics.Bitmap
import java.lang.NullPointerException
import java.util.HashSet
import java.util.LinkedHashMap

/**
 * @author: xuzhiyong
 * @date: 2021/7/29  上午9:50
 * @Email: 18971269648@163.com
 * @description:
 * @param maxSize Maximum sum of the sizes of the Bitmaps in this cache
 */
class LruMemoryCache(val maxSize: Int) : MemoryCache {


    private var map: LinkedHashMap<String, Bitmap>? = null

    /** Size of this cache in bytes  */
    private var size = 0

   init {
       require(maxSize > 0) { "maxSize <= 0" }
       map = LinkedHashMap(0, 0.75f, true)
   }

    /**
     * Returns the Bitmap for `key` if it exists in the cache. If a Bitmap was returned, it is moved to the head
     * of the queue. This returns null if a Bitmap is not cached.
     */
    override fun get(key: String?): Bitmap? {
        if (key == null) {
            throw NullPointerException("key == null")
        }
        synchronized(this) { return map!![key] }
    }

    /** Caches `Bitmap` for `key`. The Bitmap is moved to the head of the queue.  */
    override fun put(key: String?, value: Bitmap): Boolean {
        if (key == null || value == null) {
            throw NullPointerException("key == null || value == null")
        }
        synchronized(this) {
            size += sizeOf(key, value)
            val previous = map!!.put(key, value)
            if (previous != null) {
                size -= sizeOf(key, previous)
            }
        }
        trimToSize(maxSize)
        return true
    }

    /**
     * Remove the eldest entries until the total of remaining entries is at or below the requested size.
     *
     * @param maxSize the maximum size of the cache before returning. May be -1 to evict even 0-sized elements.
     */
    private fun trimToSize(maxSize: Int) {
        while (true) {
            var key: String
            var value: Bitmap
            synchronized(this) {
                if (size < 0 || (map!!.isEmpty() && size != 0)) {
                    throw IllegalStateException(javaClass.name + ".sizeOf() is reporting inconsistent results!");
                }

                if (size <= maxSize || map!!.isEmpty()) {
                    return
                }
                val toEvict: MutableMap.MutableEntry<String, Bitmap> =
                    map!!.entries.iterator().next() ?: return
                key = toEvict!!.key
                value = toEvict!!.value
                map!!.remove(key)
                size -= sizeOf(key, value)
            }
        }
    }

    /** Removes the entry for `key` if it exists.  */
    override fun remove(key: String?): Bitmap? {
        if (key == null) {
            throw NullPointerException("key == null")
        }
        synchronized(this) {
            val previous = map!!.remove(key)
            if (previous != null) {
                size -= sizeOf(key, previous)
            }
            return previous
        }
    }

    override fun keys(): Collection<String> {
        synchronized(this) { return HashSet(map!!.keys) }
    }

    override fun clear() {
        trimToSize(-1) // -1 will evict 0-sized elements
    }

    /**
     * Returns the size `Bitmap` in bytes.
     *
     *
     * An entry's size must not change while it is in the cache.
     */
    private fun sizeOf(key: String, value: Bitmap): Int {
        return value.rowBytes * value.height
    }

    @Synchronized
    override fun toString(): String {
        return String.format("LruCache[maxSize=%d]", maxSize)
    }
}