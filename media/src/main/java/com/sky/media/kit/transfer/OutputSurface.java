package com.sky.media.kit.transfer;

import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.view.Surface;

public class OutputSurface implements OnFrameAvailableListener {

    private EGLDisplay eglDisplay = EGL14.EGL_NO_DISPLAY;
    private EGLContext eglContext = EGL14.EGL_NO_CONTEXT;
    private EGLSurface eglSurface = EGL14.EGL_NO_SURFACE;
    private SurfaceTexture surfaceTexture;
    private Surface surface;
    private final Object mLock = new Object();
    private boolean isFrameAvailable;
    private OnProcessCallback callback;

    public interface OnProcessCallback {
        SurfaceTexture getSurfaceTexture();
        void processVideo(long j);
    }

    public OutputSurface(OnProcessCallback onProcessCallback) {
        this.callback = onProcessCallback;
        init();
    }

    private void init() {
        this.surfaceTexture = this.callback.getSurfaceTexture();
        this.surfaceTexture.setOnFrameAvailableListener(this);
        this.surface = new Surface(this.surfaceTexture);
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
        this.callback = null;
        this.surface = null;
        this.surfaceTexture = null;
    }

    public Surface getSurface() {
        return this.surface;
    }

    public void updateTexImage() {
        synchronized (this.mLock) {
            do {
                if (this.isFrameAvailable) {
                    this.isFrameAvailable = false;
                } else {
                    try {
                        this.mLock.wait(10000);
                    } catch (Throwable e) {
                        throw new RuntimeException("Surface frame wait timed out");
                    }
                }
            } while (this.isFrameAvailable);
        }
        this.surfaceTexture.updateTexImage();
    }

    public void processVideo(long j) {
        this.callback.processVideo(j);
    }

    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        synchronized (this.mLock) {
            if (this.isFrameAvailable) {
                throw new RuntimeException("mFrameAvailable already set, frame could be dropped");
            }
            this.isFrameAvailable = true;
            this.mLock.notifyAll();
        }
    }
}
