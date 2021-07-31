package com.sky.medialib.ui.picture.helper

import android.app.Activity
import android.graphics.*
import android.widget.Toast
import com.sky.media.image.core.out.BitmapOutput
import com.sky.medialib.R
import com.sky.medialib.util.Storage

/**
 * @author: xuzhiyong
 * @date: 2021/7/31  下午2:09
 * @Email: 18971269648@163.com
 * @description: savePic
 */
class PictureFilterHelper(val activity: Activity,val onImageProcessListener: PictureBeautyHelper.OnImageProcessListener) {

    var stickerHelper: PictureStickerHelper? = null
    private val mEditImageProcessExt = onImageProcessListener.getProcess()
    private val bitmapWaterMarker = BitmapFactory.decodeResource(activity.resources, R.drawable.watermark_logo)

    fun savePic(needWaterMark:Boolean){
        mEditImageProcessExt.getOutputBitmap(object : BitmapOutput.BitmapOutputCallback{
            override fun bitmapOutput(bitmap: Bitmap?) {
                if (bitmap != null) {
                    val copy = bitmap.copy(Bitmap.Config.ARGB_8888,true)
                    val canvas = Canvas(copy)
                    stickerHelper?.drawCanvas(canvas,copy.width,copy.height)
                    if(needWaterMark){
                        val width3 = (copy.width.toFloat() * 0.2f).toInt()
                        Canvas(copy).drawBitmap(bitmapWaterMarker,null,RectF((copy.width - width3).toFloat(),
                            (copy.height - (( width3 / bitmapWaterMarker.width) * bitmapWaterMarker.height)).toFloat(),
                             copy.width.toFloat(),
                            copy.height.toFloat()), Paint()
                        )
                    }

                    Storage.storageToDCIMJpgPath(copy)
                    Toast.makeText(activity,"save pic success",Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}