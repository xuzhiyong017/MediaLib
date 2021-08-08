package com.sky.medialib.ui.camera

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.hardware.Camera
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.widget.AdapterView
import com.blankj.utilcode.util.SPStaticUtils
import com.sky.media.image.core.cache.ImageBitmapCache
import com.sky.media.image.core.view.ContainerViewHelper
import com.sky.media.kit.base.BaseActivity
import com.sky.medialib.MainActivity
import com.sky.medialib.PICK_PICTURE
import com.sky.medialib.PictureEditActivity
import com.sky.medialib.R
import com.sky.medialib.ui.camera.adapter.CameraTypeAdapter
import com.sky.medialib.ui.camera.helper.CameraFilterBeautyHelper
import com.sky.medialib.ui.camera.helper.CameraZoomHelper
import com.sky.medialib.ui.camera.process.CameraProcessExt
import com.sky.medialib.ui.dialog.SimpleAlertDialog
import com.sky.medialib.ui.kit.camera.CameraHolder
import com.sky.medialib.ui.kit.common.animate.ViewAnimator
import com.sky.medialib.ui.kit.manager.FocusManager
import com.sky.medialib.ui.kit.manager.ToolFilterManager
import com.sky.medialib.ui.kit.view.ShutterView
import com.sky.medialib.ui.kit.view.camera.RecordProgressView
import com.sky.medialib.util.*
import kotlinx.android.synthetic.main.camera_focus_indicator.*
import kotlinx.android.synthetic.main.camera_preview.*
import kotlinx.android.synthetic.main.camera_preview_frame.*
import kotlinx.android.synthetic.main.camera_preview_right.*
import kotlinx.android.synthetic.main.camera_preview_top.*
import java.io.File
import java.util.ArrayList

