package com.sky.medialib.util

import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import com.sky.media.image.core.util.LogUtils
import com.sky.media.kit.BaseMediaApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * @author: xuzhiyong
 * @date: 2021/8/2  下午12:00
 * @Email: 18971269648@163.com
 * @description:
 */
object ToastUtils {

    val handler = Handler(Looper.getMainLooper())

    fun show(msg:String?){
        handler.post {
            val text = if(msg.isNullOrEmpty()) "msg tips" else msg
            Toast.makeText(BaseMediaApplication.sContext,text,Toast.LENGTH_SHORT).show()
        }
    }
}