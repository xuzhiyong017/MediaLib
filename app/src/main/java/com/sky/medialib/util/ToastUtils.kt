package com.sky.medialib.util

import android.widget.Toast
import com.sky.media.kit.BaseMediaApplication

/**
 * @author: xuzhiyong
 * @date: 2021/8/2  下午12:00
 * @Email: 18971269648@163.com
 * @description:
 */
object ToastUtils {

    fun show(msg:String?){
        val text = if(msg.isNullOrEmpty()) "msg tips" else msg
        Toast.makeText(BaseMediaApplication.sContext,text,Toast.LENGTH_SHORT).show()
    }
}