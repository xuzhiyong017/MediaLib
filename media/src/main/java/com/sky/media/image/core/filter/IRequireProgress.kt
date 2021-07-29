package com.sky.media.image.core.filter

interface IRequireProgress {
    fun getDuration(): Long
    fun setProgress(f: Float)
}