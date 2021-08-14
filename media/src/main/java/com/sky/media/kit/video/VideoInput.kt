package com.sky.media.kit.video

import android.graphics.SurfaceTexture
import android.net.Uri
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.os.Handler
import android.os.Looper
import android.view.Surface
import com.sky.media.image.core.base.TextureOutRender
import com.sky.media.image.core.extra.FpsTest
import com.sky.media.image.core.view.IRenderView
import com.sky.media.kit.player.IMediaPlayer
import java.io.IOException

/**
 * @author: xuzhiyong
 * @date: 2021/8/9  上午10:40
 * @Email: 18971269648@163.com
 * @description:
 */
class VideoInput(val iRenderView: IRenderView) : TextureOutRender(),SurfaceTexture.OnFrameAvailableListener {


    private var mSurfaceTexture: SurfaceTexture? = null
    private var playUri: Uri? = null
    private var mMediaPlayer: IMediaPlayer? = null
    private var mMatrixHandle = 0
    private val mMatrix = FloatArray(16)
    private var isNeedPlay = false
    private var isPrepared = false
    private var leftVolume = 1.0f
    private var rightVolume = 1.0f
    private var isLoop = false
    private var mPrepareListener: OnPreparedListener? = null

    interface OnPreparedListener {
        fun onPrepared(iMediaPlayer: IMediaPlayer)
    }
    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        markNeedDraw()
        iRenderView.requestRender()
    }


    fun setOnPreparedListener(onPreparedListener: OnPreparedListener?) {
        mPrepareListener = onPreparedListener
    }

    @Throws(IOException::class)
    fun setMediaPlayerAndUri(iMediaPlayer: IMediaPlayer?, uri: Uri?) {
        if (uri != null) {
            releaseMediaPlayer()
            playUri = uri
            mMediaPlayer = iMediaPlayer
            mMediaPlayer!!.setDataSource(iRenderView.getContext(), playUri)
            mMediaPlayer!!.setVolume(leftVolume, rightVolume)
            mMediaPlayer!!.setLooping(isLoop)
            reInitialize()
        }
    }

    fun getMediaPlayer(): IMediaPlayer? {
        return mMediaPlayer
    }

    fun setLooping(z: Boolean) {
        isLoop = z
        mMediaPlayer?.setLooping(isLoop)
    }

    fun setVolumeLeftAndRight(f: Float, f2: Float) {
        leftVolume = f
        rightVolume = f2
        mMediaPlayer?.setVolume(leftVolume, rightVolume)
    }

    fun getPlayUri(): Uri? {
        return playUri
    }

    override fun drawFrame() {
        try {
            mSurfaceTexture!!.updateTexImage()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        super.drawFrame()
        FpsTest.getInstance().countFps()
    }

    override fun initShaderHandles() {
        super.initShaderHandles()
        mMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_Matrix")
    }

    override fun initWithGLContext() {
        super.initWithGLContext()
        isPrepared = false
        if (texture_in !== 0) {
            GLES20.glDeleteTextures(1, intArrayOf(texture_in), 0)
            texture_in = 0
        }
        val iArr = IntArray(1)
        GLES20.glGenTextures(1, iArr, 0)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, iArr[0])
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR.toFloat())
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR.toFloat())
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        texture_in = iArr[0]
        if (mSurfaceTexture != null) {
            mSurfaceTexture!!.release()
            mSurfaceTexture = null
        }
        mSurfaceTexture = SurfaceTexture(texture_in)
        mSurfaceTexture!!.setOnFrameAvailableListener(this)
        mMediaPlayer!!.setSurface(Surface(mSurfaceTexture))
        Handler(Looper.getMainLooper()).post {
            try {
                mMediaPlayer!!.prepareAsync()
                mMediaPlayer!!.setOnPreparedListener {
                    isPrepared = true
                    if (isNeedPlay) {
                        mMediaPlayer!!.start()
                    }
                    if (mPrepareListener != null) {
                        mPrepareListener!!.onPrepared(mMediaPlayer!!)
                    }
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    override fun bindShaderValues() {
        renderVertices!!.position(0)
        GLES20.glVertexAttribPointer(positionHandle, 2,  GLES20.GL_FLOAT, false, 8, renderVertices)
        GLES20.glEnableVertexAttribArray(positionHandle)
        textureVertices[curRotation]!!.position(0)
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 8, textureVertices[curRotation])
        GLES20.glEnableVertexAttribArray(texCoordHandle)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture_in)
        GLES20.glUniform1i(textureHandle, 0)
        mSurfaceTexture!!.getTransformMatrix(mMatrix)
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMatrix, 0)
    }

    fun isPlaying(): Boolean {
        return mMediaPlayer != null && mMediaPlayer!!.isPlaying
    }

    fun startPlay() {
        if (!isPrepared || mMediaPlayer == null) {
            isNeedPlay = true
        } else {
            mMediaPlayer!!.start()
        }
    }

    fun pause() {
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer!!.pause()
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    fun seekTo(i: Int) {
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer!!.seekTo(i)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    private fun releaseMediaPlayer() {
        playUri = null
        if (mMediaPlayer != null) {
            mMediaPlayer!!.release()
            mMediaPlayer = null
            isPrepared = false
        }
    }

    override fun destroy() {
        super.destroy()
        if (mSurfaceTexture != null) {
            mSurfaceTexture!!.release()
            mSurfaceTexture = null
        }
        if (texture_in !== 0) {
            GLES20.glDeleteTextures(1, intArrayOf(texture_in), 0)
            texture_in = 0
        }
        releaseMediaPlayer()
    }

    init {
        mFragmentShader = """#extension GL_OES_EGL_image_external : require
                            precision mediump float;
                            uniform samplerExternalOES inputImageTexture;
                            varying vec2 textureCoordinate;
                            void main() {
                                gl_FragColor = texture2D(inputImageTexture, textureCoordinate);
                            }"""

        mVertexShader = """uniform mat4 u_Matrix;
                            attribute vec4 position;
                            attribute vec2 inputTextureCoordinate;
                            varying vec2 textureCoordinate;
                            void main() {
                                vec4 texPos = u_Matrix * vec4(inputTextureCoordinate, 1, 1);
                                textureCoordinate = texPos.xy;
                                gl_Position = position;
                            }"""
    }
}