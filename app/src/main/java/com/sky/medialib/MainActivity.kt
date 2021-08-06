package com.sky.medialib

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.github.gzuliyujiang.imagepicker.ImagePicker
import com.github.gzuliyujiang.imagepicker.PickCallback
import com.permissionx.guolindev.PermissionX
import com.sky.media.kit.base.BaseActivity
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
        imageUri?.let {
            PermissionX.init(MainActivity@this)
                .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
                .request { allGranted, _, _ ->
                    if (allGranted) {
                        startActivity(Intent(this,PictureEditActivity::class.java).putExtra(PICK_PICTURE,it))
                    } else {
                        Toast.makeText(this, "you denied the File permission for read and write ", Toast.LENGTH_SHORT).show()
                    }
                }
        }

    }

}