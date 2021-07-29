package com.sky.media.image.core.view

import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

interface IRender {
    fun onDrawFrame(gl10: GL10?)
    fun onSurfaceChanged(gl10: GL10?, i: Int, i2: Int)
    fun onSurfaceCreated(gl10: GL10?, eGLConfig: EGLConfig?)
    fun onSurfaceDestroyed()
}