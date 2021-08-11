package com.sky.medialib.ui.kit.effect

import android.content.Context
import android.text.TextUtils
import com.sky.media.image.core.base.BaseRender
import com.sky.media.image.core.cache.IBitmapCache
import com.sky.media.image.core.render.GroupRender
import com.sky.media.image.core.render.MultiBmpInputRender
import com.sky.media.kit.render.FaceBuffingRender
import com.sky.media.kit.render.FaceWhiteningRender
import com.sky.media.kit.render.sticker.Sticker
import com.sky.media.kit.render.sticker.StickerRender
import com.sky.media.kit.render.sticker.trigger.OnTriggerStartListener
import java.util.*

class EffectRender(private val mContext: Context, private val mBitmapCache: IBitmapCache) :
    GroupRender() {
    private var mEffect: Effect? = null
    private var mIndex = 0
    private var mTriggerStartListener: OnTriggerStartListener? = null
    override fun destroy() {
        super.destroy()
        if (mIndex != 0) {
            mIndex = 0
            destroyEffect()
        }
    }

    operator fun next() {
        if (mEffect!!.count > 1) {
            mIndex++
            mIndex %= mEffect!!.count
            destroyEffect()
            createEffect()
        }
    }

    val currentText: String
        get() {
            val stringBuilder = StringBuilder()
            for (basicRender in filters) {
                if (basicRender is StickerRender) {
                    for (component in basicRender.sticker!!.component) {
                        if (!TextUtils.isEmpty(component.text)) {
                            stringBuilder.append(component.text)
                        }
                    }
                }
            }
            return stringBuilder.toString()
        }

    private fun destroyEffect() {
        val arrayList: ArrayList<BaseRender> = ArrayList<BaseRender>()
        arrayList.addAll(filters)
        runOnDraw {
            val it: Iterator<BaseRender> = arrayList.iterator()
            while (it.hasNext()) {
                it.next().destroy()
            }
        }
        initialFilters.clear()
        filters.clear()
        terminalFilters.clear()
    }

    private fun createEffect() {
        val arrayList: MutableList<BaseRender> = ArrayList<BaseRender>()
        if (mEffect!!.filter != null) {
            for (filter in mEffect!!.filter) {
                if (filter.index === mIndex || filter.index === -1) {
                    when (filter.type) {
                        0 -> {
                            val faceBuffingRender = FaceBuffingRender()
                            faceBuffingRender.adjust(filter.strength, 0, 100)
                            arrayList.add(faceBuffingRender)
                        }
                        3 -> {
                            val faceWhiteningRender = FaceWhiteningRender()
                            faceWhiteningRender.adjust(filter.strength, 0, 100)
                            arrayList.add(faceWhiteningRender)
                        }
                        4 -> {
                            val stickerRender = StickerRender(mContext, mBitmapCache)
                            val sticker = Sticker()
                            sticker.component = filter.component
                            sticker.face_count = filter.face_count
                            sticker.componentResourceMap = filter.componentResourceMap
                            stickerRender.sticker = sticker
                            stickerRender.setOnTriggerStartListener(mTriggerStartListener)
                            arrayList.add(stickerRender)
                        }
                        5 -> {
                            val multiBmpInputRender = MultiBmpInputRender(mBitmapCache)
                            if (filter.textures != null) {
                                multiBmpInputRender.setImages(
                                    mContext,
                                    filter.textures.toTypedArray()
                                )
                            }
                            if (!TextUtils.isEmpty(filter.fshader)) {
                                multiBmpInputRender.mFragmentShader = filter.fshader
                            }
                            if (!TextUtils.isEmpty(filter.vshader)) {
                                multiBmpInputRender.mVertexShader = filter.vshader
                            }
                            arrayList.add(multiBmpInputRender)
                        }
                        else -> {
                        }
                    }
                }
            }
            var basicRender: BaseRender
            val basicRender2: BaseRender
            if (arrayList.size == 1) {
                basicRender = arrayList[0] as BaseRender
                basicRender.addNextRender(this)
                registerInitialFilter(basicRender)
                registerTerminalFilter(basicRender)
            } else if (arrayList.size == 2) {
                basicRender = arrayList[0] as BaseRender
                basicRender2 = arrayList[1] as BaseRender
                basicRender.addNextRender(basicRender2)
                basicRender2.addNextRender(this)
                registerInitialFilter(basicRender)
                registerTerminalFilter(basicRender2)
            } else if (arrayList.size > 2) {
                basicRender2 = arrayList[arrayList.size - 1] as BaseRender
                registerInitialFilter(arrayList[0] as BaseRender)
                for (i in 0 until arrayList.size - 1) {
                    basicRender = arrayList[i] as BaseRender
                    basicRender.addNextRender(arrayList[i + 1] as BaseRender?)
                    registerFilter(basicRender)
                }
                basicRender2.addNextRender(this)
                registerTerminalFilter(basicRender2)
            }
        }
    }

    fun setEffect(effect: Effect?, onTriggerStartListener: OnTriggerStartListener?) {
        mEffect = effect
        mTriggerStartListener = onTriggerStartListener
        mIndex = 0
        destroyEffect()
        createEffect()
    }

    var effect: Effect?
        get() = mEffect
        set(effect) {
            setEffect(effect, null)
        }
}