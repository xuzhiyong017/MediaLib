package com.sky.medialib.ui.camera

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Rect
import android.hardware.Camera
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.widget.AdapterView
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.SPStaticUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.sky.media.image.core.cache.ImageBitmapCache
import com.sky.media.image.core.out.VideoFrameOutput
import com.sky.media.image.core.util.LogUtils
import com.sky.media.image.core.view.ContainerViewHelper
import com.sky.media.kit.record.IVideoRecorder
import com.sky.media.kit.record.RecordListener
import com.sky.media.kit.record.VideoRecorderCreator
import com.sky.medialib.MainActivity
import com.sky.medialib.PICK_PICTURE
import com.sky.medialib.PictureEditActivity
import com.sky.medialib.R
import com.sky.medialib.ui.camera.adapter.CameraTypeAdapter
import com.sky.medialib.ui.camera.helper.CameraAudioHelper
import com.sky.medialib.ui.camera.helper.CameraFilterBeautyHelper
import com.sky.medialib.ui.camera.helper.CameraZoomHelper
import com.sky.medialib.ui.camera.process.CameraProcessExt
import com.sky.medialib.ui.dialog.BottomSheetDialog
import com.sky.medialib.ui.dialog.SimpleAlertDialog
import com.sky.medialib.ui.editvideo.VIDEO_PATH
import com.sky.medialib.ui.editvideo.VideoEditActivity
import com.sky.medialib.ui.kit.camera.CameraHolder
import com.sky.medialib.ui.kit.common.animate.AnimationListener
import com.sky.medialib.ui.kit.common.animate.ViewAnimator
import com.sky.medialib.ui.kit.common.base.AppActivity
import com.sky.medialib.ui.kit.common.network.RxUtil
import com.sky.medialib.ui.kit.common.view.NavigationTabStrip
import com.sky.medialib.ui.kit.effect.Effect
import com.sky.medialib.ui.kit.manager.FocusManager
import com.sky.medialib.ui.kit.manager.ToolFilterManager
import com.sky.medialib.ui.kit.media.MediaKitExt
import com.sky.medialib.ui.kit.view.ShutterView
import com.sky.medialib.ui.kit.view.TimeCountDownView
import com.sky.medialib.ui.kit.view.camera.RecordProgressView
import com.sky.medialib.ui.music.MusicChooseActivity
import com.sky.medialib.ui.music.event.CutMusicInfoEvent
import com.sky.medialib.util.*
import com.weibo.soundtouch.SoundTouch
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.camera_focus_indicator.*
import kotlinx.android.synthetic.main.camera_preview.*
import kotlinx.android.synthetic.main.camera_preview_frame.*
import kotlinx.android.synthetic.main.camera_preview_right.*
import kotlinx.android.synthetic.main.camera_preview_top.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import java.io.File
import java.util.ArrayList
import java.util.HashMap

