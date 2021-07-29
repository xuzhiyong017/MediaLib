package com.sky.media.kit

import android.app.Application
import android.content.Context

/**
 * @author: xuzhiyong
 * @date: 2021/7/29  上午10:13
 * @Email: 18971269648@163.com
 * @description:
 */
open class BaseMediaApplication : Application() {

    companion object{
        lateinit var sContext:Context
    }

    override fun onCreate() {
        super.onCreate()
        sContext = this
    }
}