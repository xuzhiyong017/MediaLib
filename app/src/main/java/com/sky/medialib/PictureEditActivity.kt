package com.sky.medialib

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.sky.media.image.core.process.ImageProcessExt
import com.sky.medialib.ui.kit.common.animate.ViewAnimator
import com.sky.medialib.ui.kit.view.editmenu.EditMenu
import com.sky.medialib.ui.kit.view.editmenu.EditMenuItem
import com.sky.medialib.ui.picture.helper.PictureBeautyHelper
import com.sky.medialib.ui.picture.helper.PictureBitmapHolder
import com.sky.medialib.ui.picture.helper.PictureFilterHelper
import com.sky.medialib.ui.picture.helper.PictureStickerHelper
import kotlinx.android.synthetic.main.activity_picture_edit.*
import px
import kotlin.math.roundToInt

class PictureEditActivity : AppCompatActivity(),EditMenu.OnItemClickListener {

    private var mMenuHeight = 0
    private var mFrameHeight = 0
    lateinit var mEditImageProcessExt:ImageProcessExt
    var mState = 0
    var mCurrentTab:EditMenuItem = EditMenuItem.NONE
    lateinit var mBeautyHelper:PictureBeautyHelper
    lateinit var mStickerHelper: PictureStickerHelper
    lateinit var mFilterHelper: PictureFilterHelper
    private var mIsTouchToShowOriginal = true
    private var mIsLongClick = false
    private var mScreenHeight = 0
    private var mScreenWidth = 0
    private var mCurrentPhoto = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture_edit)
        initParams()
        mEditImageProcessExt = ImageProcessExt(frame,processing_view)
        mEditImageProcessExt.initInputBitmap(BitmapFactory.decodeResource(resources,R.drawable.image1),
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
        mFilterHelper = PictureFilterHelper(this,listener)
        mStickerHelper = PictureStickerHelper(this,mScreenWidth,mScreenHeight)
        mFilterHelper.stickerHelper = mStickerHelper

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
                        if (!mIsLongClick && (mCurrentTab === EditMenuItem.STICKER || mCurrentTab === EditMenuItem.FILTER || mCurrentTab === EditMenuItem.MAGIC || mCurrentTab === EditMenuItem.TOOL || mCurrentTab === EditMenuItem.TEXT || mCurrentTab === EditMenuItem.AT)) {
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
            if (this.mCurrentTab == EditMenuItem.STICKER || this.mCurrentTab == EditMenuItem.FILTER || this.mCurrentTab == EditMenuItem.MAGIC || this.mCurrentTab == EditMenuItem.TOOL || this.mCurrentTab == EditMenuItem.TEXT || this.mCurrentTab == EditMenuItem.AT) {
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

    }

    private fun showAllCommonFunctionView() {
        edit_menu.visibility = View.VISIBLE
        edit_menu_bg.visibility = View.VISIBLE
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
    }

    private fun showTab() {
        when(mCurrentTab){
            EditMenuItem.BEAUTY -> {
                mBeautyHelper.showBeautyAdjustWindow()
                mIsTouchToShowOriginal = true
            }
            EditMenuItem.STICKER -> {
                mStickerHelper.showStickerListView()
            }
        }
    }

    private fun hideAllTab() {
        mBeautyHelper.hideWindow()
        mStickerHelper.hideStickerListView()
    }
}