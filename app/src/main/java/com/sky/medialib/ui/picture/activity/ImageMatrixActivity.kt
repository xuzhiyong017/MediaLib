package com.sky.medialib.ui.picture.activity

import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.sky.medialib.R
import com.sky.medialib.ui.kit.view.crop.FastBitmapDrawable
import kotlinx.android.synthetic.main.activity_image_matrix.*

class ImageMatrixActivity : AppCompatActivity() {
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
        image.setImageDrawable(FastBitmapDrawable(BitmapFactory.decodeResource(resources,R.drawable.image1)))
    }
}