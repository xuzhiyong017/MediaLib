package com.sky.medialib.ui.kit.common.base

import com.sky.media.kit.base.BaseActivity
import com.sky.medialib.ui.dialog.LoadingDialog

/**
 * @author: xuzhiyong
 * @date: 2021/8/10  下午6:06
 * @Email: 18971269648@163.com
 * @description:
 */
open abstract class AppActivity : BaseActivity(){

    private var mProgressDialog: LoadingDialog? = null

    open fun showProgressDialog(i: Int) {
        showProgressDialog(getString(i))
    }

    open fun showProgressDialog(i: Int, z: Boolean) {
        showProgressDialog(getString(i), z)
    }

    open fun showProgressDialog(str: String?) {
        showProgressDialog(str, false)
    }

    open fun showProgressDialog(str: String?, z: Boolean) {
        if (canShowDialog()) {
            this.mProgressDialog = LoadingDialog(this)
            this.mProgressDialog?.setCanceledOnTouchOutside(false)
            this.mProgressDialog?.setCancelable(z)
            this.mProgressDialog?.setMessage(str)
            this.mProgressDialog?.show()
        }
    }

    protected open fun canShowDialog(): Boolean {
        return !(isDestroyed || isFinishing)
    }

    open fun dismissProgressDialog() {
        if (this.mProgressDialog != null && this.mProgressDialog?.isShowing == true) {
            this.mProgressDialog?.dismiss()
        }
        this.mProgressDialog = null
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}