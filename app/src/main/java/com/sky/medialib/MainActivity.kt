package com.sky.medialib

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.sky.media.image.core.view.GLSurfaceView
import com.sky.media.image.core.view.GLTextureView
import com.sky.medialib.render.Square
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        surface_view.setEGLContextClientVersion(2)
        surface_view.setRenderer(Square())
        surface_view.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY

        gl_surface.setEGLContextClientVersion(2)
        gl_surface.setRenderer(Square())
        gl_surface.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY

        texture_view.setEGLContextClientVersion(2)
        texture_view.setRenderer(Square())
        texture_view.renderMode = GLTextureView.RENDERMODE_WHEN_DIRTY
        texture_view.requestRender()
    }

    fun jumpPictureEdit(view: View) {
        startActivity(Intent(this,PictureEditActivity::class.java))
    }


    override fun onResume() {
        super.onResume()
        gl_surface.onResume()
    }

    override fun onPause() {
        super.onPause()
        gl_surface.onPause()
    }

}