class CameraActivity : AppActivity(),View.OnTouchListener,FocusManager.OnFocusListener,
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
    private lateinit var mMusicHelper: CameraAudioHelper

    private val mTempVideoPaths: ArrayList<String> = ArrayList<String>()
    private val mLastVideoPath: ArrayList<String?> = ArrayList<String?>()
    private val mShootTypeMap: HashMap<String, Int> = HashMap<String, Int>()
    private var mShootType = 3

    private var mJumpFilterId: Int = 0
    private var mFinalVideoPath: String? = null
    private var mAudioPath: String? = null
    private var mVideoRecorder: IVideoRecorder? = null
    private val mVoice: Effect.Voice? = null
    private var mClickNext = false

    private var mAnimator: ViewAnimator? = null
    private var mBottomSheet: BottomSheetDialog? = null

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
        EventBusHelper.register(this)
        mShootTypeMap.clear()
        mTempVideoPaths.clear()

        processing_view.setOnTouchListener(this)
        filter_mask.setOnClickListener {
            hideBeautyFilterLayout()
        }

        camera_list_show_mask.setOnClickListener {
            hideDynamicStickers()
        }

        shoot_tab_mask.setOnClickListener {
            hideShootBar()
        }

        camera_rightbar_speed.setOnClickListener(this)
        camera_rightbar_time_count.setOnClickListener(this)
        camera_rightbar_music.setOnClickListener(this)
        camera_bottombar_rollback.setOnClickListener(this)
        camera_bottombar_next.setOnClickListener(this)

        camera_time_count.setCountDownTimeListener(object : TimeCountDownView.CountDownListener {
            override fun onFinish() {
                camera_time_count.doJob()
                if(record_progress.state == RecordProgressView.State.CONCAT){
                    concatVideo()
                }else{
                    shutter_button.isSelected = true
                    record_progress.startRecording()
                }
            }

            override fun start() {
                hideCountDownView()
            }
        })
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
        bindRightView()
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

    private fun bindRightView() {
        shoot_tab.tabIndex = mShootType -1
        shoot_tab.onTabStripSelectedIndexListener = object : NavigationTabStrip.OnTabStripSelectedIndexListener {
            override fun onStartTabSelected(title: String?, index: Int) {

            }

            override fun onEndTabSelected(title: String?, index: Int) {
                mShootType = index + 1
                mMusicHelper.setShootType(mShootType)
                camera_rightbar_speed.isSelected = mShootType != 3
                camera_rightbar_speed.postDelayed({
                    toggleShootBar()
                }, 200)
                val a = 1.0f / MediaKitExt.getSpeedByType(mShootType)
                record_progress.setSpeed(a)
                mMusicHelper.mediaPlayer?.setSpeed(a)
            }
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
                if(mCameraType == 1){
                    record_progress.setLongPressed(true)
                    record_progress.startRecording()
                }
            }

            override fun onPause() {
                if(mCameraType == 1){
                    record_progress.setLongPressed(false)
                    record_progress.pauseRecord()
                }
            }
        })
    }

    private fun takePicture() {
        if (mCameraType == 0) {
//            this.mEffectHelper.mo16841e()
            takeSnap()
        }
        else if (mVideoRecorder != null && mVideoRecorder!!.isRecording) {
           record_progress.pauseRecord()
            cancelCountDown()
        } else if (record_progress.state == RecordProgressView.State.CONCAT) {
            concatVideo()
        } else {
            shutter_button.isSelected = true
            record_progress.startRecording()
        }
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
            mFlashIndex = 1
        } else if (mFlashMode == "off") {
            mFlashIndex = 0
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
                mParameters = mCameraDevice!!.parameters
                val previewSize: Camera.Size = mParameters!!.getPreviewSize()
                mPreviewSize = Rect(0, 0, previewSize.width, previewSize.height)
                val is720: Boolean = CameraUtil.isSupport720(mParameters)
                val is1080: Boolean = CameraUtil.isSupport1080(mParameters)
                if (mCameraType == 1) {
                    if (is720) {
                        mPreviewSize = Rect(0, 0, 1280, 720)
                    }
                } else if (is1080) {
                    mPreviewSize = Rect(0, 0, 1920, 1080)
                } else if (is720) {
                    mPreviewSize = Rect(0, 0, 1280, 720)
                }
                Log.e("Camera", "Size:" + mPreviewSize!!.width() + "x" + mPreviewSize!!.height())
                mParameters!!.setPreviewSize(mPreviewSize!!.width(), mPreviewSize!!.height())
                mParameters!!.setPictureSize(mPreviewSize!!.width(), mPreviewSize!!.height())
                mParameters!!.setJpegQuality(100)
                if (mFocusAreaSupported) {
                    mParameters!!.focusAreas = mFocusManager.focusArea
                }
                if (mMeteringAreaSupported) {
                    mParameters!!.meteringAreas = mFocusManager.meteringArea
                }
                mCurrentRatio =
                    mPreviewSize!!.height() * 1.0f / mPreviewSize!!.width()
                frame.setScaleType(ContainerViewHelper.ScaleType.CENTER_CROP)
                frame.setAspectRatio(mCurrentRatio, 0, 0)
                var sceneMode = mParameters!!.sceneMode
                if (sceneMode == null) {
                    sceneMode = "auto"
                }
                if (mCameraId == mBackCameraId) {
                    mParameters!!.setFlashMode(mFlashMode)
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
                mCameraDevice!!.parameters = mParameters
            } catch (e: Throwable) {
               e.printStackTrace()
            }
        }
    }

    private fun setDisplayOrientation() {
        mDisplayOrientation = CameraUtil.getDisplayOrientation(
            CameraUtil.getRotation(this as Activity),
            mCameraId
        )
        mCameraDevice!!.setDisplayOrientation(mDisplayOrientation)
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
        mMusicHelper = CameraAudioHelper()
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

    override fun onStop() {
        super.onStop()
        mMusicHelper.release()
    }

    private fun onPauseStopRecord() {
        if (mVideoRecorder != null && mVideoRecorder?.isRecording == true) {
            record_progress.pauseRecord()
            mCameraProcess.stopRecord()
        }
        hideDynamicStickers()
        hideBeautyFilterLayout()
        hideCountDownView()
        hideShootBar()
    }

    private fun toggleShootBar() {
        if (shoot_tab.visibility == View.VISIBLE) {
            hideShootBar()
        } else {
            showShootBar()
        }
    }

    private fun showShootBar() {
        if (mAnimator != null) {
            mAnimator!!.cancel()
            mAnimator = null
        }
        mAnimator =
            ViewAnimator.animate(shoot_tab).translationX(300.0f, 0.0f).alpha(0.0f, 1.0f)
                .setDuration(400).setOnStartListener(object : AnimationListener.OnStartListener {
                    override fun onStart() {
                        shoot_tab.visibility = View.VISIBLE
                    }
                }).interplolatorDecelerate().start()
        shoot_tab_mask.visibility = View.VISIBLE
        hideBottomView()
    }
    private fun hideShootBar() {
        if (mAnimator != null) {
            mAnimator!!.cancel()
            mAnimator = null
        }
        mAnimator =
            ViewAnimator.animate(shoot_tab).translationX(0.0f, 300.0f).alpha(1.0f, 0.0f)
                .setDuration(400).interplolatorDecelerate()
                .setOnEndListener(object : AnimationListener.OnEndListener {
                    override fun onEnd() {
                        shoot_tab.visibility = View.GONE
                    }
                }).start()
        shoot_tab_mask.visibility = View.GONE
        showBottomView()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBusHelper.unregister(this)
        mShootTypeMap.clear()
        mTempVideoPaths.clear()
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
        mHandler.removeMessages(1)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        mHandler.sendEmptyMessageDelayed(1, 120000)
    }

    override fun onTouch(v: View, motionEvent: MotionEvent): Boolean {
        if (mCameraDevice == null || mCameraState == 3) {
            return false
        }
        if (mGesture?.onTouchEvent(motionEvent) == true) {
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
                if (mCameraZoomHelper.status == 1) {
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

        CameraHolder.adjustRotation(mParameters, mCameraId, mOrientation)
        mCameraFilterBeautyHelper.takePhoto(mCameraDevice, mParameters, mFlashMode, mOrientation, mCurrentRatio, z){bitmap,bitmap2,orientation ->
            val fromFile = Uri.fromFile(File(Storage.storageToTempJpgPath(bitmap, null, orientation)))
            bitmap.recycle()
            if (FileUtil.checkUriValid(this, fromFile)) {
                startActivity(Intent(this,PictureEditActivity::class.java).putExtra(PICK_PICTURE,fromFile))
            }else{
                mCameraState = 1
                ToastUtils.showToast("??????????????????")
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
                R.id.camera_rightbar_speed -> {
                    toggleShootBar()
                }
                R.id.camera_rightbar_time_count -> {
                    showCountDownView()
                }
                R.id.camera_rightbar_music -> {
                    if (mTempVideoPaths != null && mTempVideoPaths.size == 0) {
                        if (TextUtils.isEmpty(mAudioPath)) {
//                            this.mEffectHelper.mo16841e()
                            val intent = Intent(this, MusicChooseActivity::class.java)
                            intent.putExtra("key_is_from_camera", true)
                            startActivity(intent)
                        } else {
                            BottomSheetDialog(this).addList(
                                getString(R.string.change_music),
                                getString(R.string.cancel_music)
                            ).setOnItemClickListener { parent, view, position, id ->
                                when(position){
                                    0 -> {
                                        val intent = Intent(this, MusicChooseActivity::class.java)
                                        intent.putExtra("key_is_from_camera", true)
                                        startActivity(intent)
                                    }
                                    1 -> {
                                        mAudioPath = ""
                                        mMusicHelper.setMusicPath(mAudioPath)
                                        mMusicHelper.music = null
                                        mMusicHelper.release()
                                        changeMusicBtnState()
                                        camera_rightbar_music_cover.setImageDrawable(null)
                                    }
                                    else ->{ }
                                }
                            }
                                .show()
                        }
                    }
                }
                R.id.camera_bottombar_rollback -> {
                    SimpleAlertDialog.newBuilder(this).setMessage("????????????????????????????", 17)
                        .setLeftBtn(R.string.cancel).setRightBtn("??????", object : DialogInterface.OnClickListener {
                            override fun onClick(dialogInterface: DialogInterface, which: Int) {
                                dialogInterface.dismiss()
                                record_progress.removeLastSegment()
                                mMusicHelper.seekTo(record_progress.duration)
                                camera_bottombar_next.setImageResource(if (record_progress.canSave()) R.drawable.selector_camera_next else R.drawable.shoot_button_next_disabled)
                                val size: Int = mTempVideoPaths.size
                                if (size > 0) {
                                    mTempVideoPaths.removeAt(size - 1)
                                }
                                if (mTempVideoPaths.size == 0) {
                                    shutter_button.setRecordingIdle()
                                    camera_type_tab.visibility = View.VISIBLE
                                    foot_icon.visibility = View.VISIBLE
                                }
                                changeMusicBtnState()
                            }
                        })
                        .setCancleable(false).build().show()
                }
                R.id.camera_bottombar_next -> {
                    if (mVideoRecorder == null || !mVideoRecorder!!.isRecording) {
                        next()
                        return
                    }
                    record_progress.pauseRecord()
                    cancelCountDown()
                    mClickNext = true
                }
            }
        }
    }

    private operator fun next() {
        if (!record_progress.canSave()) {
            ToastUtils.showToast("???????????????3??????~")
        } else if (mTempVideoPaths.size > 0) {
            concatVideo()
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
            } else {
                showCameraTypeTab()
            }
        }else{
            showRecordBottomBar()
            hideCameraTypeTab()
        }

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
        if (mBottomSheet == null || !mBottomSheet!!.isShowing) {
            val arrayList: MutableList<BottomSheetDialog.ItemBean> = ArrayList<BottomSheetDialog.ItemBean>()
            arrayList.add(BottomSheetDialog.ItemBean(getString(R.string.exit_shoot)))
            arrayList.add(BottomSheetDialog.ItemBean(getString(R.string.re_shoot)))
            mBottomSheet = BottomSheetDialog(this)
            mBottomSheet!!.replaceList(arrayList).setOnItemClickListener { _, _, position, _ ->
               when(position){
                   0 -> finish()
                   1 -> {
                       mTempVideoPaths.clear()
                       mShootTypeMap.clear()
                       shutter_button.setRecordingIdle()
                       mFinalVideoPath = null
                       record_progress.reset()
                       showBottomView()
                       changeMusicBtnState()
                   }
               }
            }
            mBottomSheet!!.show()
        }
    }

    override fun disableVideo() {
        ToastUtils.showToast("????????????????????????!")
        record_progress.removeLastSegment()
        camera_bottombar_next.setImageResource(if (record_progress.canSave()) R.drawable.selector_camera_next else R.drawable.shoot_button_next_disabled)
        val size = mTempVideoPaths.size
        if (size > 0) {
            mTempVideoPaths.removeAt(size - 1)
        }
    }

    override fun onIsRecordMin(z: Boolean) {
        if (z) {
            if (mVideoRecorder != null && mVideoRecorder!!.isRecording) {
                camera_bottombar_next.visibility = View.VISIBLE
            }
            camera_bottombar_next.setImageResource(R.drawable.selector_camera_next)
        }else{
            camera_bottombar_next.setImageResource(R.drawable.shoot_button_next_disabled)
        }
    }

    override fun onRecordEnd() {
        onRecordPause()
    }

    override fun onRecordIdle() {
        mTempVideoPaths.clear()
        mShootTypeMap.clear()
        showBottomView()
    }

    override fun onRecordPause() {
        if (mVideoRecorder != null && mVideoRecorder!!.isRecording) {
            mVideoRecorder!!.recordStop()
            mVideoRecorder = null
        }
        mCameraProcess.stopRecord()
        if (TextUtils.equals(mFlashMode, "on") || TextUtils.equals(mFlashMode, "torch")) {
            mParameters!!.flashMode = "on"
            try {
                mCameraDevice!!.parameters = mParameters
            } catch (e:Exception) {
                LogUtils.loge("CameraActivity", ""+ e.message)
            }
        }
    }

    override fun onRecordStart() {
        if (record_progress.isMaxDurtion(0.0f) || mPreviewSize == null) {
            jumpVideoEditPage(mFinalVideoPath)
            return
        }

        if (mFlashMode == "on") {
            mParameters!!.flashMode = "torch"
            try {
                mCameraDevice!!.parameters = mParameters
            } catch (e: Exception) {
                e.message?.let { LogUtils.loge("CameraActivity", it) }
            }
        }

        val createVideoPath: String = createVideoPath()
        val height = mPreviewSize!!.height()
        val width = mPreviewSize!!.width()
        mVideoRecorder = if (mVoice == null || mVoice.pitch === 0.0f && mVoice.tempo === 0.0f) {
            VideoRecorderCreator.buildRecorder(this, height, width, createVideoPath, null)
        } else {
            val a = SoundTouch.getInstance()
            a.setPitch(mVoice.pitch)
            a.setTempo(mVoice.tempo)
            VideoRecorderCreator.buildRecorder(this, height, width, createVideoPath, a)
        }
        mVideoRecorder?.enableAudio(TextUtils.isEmpty(mAudioPath))
        mVideoRecorder?.setRecordListener(object : RecordListener{
            override fun onRecordStart() {
                mMusicHelper.startPlayer()
            }

            override fun onRecordSuccess(success: Boolean) {
                if(success){
                    mTempVideoPaths.add(createVideoPath)
                    mShootTypeMap[createVideoPath] = mShootType
                    changeMusicBtnState()
                }else{
                    ToastUtils.showToast("??????????????????")
                }

                cancelCountDown()
                showAllView()
                if (record_progress.getState() === RecordProgressView.State.CONCAT || mClickNext) {
                    concatVideo()
                    mClickNext = false
                }
            }

            override fun onRecordStop() {
                mMusicHelper.pause()
            }
        })
        if (mVideoRecorder!!.prepared()) {
            mCameraProcess.setVideoFrameOutput(object : VideoFrameOutput.VideoFrameOutputCallback {
                override fun videoFrameOutput(bArr: ByteArray?) {
                    mVideoRecorder?.onEncodeData(bArr)
                }
            }, height, width)
            mCameraProcess.startRecord()
            hideAllView()
            shutter_button.visibility = View.VISIBLE
            return
        }else{
            ToastUtils.showToast("?????????????????????")
            shutter_button.isSelected = false
            record_progress.onPause()
        }

    }

    private fun changeMusicBtnState() {
        if (Util.isNotEmptyList(mTempVideoPaths)) {
            if (TextUtils.isEmpty(mAudioPath)) {
                camera_rightbar_music_cover_mask.setImageResource(R.drawable.shoot_button_music_disabled)
            } else {
                camera_rightbar_music_cover_mask.setImageResource(R.drawable.shoot_button_music_cover_disabled)
            }
            camera_rightbar_music_name.setTextColor(-1711276033)
        } else {
            if (TextUtils.isEmpty(mAudioPath)) {
                camera_rightbar_music_cover_mask.setImageResource(R.drawable.selector_video_music_off)
            } else {
                camera_rightbar_music_cover_mask.setImageResource(R.drawable.shoot_button_music_cover)
            }
            camera_rightbar_music_name.setTextColor(resources.getColor(R.color.white))
        }
    }

    private fun concatVideo() {
        val size = mTempVideoPaths.size
        if (size > 0) {
            if (size <= 1 || !notNeedConcat()) {
                if (size == 1) {
                    val str = mTempVideoPaths[0]
                    val intValue = mShootTypeMap[str]
                    if ((intValue == 3 || intValue == 0) && TextUtils.isEmpty(mAudioPath)) {
                        mFinalVideoPath = str
                        mLastVideoPath.clear()
                        mLastVideoPath.add(mFinalVideoPath)
                        jumpVideoEditPage(mFinalVideoPath)
                        return
                    }
                }

                lifecycleScope.launch(Dispatchers.Main) {
                    showProgressDialog(R.string.combining)
                    val path = withContext(Dispatchers.IO){
                        var finalVideoPath: String = ""
                        var str: String?
                        val z: Boolean
                        if (size > 1) {
                            finalVideoPath = MediaKitExt.concatVideo(
                                mTempVideoPaths,
                                mShootTypeMap,
                                this@CameraActivity
                            )
                        } else {
                            try {
                                Thread.sleep(800)
                            } catch (e: InterruptedException) {
                            }
                            str = mTempVideoPaths[0]
                            finalVideoPath = MediaKitExt.covertSpeedToVideo(
                                str,
                                mShootTypeMap[str]!!,
                                this@CameraActivity
                            )
                        }
                        var str2 = ""
                        if (!FileUtil.exists(finalVideoPath)) {
                            str = str2
                            z = false
                        } else if (FileUtil.exists(mAudioPath)) {
                            str2 = createVideoPath()
                            val str3 = str2
                            z = MediaKitExt.covertMusicToAACAndMergeVideo(
                                finalVideoPath,
                                str2,
                                mAudioPath,
                                this@CameraActivity
                            )
                            finalVideoPath = str3
                        } else {
                            z = true
                        }
                        if (!z) {
                            finalVideoPath = ""
                        }
                        finalVideoPath
                    }

                    dismissProgressDialog()
                    if (FileUtil.exists(path)) {
                        mFinalVideoPath = path
                        jumpVideoEditPage(mFinalVideoPath)
                        mLastVideoPath.clear()
                        mLastVideoPath.addAll(mTempVideoPaths)
                    }else{
                        ToastUtils.showToast(R.string.record_video_failed)
                    }
                }
            }else{
                jumpVideoEditPage(mFinalVideoPath)
            }
        }
    }


    private fun notNeedConcat(): Boolean {
        if (mLastVideoPath.size < 1 || mLastVideoPath.size != mTempVideoPaths.size || TextUtils.isEmpty(
                mFinalVideoPath
            ) || !File(mFinalVideoPath).exists()
        ) {
            return false
        }
        for (i in mLastVideoPath.indices) {
            if (mLastVideoPath[i] != mTempVideoPaths[i]) {
                return false
            }
        }
        return true
    }

    private fun showCountDownView() {
        hideAllView()
        camera_time_count.visibility = View.VISIBLE
    }

    private fun hideCountDownView() {
        showAllView()
        camera_time_count.visibility = View.GONE
    }

    private fun cancelCountDown() {
        shutter_button.isSelected = false
        camera_time_count.doJob()
    }

    private fun showAllView() {
        if (camera_time_count.visibility != View.VISIBLE) {
            camera_topbar.visibility = View.VISIBLE
            val view: View = camera_rightbar
            view.visibility = View.VISIBLE
            if (mCameraType == 0) {
                view.visibility = View.GONE
            }

            if (shoot_tab.visibility != View.VISIBLE && !mCameraFilterBeautyHelper.isShown && camera_dynamic_stick_rv.visibility != View.VISIBLE) {
                showBottomView()
            }
        }
    }

    private fun hideAllView() {
        camera_topbar.visibility = View.GONE
        camera_rightbar.visibility = View.GONE
        hideBottomView()
    }

    private fun createVideoPath(): String {
        return Storage.getFilePathByType(3) + Util.getCurTime().toString() + ".mp4"
    }

    private fun jumpVideoEditPage(mFinalVideoPath: String?) {
        startActivity(Intent(this,VideoEditActivity::class.java).putExtra(VIDEO_PATH,mFinalVideoPath))
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(cutMusicInfoEvent: CutMusicInfoEvent) {
        if (cutMusicInfoEvent.mIsFromCamera) {
            mAudioPath = cutMusicInfoEvent.mCutMusicPath
            mMusicHelper.setMusicPath(mAudioPath)
            mMusicHelper.music = cutMusicInfoEvent.mMusic
            Glide.with(this).load(cutMusicInfoEvent.mMusic.photo).apply(RequestOptions.bitmapTransform(CircleCrop())).into(camera_rightbar_music_cover)
            changeMusicBtnState()
        }
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