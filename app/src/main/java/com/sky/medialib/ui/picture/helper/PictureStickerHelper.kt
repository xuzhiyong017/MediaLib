package com.sky.medialib.ui.picture.helper

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sky.medialib.R
import com.sky.medialib.ui.kit.model.StickerModel
import com.sky.medialib.ui.kit.view.StickerView
import com.sky.medialib.ui.picture.adapter.StickersAdapter


/**
 * @author: xuzhiyong
 * @date: 2021/7/30  下午2:07
 * @Email: 18971269648@163.com
 * @description:
 */
class PictureStickerHelper(val mContext: Activity, val screenWidth:Int, val screenHeight:Int) {

    var sticker_layout:RelativeLayout = mContext.findViewById(R.id.sticker_layout)
    var curStickerView:StickerView? = null
    val mStickerRecyclerView:RecyclerView by lazy {
        mContext.findViewById(R.id.sticker_gallery)
    }
    val adapter:StickersAdapter = StickersAdapter{_,_,model ->
        addSticker(model)
    }

    init {
        mStickerRecyclerView.layoutManager = LinearLayoutManager(mContext, RecyclerView.HORIZONTAL,false)
        mStickerRecyclerView.adapter = adapter
    }


    fun addSticker(model: StickerModel){
        for (i in 0 until sticker_layout.childCount){
            val childView = sticker_layout[i]
            if(childView is StickerView){
                childView.isShowDrawController = false
                childView.invalidate()
            }
        }
        val stickerView = StickerView(mContext)
        val bitmap = BitmapFactory.decodeResource(mContext.resources,model.imageRes)
        stickerView.setWaterMark(bitmap)
        stickerView.tag = model.id
        stickerView.setOnStickerClickListener(object :StickerView.OnStickerClickListener{
            override fun onClick() {
                if(curStickerView == null || curStickerView!!.tag != stickerView.tag){
                    updateStickerViewToTop(stickerView)
                }else if(stickerView.isSupportInput){
                    //jump input edit activity
                }
            }
        })

        stickerView.setOnStickerDeleteListener(object :StickerView.OnStickerDeleteListener{

            override fun onClick(tag: Any?) {
                tag?.run {
                    adapter.unSelectSticker(this as Int)
                }
            }
        })

        val showRect = model.showRect
        val height = bitmap.height
        val width = bitmap.width
        var radio = 0.0f
        if(width > height){
            radio = sticker_layout.height.toFloat() / 750.0f
            stickerView.scaleXY(showRect.width * radio,showRect.height * radio)
            stickerView.translateXY(showRect.x * radio + (sticker_layout.width - showRect.width * radio) / 2.0f,showRect.y * radio )
        }else{
            radio = sticker_layout.width / 750.0f
            stickerView.scaleXY(showRect.width * radio,showRect.height * radio)
            stickerView.translateXY(showRect.x * radio,showRect.y * radio)
        }

        val layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM,R.id.processing_view)
        layoutParams.addRule(RelativeLayout.ALIGN_TOP,R.id.processing_view)
        sticker_layout.addView(stickerView,layoutParams)

        curStickerView = stickerView
    }

    private fun updateStickerViewToTop(stickerView: StickerView) {
        for (i in 0 until sticker_layout.childCount){
            val sticker = sticker_layout.getChildAt(i)
            if(sticker == stickerView){
                sticker_layout.removeView(sticker)
                break
            }
        }
        sticker_layout.addView(stickerView)
        curStickerView = stickerView

    }

    fun drawCanvas(canvas: Canvas, width: Int, height: Int) {
        val needResumeViewStatus = isTopViewShowController()
        canvas.save()
        val radio = width / sticker_layout.width.toFloat()
        canvas.scale(radio,radio)
        sticker_layout.draw(canvas)
        canvas.restore()
        if(needResumeViewStatus){
            resumeTopViewShowController()
        }
    }

    private fun resumeTopViewShowController() {
        if (sticker_layout.childCount > 0) {
            val childAt: View = sticker_layout.getChildAt(sticker_layout.childCount - 1)
            if (childAt is StickerView) {
                childAt.isShowDrawController = true
                childAt.invalidate()
            }
        }
    }

    fun isTopViewShowController(): Boolean {
        if (sticker_layout.childCount > 0) {
            val childAt: View = sticker_layout.getChildAt(sticker_layout.childCount - 1)
            if (childAt is StickerView && childAt.isShowDrawController) {
                childAt.isShowDrawController = false
                childAt.invalidate()
                return true
            }
        }
        return false
    }

    fun showStickerListView(){
        mStickerRecyclerView.visibility = View.VISIBLE
    }

    fun hideStickerListView(){
        mStickerRecyclerView.visibility = View.GONE
    }

}

