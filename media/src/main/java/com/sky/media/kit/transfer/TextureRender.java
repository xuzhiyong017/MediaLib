package com.sky.media.kit.transfer;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

class TextureRender implements OutputSurface.OnProcessCallback {
    private int rotationIndex = 0;
    private FloatBuffer verticesBuffer;
    private FloatBuffer[] textureBufferList;
    private float[] mvpMatrix = new float[16];
    private float[] stMatrix = new float[16];
    private int program;
    private int textureId = -12345;
    private int mMVPMatrixHandle;
    private int mSTMatrixHandle;
    private int positionHandle;
    private int textureCoordHandle;
    private SurfaceTexture surfaceTexture;

    public TextureRender() {
        Matrix.setIdentityM(this.stMatrix, 0);
        float[] fArr = new float[]{-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f};
        this.verticesBuffer = ByteBuffer.allocateDirect(fArr.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.verticesBuffer.put(fArr).position(0);
        this.textureBufferList = new FloatBuffer[4];
        fArr = new float[]{0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f};
        this.textureBufferList[0] = ByteBuffer.allocateDirect(fArr.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.textureBufferList[0].put(fArr).position(0);
        fArr = new float[]{1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f};
        this.textureBufferList[1] = ByteBuffer.allocateDirect(fArr.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.textureBufferList[1].put(fArr).position(0);
        fArr = new float[]{1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f};
        this.textureBufferList[2] = ByteBuffer.allocateDirect(fArr.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.textureBufferList[2].put(fArr).position(0);
        fArr = new float[]{0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f};
        this.textureBufferList[3] = ByteBuffer.allocateDirect(fArr.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.textureBufferList[3].put(fArr).position(0);
    }

    public void setRotation(int i) {
        if (i > 0 && i < 360) {
            this.rotationIndex = i / 90;
        }
    }

    public void processVideo(long j) {
        this.surfaceTexture.getTransformMatrix(this.stMatrix);
        GLES20.glClearColor(0.0f, 1.0f, 0.0f, 1.0f);
        GLES20.glClear(16640);
        GLES20.glUseProgram(this.program);
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(36197, this.textureId);
        this.verticesBuffer.position(0);
        GLES20.glVertexAttribPointer(this.positionHandle, 2, 5126, false, 8, this.verticesBuffer);
        GLES20.glEnableVertexAttribArray(this.positionHandle);
        this.textureBufferList[this.rotationIndex].position(0);
        GLES20.glVertexAttribPointer(this.textureCoordHandle, 2, 5126, false, 8, this.textureBufferList[this.rotationIndex]);
        GLES20.glEnableVertexAttribArray(this.textureCoordHandle);
        Matrix.setIdentityM(this.mvpMatrix, 0);
        GLES20.glUniformMatrix4fv(this.mMVPMatrixHandle, 1, false, this.mvpMatrix, 0);
        GLES20.glUniformMatrix4fv(this.mSTMatrixHandle, 1, false, this.stMatrix, 0);
        GLES20.glDrawArrays(5, 0, 4);
        GLES20.glFinish();
    }

    public void bindHandle() {
        this.program = generateProgram("uniform mat4 uMVPMatrix;\n" +
                "uniform mat4 uSTMatrix;\n" +
                "attribute vec4 aPosition;\n" +
                "attribute vec4 aTextureCoord;\n" +
                "varying vec2 vTextureCoord;\n" +
                "void main() {\n" +
                "    gl_Position = uMVPMatrix * aPosition;\n" +
                "    vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n" +
                "}","#extension GL_OES_EGL_image_external : require\n" +
                "precision mediump float;\n" +
                "varying vec2 vTextureCoord;\n" +
                "uniform samplerExternalOES sTexture;\n" +
                "void main() {\n" +
                "    gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
                "}");
        if (this.program == 0) {
            throw new RuntimeException("failed creating program");
        }
        this.positionHandle = GLES20.glGetAttribLocation(this.program, "aPosition");
        if (this.positionHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aPosition");
        }
        this.textureCoordHandle = GLES20.glGetAttribLocation(this.program, "aTextureCoord");
        if (this.textureCoordHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aTextureCoord");
        }
        this.mMVPMatrixHandle = GLES20.glGetUniformLocation(this.program, "uMVPMatrix");
        if (this.mMVPMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uMVPMatrix");
        }
        this.mSTMatrixHandle = GLES20.glGetUniformLocation(this.program, "uSTMatrix");
        if (this.mSTMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uSTMatrix");
        }
        int[] iArr = new int[1];
        GLES20.glGenTextures(1, iArr, 0);
        this.textureId = iArr[0];
        GLES20.glBindTexture(36197, this.textureId);
        GLES20.glTexParameterf(36197, 10241, 9729.0f);
        GLES20.glTexParameterf(36197, 10240, 9729.0f);
        GLES20.glTexParameteri(36197, 10242, 33071);
        GLES20.glTexParameteri(36197, 10243, 33071);
        this.surfaceTexture = new SurfaceTexture(this.textureId);
    }

    public SurfaceTexture getSurfaceTexture() {
        bindHandle();
        return this.surfaceTexture;
    }

    private int complieShader(int i, String str) {
        int glCreateShader = GLES20.glCreateShader(i);
        GLES20.glShaderSource(glCreateShader, str);
        GLES20.glCompileShader(glCreateShader);
        int[] iArr = new int[1];
        GLES20.glGetShaderiv(glCreateShader, 35713, iArr, 0);
        if (iArr[0] != 0) {
            return glCreateShader;
        }
        Log.e("TextureRender", "Could not compile shader " + i + ":");
        Log.e("TextureRender", " " + GLES20.glGetShaderInfoLog(glCreateShader));
        GLES20.glDeleteShader(glCreateShader);
        return 0;
    }

    private int generateProgram(String str, String str2) {
        int a = complieShader(35633, str);
        if (a == 0) {
            return 0;
        }
        int a2 = complieShader(35632, str2);
        if (a2 == 0) {
            return 0;
        }
        int glCreateProgram = GLES20.glCreateProgram();
        if (glCreateProgram == 0) {
            Log.e("TextureRender", "Could not create program");
        }
        GLES20.glAttachShader(glCreateProgram, a);
        GLES20.glAttachShader(glCreateProgram, a2);
        GLES20.glLinkProgram(glCreateProgram);
        int[] iArr = new int[1];
        GLES20.glGetProgramiv(glCreateProgram, 35714, iArr, 0);
        if (iArr[0] == 1) {
            return glCreateProgram;
        }
        Log.e("TextureRender", "Could not link program: ");
        Log.e("TextureRender", GLES20.glGetProgramInfoLog(glCreateProgram));
        GLES20.glDeleteProgram(glCreateProgram);
        return 0;
    }
}
