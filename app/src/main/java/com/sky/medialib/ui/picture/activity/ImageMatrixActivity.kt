package com.sky.medialib.ui.picture.activity

import android.Manifest
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.widget.ImageView
import com.permissionx.guolindev.PermissionX
import com.sky.media.kit.base.BaseActivity
import com.sky.medialib.R
import com.sky.medialib.ui.camera.CameraActivity
import com.sky.medialib.ui.kit.view.crop.FastBitmapDrawable
import kotlinx.android.synthetic.main.activity_image_matrix.*

class ImageMatrixActivity : BaseActivity() {

    final val TAG = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_matrix)

        image.scaleType = ImageView.ScaleType.MATRIX
        image.imageMatrix = Matrix().apply {
            postScale(0.4f,0.4f)
            postTranslate(43f,131f)
            val floatArray = FloatArray(9)
            this.getValues(floatArray)

            var count = 0
            floatArray.forEach {
                print("$it ")
                count++;
                if(count != 0 && count % 3 == 0){
                    println()
                }
            }

        }
//        image.setImageDrawable(FastBitmapDrawable(BitmapFactory.decodeResource(resources,R.drawable.image1)))

        image.setOnClickListener {
            PermissionX.init(this)
                .permissions(Manifest.permission.CAMERA)
                .request { allgrant, _, _ ->
                    if(allgrant){
                        startActivity(Intent(this,CameraActivity::class.java))
                    }
                }

        }
    }
}