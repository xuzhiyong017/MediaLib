package com.sky.media.image.core.filter

interface IAdjustable {
    fun adjust(cur: Int, start: Int, end: Int)
}