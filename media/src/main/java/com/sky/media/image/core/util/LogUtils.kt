package com.sky.media.image.core.util

import android.util.Log

/**
 * @author: xuzhiyong
 * @date: 2021/7/27  下午6:06
 * @Email: 18971269648@163.com
 * @description:
 */
class LogUtils {
    companion object{
        var debug = true

        @JvmStatic
        fun loge(tag:String?,msg:String){
            Log.e(tag,msg)
        }

        @JvmStatic
        fun logd(tag:String?,msg:String){
            Log.d(tag,msg)
        }

        @JvmStatic
        fun logi(tag:String?,msg:String){
            Log.i(tag,msg)
        }

        @JvmStatic
        fun logw(tag:String?,msg:String){
            Log.w(tag,msg)
        }

        @JvmStatic
        fun logv(tag:String?,msg:String){
            Log.v(tag,msg)
        }
    }
}