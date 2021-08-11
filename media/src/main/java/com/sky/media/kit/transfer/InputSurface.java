package com.sky.media.kit.transfer;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.view.Surface;

class InputSurface {
    private EGLDisplay eglDisplay = EGL14.EGL_NO_DISPLAY;
    private EGLContext eglContext = EGL14.EGL_NO_CONTEXT;
    private EGLSurface eglSurface = EGL14.EGL_NO_SURFACE;
    private Surface surface;

    public InputSurface(Surface surface) {
        if (surface == null) {
            throw new NullPointerException();
        }
        this.surface = surface;
        initEGLContext();
    }

    private void initEGLContext() {
        this.eglDisplay = EGL14.eglGetDisplay(0);
        if (this.eglDisplay == EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("unable to get EGL14 display");
        }
        int[] iArr = new int[2];
        if (EGL14.eglInitialize(this.eglDisplay, iArr, 0, iArr, 1)) {
            EGLConfig[] eGLConfigArr = new EGLConfig[1];
            if (EGL14.eglChooseConfig(this.eglDisplay, new int[]{12324, 8, 12323, 8, 12322, 8, 12352, 4, 12610, 1, 12344}, 0, eGLConfigArr, 0, eGLConfigArr.length, new int[1], 0)) {
                this.eglContext = EGL14.eglCreateContext(this.eglDisplay, eGLConfigArr[0], EGL14.EGL_NO_CONTEXT, new int[]{12440, 2, 12344}, 0);
                checkEGLError("eglCreateContext");
                if (this.eglContext == null) {
                    throw new RuntimeException("null context");
                }
                this.eglSurface = EGL14.eglCreateWindowSurface(this.eglDisplay, eGLConfigArr[0], this.surface, new int[]{12344}, 0);
                checkEGLError("eglCreateWindowSurface");
                if (this.eglSurface == null) {
                    throw new RuntimeException("surface was null");
                }
                return;
            }
            throw new RuntimeException("unable to find RGB888+recordable ES2 EGL config");
        }
        this.eglDisplay = null;
        throw new RuntimeException("unable to initialize EGL14");
    }

    public void release() {
        if (this.eglDisplay != EGL14.EGL_NO_DISPLAY) {
            EGL14.eglDestroySurface(this.eglDisplay, this.eglSurface);
            EGL14.eglDestroyContext(this.eglDisplay, this.eglContext);
            EGL14.eglReleaseThread();
            EGL14.eglTerminate(this.eglDisplay);
        }
        this.surface.release();
        this.eglDisplay = EGL14.EGL_NO_DISPLAY;
        this.eglContext = EGL14.EGL_NO_CONTEXT;
        this.eglSurface = EGL14.EGL_NO_SURFACE;
        this.surface = null;
    }

    public void eglMakeCurrent() {
        if (!EGL14.eglMakeCurrent(this.eglDisplay, this.eglSurface, this.eglSurface, this.eglContext)) {
            throw new RuntimeException("eglMakeCurrent failed");
        }
    }

    public boolean eglSwap() {
        return EGL14.eglSwapBuffers(this.eglDisplay, this.eglSurface);
    }

    public void eglPresentationTimeANDROID(long j) {
        EGLExt.eglPresentationTimeANDROID(this.eglDisplay, this.eglSurface, j);
    }

    private void checkEGLError(String str) {
        int eglGetError = EGL14.eglGetError();
        if (eglGetError != EGL14.EGL_SUCCESS) {
            throw new RuntimeException(str + ": EGL error: 0x" + Integer.toHexString(eglGetError));
        }
    }
}