class CameraActivity : BaseActivity(),View.OnTouchListener,FocusManager.OnFocusListener,
    View.OnClickListener,RecordProgressView.OnRecordListener {


    final val TAG = CameraActivity.javaClass.simpleName

    lateinit var mCameraProcess:CameraProcessExt
    var mBackCameraId = 0
    var mFrontCameraId = 0
    var mCameraId = 0
    private var mFlashMode = "off"
    private var mFlashIndex = 0
    private val mFlashIcon = intArrayOf(
        R.drawable.camera_topbar_noflash_selector_41,
        R.drawable.camera_topbar_flash_selector_41
    )


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
    private var mOrientation = -1
    private lateinit var mFocusManager:FocusManager
    private var mFocusAreaSupported = false
    private var mMeteringAreaSupported = false

    private lateinit var mCameraZoomHelper: CameraZoomHelper
    private lateinit var mCameraFilterBeautyHelper: CameraFilterBeautyHelper

    private val mTempVideoPaths: ArrayList<String> = ArrayList<String>()
    private var mJumpFilterId: Int = 0

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
        ImageBitmapCache.getInstance().clear()
        ToolFilterManager.initCameraFilter(this)
        setContentView(R.layout.camera_preview)
        initParams()
        bindView()
        initOperate()
    }

    private fun initOperate() {
        window.decorView.post{
            mCameraFilterBeautyHelper.onCreateAndInitFilter(0, 0, false)
        }
    }

    private fun initParams() {
        processing_view.setOnTouchListener(this)
        filter_mask.setOnClickListener {
            hideBeautyFilterLayout()
        }

        camera_list_show_mask.setOnClickListener {
            hideDynamicStickers()
        }
    }

    private fun bindView() {
        mBackCameraId = CameraHolder.getInstance().backCameraId
        mFrontCameraId = CameraHolder.getInstance().faceCameraId
        mCameraId = SPStaticUtils.getInt("key_camera_id",mFrontCameraId)

        mFocusManager = FocusManager("continuous-picture")
        mCameraProcess = CameraProcessExt(frame,processing_view)
        mCameraOpenThread.start()
        face_view.needShowFps(true)
        bindTopView()
        bindBottomView()
        initHelper()

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
                var z = true
                if (CameraHolder.getInstance().cameraInfos[mCameraId].facing !== 1) {
                    z = false
                }
                mFocusManager.init(
                    focus_indicator_rotate_layout, processing_view, this, z,
                    mDisplayOrientation
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun bindBottomView() {
        camera_type_tab.adapter = CameraTypeAdapter(this)
        camera_type_tab.setSelection(mCameraType)
        camera_type_tab.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var i2 = View.VISIBLE
                mCameraType = position
                shutter_button.setEnableLongPress(mCameraType != 0)
                if (mCameraType == 0) {
                    i2 = View.GONE
                }
                camera_rightbar.visibility = i2
                mHandler.removeCallbacks(mUpdatePreviewSize)
                mHandler.postDelayed(mUpdatePreviewSize, 500)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        camera_bottombar_beauty.setOnClickListener(this)
        camera_bottombar_sticker.setOnClickListener(this)
        camera_bottombar_rollback.setOnClickListener(this)
        camera_bottombar_next.setOnClickListener(this)
        shutter_button.setShutterClickListener(object : ShutterView.OnShutterClickListener {
            override fun onCapture() {
                takePicture()
            }

            override fun onStart() {
                TODO("Not yet implemented")
            }

            override fun onPause() {
                TODO("Not yet implemented")
            }
        })
    }

    private fun takePicture() {
        if (mCameraType == 0) {
//            this.mEffectHelper.mo16841e()
            takeSnap()
        }
//        else if (this.mVideoRecorder != null && this.mVideoRecorder.isRecording()) {
//            this.mRecordProgressView.pauseRecord()
//            cancelCountDown()
//        } else if (this.mRecordProgressView.getState() === State.CONCAT) {
//            concatVideo()
//        } else {
//            this.mShutterView.setSelected(true)
//            this.mRecordProgressView.startRecording()
//        }
    }

    private fun takeSnap() {
        if (mCameraState != 3) {
            showShootCloseAnimator(Runnable { mFocusManager.takePhoto() })
        }
    }

    private fun showShootCloseAnimator(runnable: Runnable?) {
        runnable?.run()
    }

    private fun bindTopView() {
        record_progress.setRecordListener(this)
        mFlashMode = "off"
        if (mFlashMode == "on" || mFlashMode == "torch") {
            this.mFlashIndex = 1
        } else if (mFlashMode == "off") {
            this.mFlashIndex = 0
        }
        camera_topbar_flash.setImageResource(mFlashIcon[mFlashIndex])
        camera_topbar_cancel.setOnClickListener(this)
        camera_topbar_facing.setOnClickListener(this)
        camera_topbar_flash.setOnClickListener(this)
        camera_topbar_album.setOnClickListener(this)
    }

    private fun startPreview() {
        var isFrontCamera = false
        val parameters = mCameraDevice!!.parameters
        var z2 =
            parameters.maxNumFocusAreas > 0 && isSupported("auto", parameters.supportedFocusModes)
        mFocusAreaSupported = z2
        z2 = parameters.maxNumMeteringAreas > 0
        mMeteringAreaSupported = z2
        val focusManager = mFocusManager
        if (mCameraId == mFrontCameraId) {
            isFrontCamera = true
        }
        focusManager.setParameters(parameters, isFrontCamera)
        mFocusManager.resetLayout()

        if (mCameraState != 0) {
            stopPreview()
        }
        setDisplayOrientation()
        setCameraParameters()
        if ("continuous-picture" == mParameters!!.focusMode) {
            try {
                mCameraDevice!!.cancelAutoFocus()
            } catch (e: java.lang.Exception) {
                Log.w("CameraActivity", e.message!!)
            }
        }
        mCameraProcess.processCamera(mCameraDevice!!)
        mCameraFilterBeautyHelper.initBeautyFilter();
        mCameraState = 1
        mFocusManager.initStatus()
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
                Log.e("Camera", "Size:" + mPreviewSize!!.width() + "x" + mPreviewSize!!.height())
                mParameters!!.setPreviewSize(mPreviewSize!!.width(), mPreviewSize!!.height())
                this.mParameters!!.setPictureSize(mPreviewSize!!.width(), mPreviewSize!!.height())
                this.mParameters!!.setJpegQuality(100)
                if (mFocusAreaSupported) {
                    mParameters!!.focusAreas = mFocusManager.focusArea
                }
                if (mMeteringAreaSupported) {
                    mParameters!!.meteringAreas = mFocusManager.meteringArea
                }
                this.mCurrentRatio =
                    this.mPreviewSize!!.height() * 1.0f / this.mPreviewSize!!.width()
                this.frame.setScaleType(ContainerViewHelper.ScaleType.CENTER_CROP)
                this.frame.setAspectRatio(this.mCurrentRatio, 0, 0)
                var sceneMode = mParameters!!.sceneMode
                if (sceneMode == null) {
                    sceneMode = "auto"
                }
                if (mCameraId == mBackCameraId) {
                    this.mParameters!!.setFlashMode(this.mFlashMode)
                    camera_topbar_flash.setSelected(true)
                } else {
                    camera_topbar_flash.setSelected(false)
                }
                if ("auto" == sceneMode) {
                    mFocusManager.setForceFocusMode(null)
                    mParameters!!.focusMode = mFocusManager.supportFocusMode
                } else {
                    mFocusManager.setForceFocusMode(mParameters!!.focusMode)
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
        mFocusManager.release()
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
        mCameraZoomHelper = CameraZoomHelper(this)
        mCameraFilterBeautyHelper = CameraFilterBeautyHelper(this, object : CameraFilterBeautyHelper.OnFilterBeautyListener {
            override fun getCameraProcess(): CameraProcessExt {
                return mCameraProcess
            }

            override fun onSelectFilterId(i: Int) {
                mJumpFilterId = i
            }
        })
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
        onPauseStopRecord()
        stopPreview()
        closeCamera()
        resetScreenOn()
        shoot_motion_up.y = 0.0f
        shoot_motion_down.y = shoot_motion_up.height.toFloat()
        mFocusManager.cancelFocus()
        super.onPause()
    }

    private fun onPauseStopRecord() {
//        if (this.mVideoRecorder != null && this.mVideoRecorder.isRecording()) {
//            this.mRecordProgressView.pauseRecord()
//            mCameraProcess.stopRecord()
//        }
        hideDynamicStickers()
        hideBeautyFilterLayout()
//        hideCountDownView()
//        hideShootBar()
    }

    private fun hideDynamicStickers(): Boolean {
        if (camera_dynamic_stick_rv.visibility != View.VISIBLE) {
            return false
        }
        showBottomView()
        camera_list_show_mask.visibility = View.GONE
        camera_dynamic_stick_rv.visibility = View.GONE
        return true
    }

    private fun hideBeautyFilterLayout(): Boolean {
        if (!mCameraFilterBeautyHelper.isShown) {
            return false
        }
        showBottomView()
        filter_mask.visibility = View.GONE
        mCameraFilterBeautyHelper.showBeautyView(false)
        return true
    }

    private fun resetScreenOn() {
        mHandler.removeMessages(1)
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun closeCamera() {
        if (mCameraDevice != null) {
            mCameraState = 0
            CameraHolder.getInstance().stopPreview()
            mCameraDevice!!.setZoomChangeListener(null)
            mCameraDevice!!.setFaceDetectionListener(null)
            mCameraDevice!!.setErrorCallback(null)
            mCameraDevice = null
            mFocusManager.clear()
        }
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
        mCameraFilterBeautyHelper.onTouch(motionEvent)
        mCameraZoomHelper.onTouch(motionEvent, mCameraDevice, mParameters)
        return when (motionEvent.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                mFocusManager.onTouch(motionEvent)
                true
            }
            MotionEvent.ACTION_UP -> {
                if (mCameraZoomHelper.status === 1) {
                    return true
                }
                if((mFocusAreaSupported || mMeteringAreaSupported) && mFocusManager.onTouch(motionEvent)){
                    return true
                }
                return false
            }
            else -> true
        }
        return false
    }

    override fun autoFocus() {
        try {
            mCameraDevice!!.autoFocus{success,_ ->
                mFocusManager.onAutoFocus(success)
            }
        } catch (e: java.lang.Exception) {
            Log.w("CameraActivity", e.message!!)
        }
        mCameraState = 2
    }

    override fun cancelAutoFocus() {
        try {
            mCameraDevice!!.cancelAutoFocus()
        } catch (e: java.lang.Exception) {
            Log.w("CameraActivity", e.message!!)
        }
        mCameraState = 1
        setCameraParameters()
    }

    override fun capture(): Boolean {
        var z = false
        if (mCameraState == 3 || mCameraDevice == null) {
            return false
        }

        CameraHolder.adjustRotation(mParameters, mCameraId, this.mOrientation)
        mCameraFilterBeautyHelper.takePhoto(mCameraDevice, mParameters, mFlashMode, mOrientation, mCurrentRatio, z){bitmap,bitmap2,orientation ->
            val fromFile = Uri.fromFile(File(Storage.storageToTempJpgPath(bitmap, null, orientation)))
            bitmap.recycle()
            if (FileUtil.checkUriValid(this, fromFile)) {
                startActivity(Intent(this,PictureEditActivity::class.java).putExtra(PICK_PICTURE,fromFile))
            }else{
                mCameraState = 1
                ToastUtils.show("图片打开失败")
            }
        }
        mCameraState = 3
        return true
    }

    override fun setFocusParameters() {
        setCameraParameters()
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when(v.id){
                R.id.camera_topbar_cancel -> {
                    if(mTempVideoPaths.isNotEmpty()){
                        showConfirmDialog()
                    }else{
                        finish()
                    }
                }
                R.id.camera_topbar_facing -> {
                    if (CameraHolder.getInstance().numberOfCameras >= 2) {
                        changeCamera()
                    }
                }
                R.id.camera_topbar_flash -> {
                    if (mParameters != null) {
                        val supportedFlashModes: List<String>? = mParameters!!.supportedFlashModes
                        var str = "off"
                        if (mFlashMode == "on" || mFlashMode == "torch") {
                            str = "off"
                            mFlashIndex = 0
                        } else if (mFlashMode == "off") {
                            str = "on"
                            mFlashIndex = 1
                        }
                        if (isSupported(str, supportedFlashModes)) {
                            camera_topbar_flash.setImageResource(mFlashIcon[mFlashIndex])
                            mFlashMode = str!!
                            mParameters!!.flashMode = mFlashMode
                            try {
                                mCameraDevice!!.parameters = mParameters
                            } catch (e: Throwable) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
                R.id.camera_topbar_album -> {
                    startActivity(Intent(this,MainActivity::class.java))
                }
                R.id.camera_bottombar_beauty -> {
                    showBeautyFilterLayout()
                }
            }
        }
    }

    private fun showBeautyFilterLayout() {
        if (!mCameraFilterBeautyHelper.isShown) {
            hideBottomView()
            filter_mask.visibility = View.VISIBLE
            mCameraFilterBeautyHelper.showBeautyView(true)
        }
    }

    private fun hideBottomView() {
        updateViewRecording(true)
    }

    private fun updateViewRecording(z: Boolean) {
        if (z) {
            camera_bottombar_sticker.visibility = View.GONE
            camera_bottombar_beauty.visibility = View.GONE
            shutter_button.visibility = View.GONE
        } else {
            camera_bottombar_sticker.visibility = View.VISIBLE
            camera_bottombar_beauty.visibility = View.VISIBLE
            shutter_button.visibility = View.VISIBLE
        }
        if (mTempVideoPaths.size <= 0 || z) {
            hideRecordBottomBar()
            if (z) {
                hideCameraTypeTab()
                return
            } else {
                showCameraTypeTab()
                return
            }
        }
        showRecordBottomBar()
        hideCameraTypeTab()
    }

    private fun showRecordBottomBar() {
        camera_bottombar_next.visibility = View.VISIBLE
        camera_bottombar_rollback.visibility = View.VISIBLE
    }

    private fun hideRecordBottomBar() {
        camera_bottombar_next.visibility = View.GONE
        camera_bottombar_rollback.visibility = View.GONE
    }

    private fun showCameraTypeTab() {
        camera_type_tab.visibility = View.VISIBLE
        foot_icon.visibility = View.VISIBLE
    }

    private fun hideCameraTypeTab() {
        camera_type_tab.visibility = View.GONE
        foot_icon.visibility = View.GONE
    }

    private fun showBottomView() {
        updateViewRecording(false)
    }

    private fun changeCamera() {
        stopPreview()
        closeCamera()
        mCameraId = if (mCameraId == mFrontCameraId) mBackCameraId else mFrontCameraId
        SPStaticUtils.put("key_camera_id", mCameraId)
        openCamera()
    }

    private fun showConfirmDialog() {
        finish()
    }

    override fun disableVideo() {
        TODO("Not yet implemented")
    }

    override fun onIsRecordMin(z: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onRecordEnd() {
        TODO("Not yet implemented")
    }

    override fun onRecordIdle() {
        TODO("Not yet implemented")
    }

    override fun onRecordPause() {
        TODO("Not yet implemented")
    }

    override fun onRecordStart() {
        TODO("Not yet implemented")
    }

    override fun onKeyDown(i: Int, keyEvent: KeyEvent?): Boolean {
        if (i == 4) {
            if (Util.isNotEmptyList(mTempVideoPaths)) {
                showConfirmDialog()
                return true
            }
            val z = hideDynamicStickers() || hideBeautyFilterLayout()
            if (z) {
                return true
            }
        }
        return super.onKeyDown(i, keyEvent)
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

    private val mUpdatePreviewSize = Runnable {
        setCameraParameters()
        if (mPreviewSize != null) {
            mCameraProcess.updateInputRenderSize(mPreviewSize!!.height(), mPreviewSize!!.width())
        }
    }


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