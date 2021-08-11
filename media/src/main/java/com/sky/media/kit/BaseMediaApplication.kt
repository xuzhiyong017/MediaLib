package com.sky.media.kit

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.sky.media.kit.base.BaseActivity
import java.util.ArrayList

/**
 * @author: xuzhiyong
 * @date: 2021/7/29  上午10:13
 * @Email: 18971269648@163.com
 * @description:
 */
open class BaseMediaApplication : Application(),Application.ActivityLifecycleCallbacks {

    companion object{
        lateinit var sContext:Context
        private val mActivityStack: ArrayList<BaseActivity> = ArrayList<BaseActivity>()

        fun getTopActivity(): BaseActivity?{
            mActivityStack.reversed().forEach {
                if(!it.isFinishing && ! it.isDestroyed){
                    return it
                }
            }
            return null
        }

        fun getAllActivity():List<BaseActivity>{
            return mActivityStack
        }

        fun finishAllActivity(){
            mActivityStack.forEach {
                if(!it.isFinishing){
                    it.finish()
                }
            }
            mActivityStack.clear()
        }
    }

    override fun onCreate() {
        super.onCreate()
        sContext = this
        registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        mActivityStack.add(activity as BaseActivity)
    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        mActivityStack.remove(activity)
    }
}