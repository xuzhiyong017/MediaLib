package com.sky.medialib.ui.picture.helper

import android.app.Activity
import android.view.Gravity
import android.view.WindowManager
import com.sky.medialib.ui.picture.process.ImageProcessExt
import com.sky.medialib.R
import com.sky.medialib.ui.kit.view.BeautyAdjusterPopupWindow

/**
 * @author: xuzhiyong
 * @date: 2021/7/29  下午5:40
 * @Email: 18971269648@163.com
 * @description:
 */
class PictureBeautyHelper(val activity :Activity, str:String, listener:OnImageProcessListener) {

    private var mBeautyAdjustPopWindow = BeautyAdjusterPopupWindow(activity,str,listener.getProcess()) {
                                             listener.showAllView()
                                          }

    interface OnImageProcessListener {
        fun showAllView()
        fun getProcess(): ImageProcessExt
    }

    fun showBeautyAdjustWindow(){
        mBeautyAdjustPopWindow.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        mBeautyAdjustPopWindow.showAtLocation(activity.findViewById(R.id.edit_menu),Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL,0,0)
    }

    fun hideWindow(){
        mBeautyAdjustPopWindow.dismiss()
    }
}