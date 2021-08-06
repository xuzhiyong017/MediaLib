package com.sky.media.kit.camera

import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.opengl.GLES11Ext
import android.opengl.GLES20
import com.sky.media.image.core.base.TextureOutRender
import com.sky.media.image.core.extra.FpsTest
import com.sky.media.image.core.view.IRenderView

/**
 * @author: xuzhiyong
 * @date: 2021/8/6  上午9:18
 * @Email: 18971269648@163.com
 * @description:
 */
class CameraInput(val iRenderView : IRenderView,val mCamera: Camera) : TextureOutRender(),SurfaceTexture.OnFrameAvailableListener {

    private var mMatrixHandle = 0
    private var mSurfaceTexture: SurfaceTexture? = null
    private val mMatrix = FloatArray(16)

    init {
        mFragmentShader = """#extension GL_OES_EGL_image_external : require
                            precision mediump float;
                            uniform samplerExternalOES inputImageTexture;
                            varying vec2 textureCoordinate;
                            void main() {
                                gl_FragColor = texture2D(inputImageTexture, textureCoordinate);
                            }
                                  """

        mVertexShader = """ uniform mat4 u_Matrix;
                             attribute vec4 position;
                             attribute vec2 inputTextureCoordinate;
                             varying vec2 textureCoordinate;
                             void main() {
                                vec4 texPos = u_Matrix * vec4(inputTextureCoordinate, 1, 1);
                                textureCoordinate = texPos.xy;
                                gl_Position = position;
                            }"""
    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        markNeedDraw()
        iRenderView.requestRender()
    }

    override fun initShaderHandles() {
        super.initShaderHandles()
        mMatrixHandle = GLES20.glGetUniformLocation(programHandle,"u_Matrix")
    }

    override fun initWithGLContext() {
        super.initWithGLContext()
        val iArr = IntArray(1)
        GLES20.glGenTextures(1, iArr, 0)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, iArr[0])
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR.toFloat())
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR.toFloat())
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        texture_in = iArr[0]
        mSurfaceTexture = SurfaceTexture(texture_in)
        mSurfaceTexture!!.setOnFrameAvailableListener(this)
        try {
            mCamera.setPreviewTexture(mSurfaceTexture)
            mCamera.startPreview()
            setRenderSize()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun bindShaderValues() {
        renderVertices!!.position(0)
        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 8, renderVertices)
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

    override fun drawFrame() {
        try {
            mSurfaceTexture?.updateTexImage()
        }catch (e:Exception){
            e.printStackTrace()
        }
        super.drawFrame()
        FpsTest.getInstance().countFps()
    }

    private fun setRenderSize() {
        val previewSize = mCamera.parameters.previewSize
        setRenderSize(previewSize.height, previewSize.width)
    }

    override fun destroy() {
        super.destroy()

        mSurfaceTexture?.release()
        mSurfaceTexture = null

        if (texture_in !== 0) {
            GLES20.glDeleteTextures(1, intArrayOf(texture_in), 0)
            texture_in = 0
        }
    }


}