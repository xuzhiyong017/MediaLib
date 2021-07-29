package com.sky.media.image.core.pipeline

import android.graphics.Rect
import android.opengl.GLES20
import com.sky.media.image.GLRender
import com.sky.media.image.core.base.TextureOutRender
import com.sky.media.image.core.util.LogUtils.Companion.logi
import com.sky.media.image.core.render.GroupRender
import com.sky.media.image.core.view.IRender
import java.util.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class RenderPipeline : IRender {
    protected val mFiltersToDestroy: MutableList<GLRender> = ArrayList<GLRender>()
    var height = 0
        protected set

    @get:Synchronized
    protected var isRendering = false
    protected val mListeners: MutableList<OnSizeChangedListener> = ArrayList<OnSizeChangedListener>()
    protected var mRootRenderer: GLRender? = null
    var width = 0
        protected set

    open interface OnSizeChangedListener {
        open fun getSize(): Rect
        open fun onSizeChanged(i: Int, i2: Int)
    }

    fun addFilterToDestroy(gLRenderer: GLRender?) {
        synchronized(mFiltersToDestroy) {
            if (gLRenderer != null) {
                mFiltersToDestroy.add(gLRenderer)
            }
        }
    }

    fun addOnSizeChangedListener(onSizeChangedListener: OnSizeChangedListener?) {
        synchronized(mListeners) {
            if (mListeners.contains(onSizeChangedListener)) {
                mListeners.remove(onSizeChangedListener)
            }
            if (onSizeChangedListener != null) {
                mListeners.add(onSizeChangedListener)
            }
        }
    }

    @Synchronized
    fun setRootRenderer(gLRenderer: GLRender?) {
        mRootRenderer = gLRenderer
    }

    @Synchronized
    fun pauseRendering() {
        isRendering = false
    }

    @Synchronized
    fun startRendering() {
        isRendering = true
    }

    override fun onSurfaceCreated(gl10: GL10?, eGLConfig: EGLConfig?) {
        GLES20.glEnable(3042)
        GLES20.glBlendFunc(770, 771)
    }

    override fun onSurfaceChanged(gl10: GL10?, i: Int, i2: Int) {
        logi("TexturePipeline", "onSurfaceChanged:" + i + "x" + i2)
        width = i
        height = i2
        synchronized(mListeners) {
            val arrayList: MutableCollection<OnSizeChangedListener> = ArrayList<OnSizeChangedListener>()
            for (onSizeChangedListener in mListeners) {
                val size = onSizeChangedListener!!.getSize()
                if (size.width() == i && size.height() == i2) {
                    onSizeChangedListener.onSizeChanged(i, i2)
                    arrayList.add(onSizeChangedListener)
                }
            }
            mListeners.removeAll(arrayList)
        }
    }

    override fun onDrawFrame(gl10: GL10?) {
        if (isRendering) {
            if (mRootRenderer != null) {
                mRootRenderer!!.onDrawFrame()
            }
            synchronized(mFiltersToDestroy) {
                for (gLRenderer in mFiltersToDestroy) {
                    gLRenderer?.destroy()
                }
                mFiltersToDestroy.clear()
            }
        }
    }

    private fun getRenderList(
        gLRenderer: GLRender?,
        list: MutableList<GLRender>,
        list2: MutableList<GLRender>
    ) {
        if (gLRenderer != null && !list2.contains(gLRenderer)) {
            list2.add(gLRenderer)
            if (gLRenderer is GroupRender) {
                for (renderList in gLRenderer.filters) {
                    getRenderList(renderList, list, list2)
                }
            } else if (gLRenderer is TextureOutRender) {
                if (!list.contains(gLRenderer)) {
                    list.add(gLRenderer)
                }
                for (gLTextureInputRenderer in gLRenderer.nextRenders) {
                    if (gLTextureInputRenderer is GLRender) {
                        getRenderList(gLTextureInputRenderer as GLRender, list, list2)
                    }
                }
            } else if (!list.contains(gLRenderer)) {
                list.add(gLRenderer)
            }
        }
    }

    override fun onSurfaceDestroyed() {
        val arrayList: MutableList<GLRender> = ArrayList<GLRender>()
        getRenderList(mRootRenderer, arrayList, ArrayList<GLRender>())
        for (destroy in arrayList) {
            destroy!!.destroy()
        }
    }
}