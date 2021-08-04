package com.sky.medialib.ui.picture.helper

import android.app.Activity
import android.graphics.Bitmap
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.blankj.utilcode.util.AdaptScreenUtils
import com.sky.medialib.R
import com.sky.medialib.ui.kit.view.crop.ClipPopupWindow
import com.sky.medialib.ui.kit.view.crop.CropView

/**
 * @author: xuzhiyong
 * @date: 2021/8/3  下午4:00
 * @Email: 18971269648@163.com
 * @description:
 */
class PictureClipHelper(val activity: Activity, val listener: ClipPopupWindow.onClipListener) {

    val mCropView = activity.findViewById<CropView>(R.id.clip_view)
    var clipPopupWindow:ClipPopupWindow? = null

    init {
        mCropView.setPadding(0,0,0,AdaptScreenUtils.pt2Px(180.0f))
        mCropView.invalidate()
    }

    fun setClipBitmap(bitmap: Bitmap?, bitmap2: Bitmap?) {
        mCropView.visibility = View.VISIBLE
        mCropView.setBitmap(bitmap, bitmap2)
        initClipView()
    }

    fun setOnImageClippedListener(onImageClippedListener: CropView.OnImageClippedListener?) {
        mCropView.setOnImageClippedListener(onImageClippedListener)
    }

    fun hideAllView() {
        if (clipPopupWindow != null) {
            clipPopupWindow!!.dismiss()
        }
        mCropView.visibility = View.GONE
    }

    fun hideCropView() {
        mCropView.visibility = View.GONE
    }


    private fun initClipView() {
        mCropView.setAreaVisible(true)
        clipPopupWindow = ClipPopupWindow(activity, mCropView,listener).apply {
            softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            showAtLocation(activity.findViewById(R.id.edit_menu),Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM,0,0)
            setOnDismissListener {
                clipPopupWindow = null
            }
        }

    }
}