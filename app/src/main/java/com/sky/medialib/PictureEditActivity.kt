package com.sky.medialib

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.sky.media.image.core.out.BitmapOutput
import com.sky.medialib.ui.picture.process.ImageProcessExt
import com.sky.medialib.ui.kit.common.animate.ViewAnimator
import com.sky.medialib.ui.kit.manager.ToolFilterManager
import com.sky.medialib.ui.kit.view.crop.ClipPopupWindow
import com.sky.medialib.ui.kit.view.editmenu.EditMenu
import com.sky.medialib.ui.kit.view.editmenu.EditMenuItem
import com.sky.medialib.ui.picture.helper.*
import kotlinx.android.synthetic.main.activity_picture_edit.*
import px
import kotlin.math.roundToInt

class PictureEditActivity : AppCompatActivity()
    ,EditMenu.OnItemClickListener,PictureFilterHelper.OnActivityListener,BitmapOutput.BitmapOutputCallback {

    private var mMenuHeight = 0
    private var mFrameHeight = 0
    lateinit var mEditImageProcessExt: ImageProcessExt
    var mState = 0
    var mCurrentTab:EditMenuItem = EditMenuItem.NONE
    lateinit var mBeautyHelper:PictureBeautyHelper
    lateinit var mStickerHelper: PictureStickerHelper
    lateinit var mFilterHelper: PictureFilterHelper
    lateinit var mClipHelper: PictureClipHelper
    lateinit var mScribbleHelper: PictureScribbleHelper
    private var mIsTouchToShowOriginal = true
    private var mIsLongClick = false
    private var mScreenHeight = 0
    private var mScreenWidth = 0
    private var mCurrentPhoto = ""
    private lateinit var mCurBitmap: Bitmap
    val mHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture_edit)
        initParams()
        mEditImageProcessExt = ImageProcessExt(frame,processing_view)
        mCurBitmap = PictureBitmapHolder.getInstance().handleBitmap(BitmapFactory.decodeResource(resources,R.drawable.image1),mScreenWidth,mScreenHeight)
        mEditImageProcessExt.initInputBitmap(mCurBitmap,
            resources.displayMetrics.widthPixels,resources.displayMetrics.heightPixels,null)


        initHelper()
        initListener()
        fixProcessingAreaCenterVertex()
    }

    private fun initParams() {
        mScreenHeight = resources.displayMetrics.heightPixels
        mScreenWidth = resources.displayMetrics.widthPixels
        mMenuHeight = (mScreenHeight - 75.0f.px).toInt()
        mFrameHeight = (mMenuHeight - 55.0f.px).toInt()

        ToolFilterManager.initPictureEditFilter(this)
    }

    private fun initHelper() {

        val listener = object :PictureBeautyHelper.OnImageProcessListener{
            override fun showAllView() {
                showAllCommonFunctionView()
            }

            override fun getProcess(): ImageProcessExt {
                return mEditImageProcessExt
            }
        }

        mBeautyHelper = PictureBeautyHelper(this,"beautyHelper",listener)
        mStickerHelper = PictureStickerHelper(this,mScreenWidth,mScreenHeight)
        mFilterHelper = PictureFilterHelper(this,mScreenWidth,mScreenHeight,mStickerHelper,this)
        mFilterHelper.stickerHelper = mStickerHelper

        mScribbleHelper = PictureScribbleHelper(this,mEditImageProcessExt,object : PictureScribbleHelper.OnListener{
            override fun onApply() {
                showAllCommonFunctionView()
            }

            override fun onCancel() {
                showAllCommonFunctionView()
            }
        }).apply {
            setBitmap(mCurBitmap)
        }

        mClipHelper = PictureClipHelper(this, object : ClipPopupWindow.onClipListener {
            override fun onComplete() {
                showAllCommonFunctionView()
            }

            override fun onCancel() {
                showAllCommonFunctionView()
            }
        }).apply {
            setOnImageClippedListener {
                if(mEditImageProcessExt.getUserFilters()?.contains(ToolFilterManager.clipScribbleTool) == true){
                    mEditImageProcessExt.clearAllFilters()
                }
                mScribbleHelper.setBitmap(it)
                mEditImageProcessExt.renderBitmap(it,mScreenWidth,mScreenHeight)
                val width = it.width * 1.0f / it.height
                frame.setAspectRatio(
                    width,
                    mScreenWidth,
                    mScreenHeight
                )
                frame.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            val width: Int = frame.width
                            val height: Int = frame.height
                            if (width.toFloat() == mScreenWidth.toFloat() * 1.0f / mScreenHeight.toFloat()) {
                                changePreviewFrameLayoutPosition(mScreenHeight, true)
                            } else {
                                changePreviewFrameLayoutPosition(mMenuHeight, true)
                            }
                            mStickerHelper.checkDealSticker(false, width, height)
                            frame.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            mScribbleHelper.refreshDrawView()
                            mEditImageProcessExt.renderBitmap(
                                it, mScreenWidth,
                                mScreenHeight
                            )
                        }
                })
                mHandler.postDelayed({
                    mEditImageProcessExt.refreshAllFilters()
                    mFilterHelper.savePic(false)
                    mClipHelper.hideCropView()
                },200)
            }
        }

    }

    private fun onFrameCreate() {
        mHandler.postDelayed({
            mEditImageProcessExt.refreshAllFilters()
            mClipHelper.hideCropView()
        },200)
    }

    private fun changeBitmapSize(width: Int, height: Int) {
        frame.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    frame.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    scaleBitmap(
                        mEditImageProcessExt.getSourceBitmap(),
                        width,
                        height
                    )?.run {
                        mEditImageProcessExt.renderBitmap(this, width, height)
                        mEditImageProcessExt.refreshAllFilters()
                    }

                }
            })
    }

    private fun scaleBitmap(bitmap: Bitmap?, screenWidth: Int, screenHeight: Int): Bitmap? {
        if (bitmap == null) {
            return null
        }
        val width = bitmap.width
        val height = bitmap.height
        var f = screenWidth.toFloat() / width.toFloat()
        val f2 = screenHeight.toFloat() / height.toFloat()
        if (f > f2) {
            f = f2
        }
        val matrix = Matrix()
        matrix.postScale(f, f)
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
    }

    private fun initListener() {
        edit_menu.setOnItemClickListener(this)
        processing_view.setOnLongClickListener {
            mIsLongClick = true
            if(mIsTouchToShowOriginal){
                mEditImageProcessExt.disableAllFilters()
            }
            return@setOnLongClickListener true
        }
        processing_view.setOnTouchListener { _, event ->
            when (event!!.action) {
                MotionEvent.ACTION_DOWN -> {
                    mIsLongClick = false
                }
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> run {
                    if (!mIsTouchToShowOriginal || !mIsLongClick) {
                        if (!mIsLongClick && (mCurrentTab === EditMenuItem.STICKER || mCurrentTab === EditMenuItem.FILTER || mCurrentTab === EditMenuItem.MAGIC || mCurrentTab === EditMenuItem.TOOL)) {
                            hideAllTab()
                            showAllCommonFunctionView()
                            return@run
                        }
                    }else{
                        mEditImageProcessExt!!.refreshAllFilters()
                        return@run
                    }
                }
            }
            false
        }

        show_area_layout.setOnClickListener {
            if (this.mCurrentTab == EditMenuItem.STICKER || this.mCurrentTab == EditMenuItem.FILTER || this.mCurrentTab == EditMenuItem.MAGIC || this.mCurrentTab == EditMenuItem.TOOL) {
                hideAllTab();
                showAllCommonFunctionView();
            }
        }
        pic_save.setOnClickListener {
            savePic(true)
        }

        edit_pic_cancel.setOnClickListener {
            if(mCurrentTab == EditMenuItem.NONE){
                showFinishDialog()
            }else{
                hideAllTab()
                showAllCommonFunctionView()
            }
        }
    }

    private fun savePic(needWaterMark: Boolean) {
        mFilterHelper.savePic(needWaterMark)
    }

    private fun showFinishDialog() {
        finish()
    }

    private fun showAllCommonFunctionView() {
        edit_menu.visibility = View.VISIBLE
        edit_menu_bg.visibility = View.VISIBLE
        pic_save.visibility = View.VISIBLE
        edit_pic_cancel.visibility = View.VISIBLE
        mStickerHelper.updateViewControllerStatus(true,true)
        mCurrentTab = EditMenuItem.NONE
        fixProcessingAreaCenterVertex()
    }

    override fun onItemClick(editMenuItem: EditMenuItem) {
        mState = 0
        mCurrentTab = editMenuItem
        hideAllTab()
        showTab()
        selectCommonFunctionView()
        fixProcessingAreaCenterVertex()
    }

    private fun fixProcessingAreaCenterVertex() {
        var height = when(mCurrentTab){
            EditMenuItem.BEAUTY ->  mScreenHeight - 55.0f.px
            EditMenuItem.STICKER -> mScreenHeight - mStickerHelper.getBottomMenuHeight()
            EditMenuItem.FILTER -> mScreenHeight - mFilterHelper.getBottomMenuHeight()
            EditMenuItem.MAGIC -> mScreenHeight - mFilterHelper.getBottomMenuHeight()
            EditMenuItem.TOOL -> mScreenHeight - mFilterHelper.getBottomMenuHeight()
            EditMenuItem.DAUBER -> mScreenHeight - mScribbleHelper.bottomMenuHeight
            else -> mMenuHeight
        }

        if(mEditImageProcessExt!!.getSourceBitmap() == null
            || mEditImageProcessExt!!.getSourceBitmap()!!.height == 0
            || mEditImageProcessExt!!.getSourceBitmap()!!
                .width.toFloat() * 1.0f / mEditImageProcessExt!!.getSourceBitmap()!!
                .height.toFloat() != mScreenWidth.toFloat() * 1.0f / mScreenHeight.toFloat()
        ){
            changePreviewFrameLayoutPosition(height.toInt(), true)
        }else{
            changePreviewFrameLayoutPosition(mScreenHeight, true)
        }
    }

    private fun changePreviewFrameLayoutPosition(i: Int, z: Boolean) {
        val frameWidth: Int
        val frameHeight: Int
//        if (this.mStickerHelper.isUseFrame()) {
//            frameWidth = this.mFrameView.getFrameWidth()
//            frameHeight = this.mFrameView.getFrameHeight()
//        } else {
            var g: Bitmap? = this.mEditImageProcessExt.getSourceBitmap()
            if (g == null) {
                g = PictureBitmapHolder.getInstance().getOriginalImage(this.mCurrentPhoto)
            }
            if (g != null) {
                frameWidth = g.width
                frameHeight = g.height
            } else {
                frameHeight = 0
                frameWidth = 0
            }
//        }
        if (frameWidth != 0) {
            val f: Float
            var i2 = mScreenWidth * frameHeight / frameWidth
            if (i > 0) {
                if (i2 > i) {
                    i2 = i
                }
                f = if (frameWidth >= frameHeight) {
                    (i - i2).toFloat() / 2.0f
                } else if (i2 > i) {
                    0.0f
                } else {
                    (i - i2).toFloat() / 2.0f
                }
            } else {
                f = 0.0f
            }
            if (z) {
                ViewAnimator.animate(frame).translationY(frame.y,f).setDuration(200).start()
                return
            }
            val layoutParams = frame.layoutParams as RelativeLayout.LayoutParams
            layoutParams.topMargin = f.roundToInt()
            this.frame.layoutParams = layoutParams
        }
    }


    private fun selectCommonFunctionView() {
        edit_menu.visibility = View.GONE
        edit_menu_bg.visibility = View.GONE
        pic_save.visibility = View.GONE
        edit_pic_cancel.visibility = View.GONE

    }

    private fun showTab() {
        when(mCurrentTab){
            EditMenuItem.BEAUTY -> {
                mStickerHelper.updateViewControllerStatus(true,true)
                mBeautyHelper.showBeautyAdjustWindow()
                mIsTouchToShowOriginal = true
            }
            EditMenuItem.STICKER -> {
                mStickerHelper.updateViewControllerStatus(true,true)
                mStickerHelper.showStickerListView()
                mIsTouchToShowOriginal = true
            }
            EditMenuItem.FILTER -> {
                mStickerHelper.updateViewControllerStatus(true,true)
                mFilterHelper.showNormalFilter()
                mIsTouchToShowOriginal = true
            }
            EditMenuItem.MAGIC -> {
                mStickerHelper.updateViewControllerStatus(true,true)
                mFilterHelper.showMagicFilter()
                mIsTouchToShowOriginal = true
            }
            EditMenuItem.TOOL -> {
                mStickerHelper.updateViewControllerStatus(true,true)
                mFilterHelper.showToolsFilter()
                mIsTouchToShowOriginal = true
            }
            EditMenuItem.CLIP -> {
                mEditImageProcessExt.getOutputBitmap(this)
                mStickerHelper.updateViewControllerStatus(false,false)
                mIsTouchToShowOriginal = false
            }
            EditMenuItem.DAUBER -> {
                mStickerHelper.updateViewControllerStatus(true,false)
                mScribbleHelper.showDauber()
                mIsTouchToShowOriginal = false
            }
        }
    }

    private fun hideAllTab() {
        mBeautyHelper.hideWindow()
        mStickerHelper.hideStickerListView()
        mFilterHelper.hideNormalFilter()
        mFilterHelper.hideMagicFilter()
        mFilterHelper.hideToolFilter()
        mClipHelper.hideAllView()
        mScribbleHelper.hideAllView()
    }

    override fun showAllView() {
        showAllCommonFunctionView()
    }

    override fun getProcess(): ImageProcessExt {
       return mEditImageProcessExt
    }

    override fun isMagicTab(): Boolean {
        return mCurrentTab == EditMenuItem.MAGIC
    }

    override fun isNormalFilterTab(): Boolean {
        return mCurrentTab == EditMenuItem.FILTER
    }

    override fun bitmapOutput(bitmap: Bitmap?) {
        runOnUiThread {
            bitmap?.run {
                if(mEditImageProcessExt.getUserFilters()?.contains(ToolFilterManager.clipScribbleTool) == true){
                    mClipHelper.setClipBitmap(bitmap,null)
                }else{
                    mClipHelper.setClipBitmap(bitmap,mEditImageProcessExt.getSourceBitmap())
                }
            }
        }
    }

    override fun onKeyDown(i: Int, keyEvent: KeyEvent?): Boolean {
        if (i != 4) {
            return super.onKeyDown(i, keyEvent)
        }
        if (edit_menu.visibility != 0) {
            hideAllTab()
            showAllCommonFunctionView()
            return true
        }
        showFinishDialog()
        return true
    }


    override fun onDestroy() {
        super.onDestroy()
        mEditImageProcessExt.clearAllFilters()
    }
}