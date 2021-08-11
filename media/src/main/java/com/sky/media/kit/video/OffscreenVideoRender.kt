package com.sky.media.kit.video

import com.sky.media.image.core.base.TextureOutRender
import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.opengl.Matrix
import com.sky.media.image.core.extra.FpsTest
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class OffscreenVideoRender(i: Int, i2: Int) : TextureOutRender() {
    private val vertiesData: FloatBuffer
    private val mMatrix = FloatArray(16)
    private var mMatrixHandle = 0
    open var surfaceTexture: SurfaceTexture? = null

    private fun createSurfaceTexture(): SurfaceTexture? {
        if (surfaceTexture == null) {
            val iArr = IntArray(1)
            GLES20.glGenTextures(1, iArr, 0)
            GLES20.glBindTexture(36197, iArr[0])
            GLES20.glTexParameterf(36197, 10241, 9729.0f)
            GLES20.glTexParameterf(36197, 10240, 9729.0f)
            GLES20.glTexParameteri(36197, 10242, 33071)
            GLES20.glTexParameteri(36197, 10243, 33071)
            texture_in = iArr[0]
            surfaceTexture = SurfaceTexture(texture_in)
        }
        return surfaceTexture
    }

    override fun bindShaderValues() {
        vertiesData.position(0)
        GLES20.glVertexAttribPointer(positionHandle, 3, 5126, false, 20, vertiesData)
        GLES20.glEnableVertexAttribArray(positionHandle)
        vertiesData.position(3)
        GLES20.glVertexAttribPointer(texCoordHandle, 2, 5126, false, 20, vertiesData)
        GLES20.glEnableVertexAttribArray(texCoordHandle)
        GLES20.glActiveTexture(33984)
        GLES20.glBindTexture(36197, texture_in)
        GLES20.glUniform1i(textureHandle, 0)
        surfaceTexture!!.getTransformMatrix(mMatrix)
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMatrix, 0)
    }

    override fun drawFrame() {
        createSurfaceTexture()
        surfaceTexture!!.updateTexImage()
        super.drawFrame()
        FpsTest.getInstance().countFps()
    }

    override fun initShaderHandles() {
        super.initShaderHandles()
        mMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_Matrix")
    }

    init {
        val fArr = floatArrayOf(
            -1.0f,
            -1.0f,
            0.0f,
            0.0f,
            0.0f,
            1.0f,
            -1.0f,
            0.0f,
            1.0f,
            0.0f,
            -1.0f,
            1.0f,
            0.0f,
            0.0f,
            1.0f,
            1.0f,
            1.0f,
            0.0f,
            1.0f,
            1.0f
        )
        vertiesData =
            ByteBuffer.allocateDirect(fArr.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        vertiesData.put(fArr).position(0)
        Matrix.setIdentityM(mMatrix, 0)
        setRenderSize(i, i2)

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