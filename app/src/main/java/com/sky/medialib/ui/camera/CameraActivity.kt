package com.sky.medialib.ui.camera

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.hardware.Camera
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.blankj.utilcode.util.SPStaticUtils
import com.sky.media.image.core.util.LogUtils
import com.sky.media.image.core.view.ContainerViewHelper
import com.sky.media.kit.base.BaseActivity
import com.sky.medialib.MainActivity
import com.sky.medialib.R
import com.sky.medialib.ui.camera.process.CameraProcessExt
import com.sky.medialib.ui.dialog.SimpleAlertDialog
import com.sky.medialib.ui.kit.camera.CameraHolder
import com.sky.medialib.ui.kit.common.animate.ViewAnimator
import com.sky.medialib.util.CameraUtil
import com.sky.medialib.util.WeakHandler
import kotlinx.android.synthetic.main.camera_preview.*
import kotlinx.android.synthetic.main.camera_preview_frame.*
import kotlinx.android.synthetic.main.camera_preview_top.*

class CameraActivity : BaseActivity(),View.OnTouchListener {

    final val TAG = CameraActivity.javaClass.simpleName

    lateinit var mCameraProcess:CameraProcessExt
    var mBackCameraId = 0
    var mFrontCameraId = 0
    var mCameraId = 0
    private val mFlashMode = "off"

    protected var mCameraDevice: Camera? = null
    private var mParameters: Camera.Parameters? = null
    private var mPreviewSize: Rect? = null
    private var mOpenCameraFail = false
    private var mCameraDisabled = false
    private var mCameraState = 0
    private var mCameraType = 1
    private var mGesture: GestureDetector? = null
    private var mDisplayOrientation = 0
    private var mCurrentRatio = 0f
    val mHandler = WeakHandler(Handler.Callback {
        when(it.what){
            1 -> window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            2 -> showShootOpenAnimator()
        }
        true
    })

    private fun showShootOpenAnimator() {
        ViewAnimator.animate(shoot_motion_up)
            .translationY(0.0f, - shoot_motion_up.height.toFloat()).setDuration(400).start()
        ViewAnimator.animate(shoot_motion_down).translationY(
            0.0f,
            shoot_motion_up.height.toFloat(),
            (shoot_motion_up.height + shoot_motion_down.height).toFloat()
        ).setDuration(400).start()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_preview)
        initParams()
        bindView()
        bindListener()
        initHelper()

