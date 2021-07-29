package com.sky.medialib

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sky.media.image.core.process.ImageProcessExt
import com.sky.media.kit.filter.BlackWhite
import com.sky.media.kit.filter.GaussianBlur
import com.sky.media.kit.filter.HighlightShadow
import com.sky.media.kit.filter.WhiteningTool
import kotlinx.android.synthetic.main.activity_picture_edit.*

class PictureEditActivity : AppCompatActivity() {

    var mEditImageProcessExt:ImageProcessExt? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture_edit)

        mEditImageProcessExt = ImageProcessExt(frame,processing_view)
        mEditImageProcessExt?.initInputBitmap(BitmapFactory.decodeResource(resources,R.drawable.image1),
            resources.displayMetrics.widthPixels,resources.displayMetrics.heightPixels,null)


        mEditImageProcessExt?.addFilter(HighlightShadow())
        mEditImageProcessExt?.addFilter(WhiteningTool())
        mEditImageProcessExt?.addFilter(BlackWhite(this))
        mEditImageProcessExt?.addFilter(GaussianBlur())
    }
}