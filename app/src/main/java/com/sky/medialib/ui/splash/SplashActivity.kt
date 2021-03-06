package com.sky.medialib.ui.splash

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.permissionx.guolindev.PermissionX
import com.sky.media.kit.base.BaseActivity
import com.sky.medialib.R
import com.sky.medialib.ui.camera.CameraActivity
import com.sky.medialib.ui.kit.common.base.AppActivity
import com.sky.medialib.util.ToastUtils
import com.weibo.mediakit.util.YuvWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashActivity : AppActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        PermissionX.init(this)
            .permissions(Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
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