        processing_view.post {
           LogUtils.logd(TAG,"processing_view ${processing_view.visibility} ${processing_view.width}X${processing_view.height}")
        }
    }

    private fun bindListener() {
        camera_topbar_album.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }
    }

    private fun initParams() {
        processing_view.setOnTouchListener(this)
    }

    private fun bindView() {
        mBackCameraId = CameraHolder.getInstance().backId
        mFrontCameraId = CameraHolder.getInstance().faceId
        if (mFrontCameraId == -1) {
            this.mCameraId = SPStaticUtils.getInt("key_camera_id", mBackCameraId)
        } else {
            this.mCameraId = SPStaticUtils.getInt("key_camera_id", mFrontCameraId)
        }
        mCameraProcess = CameraProcessExt(frame,processing_view)
        mCameraOpenThread.start()

        face_view.needShowFps(true)

        try {
            mCameraOpenThread.join()
            if (mOpenCameraFail) {
                showExitDialog(R.string.cannot_connect_camera)
            } else if (mCameraDisabled) {
                showExitDialog(R.string.camera_disabled)
            } else {
                startPreview()
                mGesture = GestureDetector(
                    this,
                    GestureListener()
                )
            }
        } catch (e: Exception) {
        }

    }

    private fun startPreview() {
        var z = false
        val parameters = mCameraDevice!!.parameters
        var z2 = parameters.maxNumFocusAreas > 0 && isSupported(
            "auto",
            parameters.supportedFocusModes
        )

        if (mCameraState != 0) {
            stopPreview()
        }
        setDisplayOrientation()
        setCameraParameters()

        mCameraProcess.processCamera(mCameraDevice!!)
        mCameraState = 1
    }

    private fun setCameraParameters() {
        if (mCameraDevice != null) {
            try {
                this.mParameters = mCameraDevice!!.parameters
                val previewSize: Camera.Size = mParameters!!.getPreviewSize()
                this.mPreviewSize = Rect(0, 0, previewSize.width, previewSize.height)
                val is720: Boolean = CameraUtil.isSupport720(this.mParameters)
                val is1080: Boolean = CameraUtil.isSupport1080(this.mParameters)
                if (this.mCameraType == 1) {
                    if (is720) {
                        this.mPreviewSize = Rect(0, 0, 1280, 720)
                    }
                } else if (is1080) {
                    this.mPreviewSize = Rect(0, 0, 1920, 1080)
                } else if (is720) {
                    this.mPreviewSize = Rect(0, 0, 1280, 720)
                }
                Log.e(
                    "Camera",
                    "Size:" + mPreviewSize!!.width() + "x" + mPreviewSize!!.height()
                )
                mParameters!!.setPreviewSize(
                    this.mPreviewSize!!.width(),
                    this.mPreviewSize!!.height()
                )
                this.mParameters!!.setPictureSize(
                    this.mPreviewSize!!.width(),
                    this.mPreviewSize!!.height()
                )
                this.mParameters!!.setJpegQuality(100)

                this.mCurrentRatio =
                    this.mPreviewSize!!.height() * 1.0f / this.mPreviewSize!!.width()
                this.frame.setScaleType(ContainerViewHelper.ScaleType.CENTER_CROP)
                this.frame.setAspectRatio(this.mCurrentRatio, 0, 0)

                if (mCameraId == mBackCameraId) {
                    this.mParameters!!.setFlashMode(this.mFlashMode)
                    camera_topbar_flash.setSelected(true)
                } else {
                    camera_topbar_flash.setSelected(false)
                }

                mCameraDevice!!.parameters = this.mParameters
            } catch (e: Throwable) {
               e.printStackTrace()
            }
        }
    }

    private fun setDisplayOrientation() {
        this.mDisplayOrientation = CameraUtil.getDisplayOrientation(
            CameraUtil.getRotation(this as Activity),
            mCameraId
        )
        mCameraDevice!!.setDisplayOrientation(this.mDisplayOrientation)
    }

    private fun stopPreview() {
        if (!(mCameraDevice == null || mCameraState == 0)) {
            try {
                mCameraDevice!!.cancelAutoFocus()
            } catch (e: Exception) {
                Log.w("CameraActivity", e.message!!)
            }
        }
        mCameraState = 0
    }


    private fun showExitDialog(stringId: Int) {
        SimpleAlertDialog.newBuilder(this).setCancleable(false).setTitle(R.string.camera_error_title)
            .setMessage(stringId, 17).setRightBtn(R.string.dialog_ok
            ) { dialog, which ->
                dialog?.dismiss()
                finish()
            }.build().show()

    }

    private fun initHelper() {

    }

    override fun onResume() {

        if (mCameraState == 0) {
            openCamera()
        }
        keepScreenOnAwhile()
        mHandler.sendEmptyMessageDelayed(2, 500);
        mCameraProcess.requestRender()
        super.onResume()

    }

    override fun onPause() {

        shoot_motion_up.y = 0.0f
        shoot_motion_down.y = shoot_motion_up.height.toFloat()
        super.onPause()
    }

    private fun openCamera() {
        if (CameraHolder.canUseCamera(this as Activity)) {
            mCameraDevice = CameraHolder.openCamera(mCameraId)
            if (mCameraDevice != null) {
                startPreview()
                return
            } else {
                showExitDialog(R.string.cannot_connect_camera)
                return
            }
        }
        showExitDialog(R.string.camera_disabled)
    }

    private fun keepScreenOnAwhile() {
        this.mHandler.removeMessages(1)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        this.mHandler.sendEmptyMessageDelayed(1, 120000)
    }

    override fun onTouch(v: View, motionEvent: MotionEvent): Boolean {
        if (mCameraDevice == null || this.mCameraState == 3) {
            return false
        }
        if (this.mGesture?.onTouchEvent(motionEvent) == true) {
            return true
        }
        return false
    }

    private var mCameraOpenThread = Thread(Runnable {
        if (CameraHolder.canUseCamera(this@CameraActivity)) {
            this@CameraActivity.mCameraDevice = CameraHolder.openCamera(mCameraId)
            if (this@CameraActivity.mCameraDevice == null) {
                this@CameraActivity.mOpenCameraFail = true
                return@Runnable
            }
            return@Runnable
        }
        this@CameraActivity.mCameraDisabled = true
    })

    internal class GestureListener : SimpleOnGestureListener() {
        override fun onSingleTapUp(motionEvent: MotionEvent): Boolean {

            return true
        }
    }

    companion object{
        private fun isSupported(str: String, list: List<String>?): Boolean {
            return list != null && list.indexOf(str) >= 0
        }
    }
}