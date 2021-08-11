package com.sky.medialib

import com.sky.media.kit.BaseMediaApplication
import com.sky.medialib.ui.kit.manager.DownControllerManager
import com.sky.medialib.util.FileUtil

/**
 * @author: xuzhiyong
 * @date: 2021/7/29  上午10:14
 * @Email: 18971269648@163.com
 * @description:
 */
class App : BaseMediaApplication() {
    override fun onCreate() {
        super.onCreate()
        FileUtil.copyMagicFilterFileFromAssetsToSDCard(this,"magics.zip")
        DownControllerManager.getInstance().initDownList()
    }
}