package com.sky.media.image.core.render

import android.opengl.GLES20
import com.sky.media.image.core.base.BaseRender

abstract class TwoPassRender : BaseRender() {
    var currentPass = 0
        private set
    private var firstPassDepthRenderBuffer: IntArray? = null
    private var firstPassFrameBuffer: IntArray? = null
    private var firstPassTextureOut: IntArray? = null

    override fun destroy() {
        super.destroy()
        if (firstPassFrameBuffer != null) {
            GLES20.glDeleteFramebuffers(1, firstPassFrameBuffer, 0)
            firstPassFrameBuffer = null
        }
        if (firstPassTextureOut != null) {
            GLES20.glDeleteTextures(1, firstPassTextureOut, 0)
            firstPassTextureOut = null
        }
        if (firstPassDepthRenderBuffer != null) {
            GLES20.glDeleteRenderbuffers(1, firstPassDepthRenderBuffer, 0)
            firstPassDepthRenderBuffer = null
        }
    }

    override fun drawFrame() {
        currentPass = 1
        if (firstPassFrameBuffer == null) {
            if (getWidth() != 0 && getHeight() != 0) {
                initFBO()
            } else {
                return
            }
        }
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, firstPassFrameBuffer!![0])
        if (this.texture_in != 0) {
            GLES20.glViewport(0, 0, getWidth(), getHeight())
            GLES20.glUseProgram(this.programHandle)
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
            GLES20.glClearColor(
                getBackgroundRed(),
                getBackgroundGreen(),
                getBackgroundBlue(),
                getBackgroundAlpha()
            )
            bindShaderValues()
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
            this.texture_in = firstPassTextureOut!![0]
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
            currentPass = 2
            super.drawFrame()
        }
    }

    protected fun initFBO() {
        if (firstPassFrameBuffer != null) {
            GLES20.glDeleteFramebuffers(1, firstPassFrameBuffer, 0)
            firstPassFrameBuffer = null
        }
        if (firstPassTextureOut != null) {
            GLES20.glDeleteTextures(1, firstPassTextureOut, 0)
            firstPassTextureOut = null
        }
        if (firstPassDepthRenderBuffer != null) {
            GLES20.glDeleteRenderbuffers(1, firstPassDepthRenderBuffer, 0)
            firstPassDepthRenderBuffer = null
        }
        firstPassFrameBuffer = IntArray(1)
        firstPassTextureOut = IntArray(1)
        firstPassDepthRenderBuffer = IntArray(1)
        GLES20.glGenFramebuffers(1, firstPassFrameBuffer, 0)
        GLES20.glGenRenderbuffers(1, firstPassDepthRenderBuffer, 0)
        GLES20.glGenTextures(1, firstPassTextureOut, 0)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, firstPassFrameBuffer!![0])
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, firstPassTextureOut!![0])
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, getWidth(),getHeight(), 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_SHORT_5_6_5, null)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, firstPassTextureOut!![0], 0)
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, firstPassDepthRenderBuffer!![0])
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, getWidth(), getHeight())
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, firstPassDepthRenderBuffer!![0])
    }
}