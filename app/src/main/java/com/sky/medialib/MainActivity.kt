package com.sky.medialib

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.permissionx.guolindev.PermissionX
import com.sky.media.image.core.view.GLSurfaceView
import com.sky.media.image.core.view.GLTextureView
import com.sky.medialib.render.Square
import com.sky.medialib.ui.picture.activity.ImageMatrixActivity
import com.sky.medialib.util.AESUtil
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

        PermissionX.init(this)
            .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    startActivity(Intent(this,PictureEditActivity::class.java))
                } else {
                    Toast.makeText(this, "you denied the File permission for read and write ", Toast.LENGTH_SHORT).show()
                }
            }

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