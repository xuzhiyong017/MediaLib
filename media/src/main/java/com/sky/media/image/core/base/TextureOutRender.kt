package com.sky.media.image.core.base

import android.opengl.GLES20
import com.sky.media.image.GLRender
import java.util.concurrent.CopyOnWriteArrayList

/**
 * @author: xuzhiyong
 * @date: 2021/7/27  下午3:55
 * @Email: 18971269648@163.com
 * @description:
 */
abstract class TextureOutRender : GLRender(){

    val nextRenders = CopyOnWriteArrayList<TextureInRender>()
    var textOut:IntArray? = null
    private var needDraw = false
    protected var frameBuffer: IntArray? = null
    protected var depthRenderBuffer: IntArray? = null
    private var mCurrentPreviewHeight = 0
    private var mCurrentPreviewWidth = 0
    protected var mIsChangeSize = false
    val mLock = Any()

    @Synchronized
    open  fun addNextRender(textureInRender: TextureInRender){
        if(!(textureInRender == null || nextRenders.contains(textureInRender))){
            nextRenders.add(textureInRender)
        }
    }

    override fun drawFrame() {
       var aleadyDraw = false
        if(textOut  == null){
            if(width != 0 && height != 0){
                initFrameBuffer()
            }else{
                return
            }
        }

        if(!(mCurrentPreviewWidth == width && mCurrentPreviewHeight == height)){
            mIsChangeSize = true
        }

        aleadyDraw = if(needDraw){
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer!![0])
            beforeDrawFrame()
            draw()
            afterDrawFrame()
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
            true
        }else{
            false
        }

        if(mIsChangeSize){
            mCurrentPreviewWidth = width
            mCurrentPreviewHeight = height
            mIsChangeSize = false
        }
        synchronized(mLock){
            for (render in nextRenders){
                if(!(render == null || textOut == null || textOut!!.isEmpty())){
                        textOut?.get(0)?.let { render.dealNextTexture(this, it,aleadyDraw) }
                    }
            }
        }
    }

    private fun draw() {
        super.drawFrame()
    }

    open fun beforeDrawFrame() {}

    open fun afterDrawFrame() {}


    private fun initFrameBuffer() {
        if(textOut != null){
            GLES20.glDeleteTextures(1,textOut,0)
            textOut = null
        }

        textOut = IntArray(1)
        GLES20.glGenTextures(1,textOut,0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textOut!![0])
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D,0,GLES20.GL_RGBA,width,height,0,GLES20.GL_RGBA,GLES20.GL_UNSIGNED_BYTE,null)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        if (frameBuffer != null) {
            GLES20.glDeleteFramebuffers(1, frameBuffer, 0)
            frameBuffer = null
        }
        if (depthRenderBuffer != null) {
            GLES20.glDeleteRenderbuffers(1, depthRenderBuffer, 0)
            depthRenderBuffer = null
        }
        frameBuffer = IntArray(1)
        depthRenderBuffer = IntArray(1)
        GLES20.glGenFramebuffers(1, frameBuffer, 0)
        GLES20.glGenRenderbuffers(1, depthRenderBuffer, 0)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,frameBuffer!![0])
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,GLES20.GL_COLOR_ATTACHMENT0,GLES20.GL_TEXTURE_2D,textOut!![0],0)
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER,depthRenderBuffer!![0])
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER,GLES20.GL_DEPTH_COMPONENT16,width,height)
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER,GLES20.GL_DEPTH_ATTACHMENT,GLES20.GL_RENDERBUFFER,depthRenderBuffer!![0])
        markNeedDraw()
    }

    open fun removeRenderIn(textureInRender: TextureInRender){
        synchronized(mLock){
            nextRenders.remove(textureInRender)
        }
    }

    open fun clearNextRenders(){
        synchronized(mLock){
            nextRenders.clear()
        }
    }

    open fun markNeedDraw() {
        needDraw = true
    }

    override fun handleSizeChange() {
        initFrameBuffer()
    }

    override fun destroy() {
        super.destroy()
        if(frameBuffer != null){
            GLES20.glDeleteFramebuffers(1,frameBuffer,0)
            frameBuffer = null
        }

        if(depthRenderBuffer != null){
            GLES20.glDeleteRenderbuffers(1,depthRenderBuffer,0)
            depthRenderBuffer = null
        }

        if(textOut != null){
            GLES20.glDeleteTextures(1,textOut,0)
            textOut = null
        }
    }


}