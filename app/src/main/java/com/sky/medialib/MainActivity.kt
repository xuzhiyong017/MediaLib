package com.sky.medialib

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.blankj.utilcode.util.UriUtils
import com.github.gzuliyujiang.imagepicker.ImagePicker
import com.github.gzuliyujiang.imagepicker.PickCallback
import com.permissionx.guolindev.PermissionX
import com.sky.media.kit.base.BaseActivity
import com.sky.medialib.ui.crop.VideoCropActivity
import com.sky.medialib.ui.crop.VideoCropActivity.KEY_VIDEO
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        ImagePicker.getInstance().onRequestPermissionsResult(this,requestCode,permissions,grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ImagePicker.getInstance().onActivityResult(this,requestCode,resultCode,data)
    }

    fun jumpPictureEdit(view: View) {

        ImagePicker.getInstance().startGallery(this,false,object : PickCallback() {
            override fun onPickImage(imageUri: Uri?) {
                jumpEdit(imageUri)

            }
        })
    }

    fun jumpEdit(imageUri: Uri?){
        if( UriUtils.uri2File(imageUri).absolutePath.endsWith(".mp4")){
            startActivity(Intent(this,VideoCropActivity::class.java).putExtra(KEY_VIDEO, UriUtils.uri2File(imageUri).absolutePath))
        }else{
            startActivity(Intent(this,PictureEditActivity::class.java).putExtra(PICK_PICTURE,imageUri))
        }

    }

}