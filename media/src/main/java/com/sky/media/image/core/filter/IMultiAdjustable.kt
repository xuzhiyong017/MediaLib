package com.sky.media.image.core.filter

interface IMultiAdjustable : IAdjustable {
    fun adjust(iArr: IntArray?, iArr2: IntArray?, iArr3: IntArray?)
}