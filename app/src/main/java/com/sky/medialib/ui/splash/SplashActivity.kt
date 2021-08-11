package com.sky.medialib.ui.splash

import android.Manifest
import android.content.Intent
import android.os.Bundle
import com.permissionx.guolindev.PermissionX
import com.sky.media.kit.base.BaseActivity
import com.sky.medialib.R
import com.sky.medialib.ui.camera.CameraActivity
import com.sky.medialib.util.ToastUtils

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        PermissionX.init(this)
            .permissions(Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .request{ allgrant,_,_ ->
                if(allgrant){
                    startActivity(Intent(this,CameraActivity::class.java))
                    finish()
                }else{
                    ToastUtils.showToast("请授权后进入")
                    finish()
                }
            }
    }
}