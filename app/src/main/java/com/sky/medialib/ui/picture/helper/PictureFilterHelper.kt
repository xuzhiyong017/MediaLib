package com.sky.medialib.ui.picture.helper

import android.app.Activity
import android.graphics.*
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sky.media.image.core.out.BitmapOutput
import com.sky.media.kit.model.FilterExt
import com.sky.medialib.R
import com.sky.medialib.ui.kit.adjust.AdjusterExt
import com.sky.medialib.ui.kit.filter.OriginMagicFilterExt
import com.sky.medialib.ui.kit.filter.OriginNormalFilter
import com.sky.medialib.ui.kit.view.IToolAdjustedListener
import com.sky.medialib.ui.kit.view.ToolAdjusterPopupWindow
import com.sky.medialib.ui.kit.view.ToolSeekBar
import com.sky.medialib.ui.picture.adapter.FiltersAdapter
import com.sky.medialib.ui.picture.adapter.MagicFilterAdapter
import com.sky.medialib.ui.picture.adapter.ToolsAdapter
import com.sky.medialib.ui.picture.process.ImageProcessExt
import com.sky.medialib.util.Storage
import px
import kotlin.math.roundToInt

/**
 * @author: xuzhiyong
 * @date: 2021/7/31  下午2:09
 * @Email: 18971269648@163.com
 * @description: savePic
 */
class PictureFilterHelper(
    val activity: Activity,
    val screenWidth: Int,
    val screenHeight: Int,
    val mStickerHelper: PictureStickerHelper,
    private val onImageProcessListener: OnActivityListener
) {

    interface OnActivityListener{
        fun showAllView()
        fun getProcess(): ImageProcessExt
        fun isMagicTab():Boolean
        fun isNormalFilterTab():Boolean
    }

    var stickerHelper: PictureStickerHelper? = null
    private val mEditImageProcessExt = onImageProcessListener.getProcess()
    private val bitmapWaterMarker = BitmapFactory.decodeResource(activity.resources, R.drawable.watermark_logo)
    private val mMenuHeight = (screenWidth * 3 / 16 + 28.0f.px).roundToInt()
    private lateinit var mMagicFilterAdapter:MagicFilterAdapter
    private lateinit var mNormalFilter:FiltersAdapter
    private lateinit var mToolAdapter:ToolsAdapter

    val tools_gallery_layout = activity.findViewById<View>(R.id.tools_gallery_layout)
    val tools_gallery = activity.findViewById<RecyclerView>(R.id.tools_gallery)
    val magic_bar_layout = activity.findViewById<View>(R.id.magic_bar_layout)
    val magic_seek_bar = activity.findViewById<ToolSeekBar>(R.id.magic_seek_bar)
    val magic_seek_bar_value = activity.findViewById<TextView>(R.id.magic_seek_bar_value)
    val intelligence_filter_textview = activity.findViewById<TextView>(R.id.intelligence_filter_textview)
    val intelligence_filter_view = activity.findViewById<ImageView>(R.id.intelligence_filter_view)
    val filter_bar_layout = activity.findViewById<View>(R.id.filter_bar_layout)
    val filter_seek_bar_value = activity.findViewById<TextView>(R.id.filter_seek_bar_value)
    val filter_seek_bar = activity.findViewById<ToolSeekBar>(R.id.filter_seek_bar)
    val intelligence_filter_layout = activity.findViewById<View>(R.id.intelligence_filter_layout)



    init {
        mMagicFilterAdapter = MagicFilterAdapter(onImageProcessListener.getProcess(),screenWidth){_,_,magicFilterExt ->
            val adjusterExt = magicFilterExt.adjuster as AdjusterExt
            if(!mEditImageProcessExt.replaceMagicFilter(magicFilterExt)){
                adjusterExt.setMirrorPos(adjusterExt.currentMirrorPos + 1)
                mEditImageProcessExt.refreshAllFilters()
            }
            magicFilterExt.startTool()
            if(magicFilterExt is OriginMagicFilterExt){
                magic_bar_layout.visibility = View.INVISIBLE
            }else if(magicFilterExt.isOff() || magicFilterExt.isOff(-1)){
                magic_bar_layout.visibility = View.INVISIBLE
            }else if(onImageProcessListener.isMagicTab()){
                magic_bar_layout.visibility = View.VISIBLE
                magic_bar_layout.post {
                    val adjuster = magicFilterExt.adjuster
                    magic_seek_bar.progress = adjuster?.progress?.minus(adjuster?.start!!) ?: 0
                }
            }else{
                magic_bar_layout.visibility = View.INVISIBLE
            }
            mMagicFilterAdapter.notifyDataSetChanged()

        }
        magic_seek_bar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                magic_seek_bar_value.x = magic_seek_bar.x + magic_seek_bar.thumbX - magic_seek_bar_value.width / 2
                magic_seek_bar_value.invalidate()
                val filter = mEditImageProcessExt.getMagicFilter()
                val adjuster = filter?.adjuster
                adjuster?.run {
                    adjust(start + progress)
                    magic_seek_bar_value.text = progressText
                    mEditImageProcessExt.requestRender()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        mNormalFilter = FiltersAdapter(onImageProcessListener.getProcess(),screenWidth){_,_,normalFilter ->

            mEditImageProcessExt.replaceNormalFilter(normalFilter)
            normalFilter.startTool()
            if(normalFilter is OriginNormalFilter){
                filter_bar_layout.visibility = View.INVISIBLE
            }else if(onImageProcessListener.isNormalFilterTab()){
                filter_bar_layout.visibility = View.VISIBLE
                filter_bar_layout.post {
                    filter_seek_bar.progress =  normalFilter.adjuster?.progress?.minus(normalFilter.adjuster?.start!!) ?: 0
                }
            }else {
                filter_bar_layout.visibility = View.INVISIBLE
            }
            mNormalFilter.notifyDataSetChanged()
            intelligence_filter_textview.isSelected = false
            intelligence_filter_textview.text = activity.getString(R.string.intelligence_filter)
            intelligence_filter_view.setImageResource(R.drawable.edit_intelligence_filter_off)

        }
        filter_seek_bar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                filter_seek_bar_value.x = filter_seek_bar.x + filter_seek_bar.thumbX - filter_seek_bar_value.width / 2
                filter_seek_bar_value.invalidate()
                val filter = mEditImageProcessExt.getNormalFilter()
                val adjuster = filter?.adjuster
                adjuster?.run {
                    adjust(start + progress)
                    filter_seek_bar_value.text = progressText
                    mEditImageProcessExt.requestRender()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        mToolAdapter = ToolsAdapter(onImageProcessListener.getProcess(),screenWidth){_,_,filter ->
            filter.startTool()
            mEditImageProcessExt.addFilter(filter)
            mStickerHelper.isTopViewShowController()
            showToolFilterWindow(filter)
            mToolAdapter.notifyDataSetChanged()
        }

        val layoutParams = tools_gallery_layout.layoutParams
        layoutParams.height = mMenuHeight
        tools_gallery_layout.layoutParams = layoutParams
        tools_gallery_layout.visibility = View.GONE

        tools_gallery.layoutManager = LinearLayoutManager(activity,RecyclerView.HORIZONTAL,false)
        tools_gallery.isFocusable = false
    }

    private fun showToolFilterWindow(filterExt: FilterExt) {
        tools_gallery_layout.visibility = View.GONE
        val toolAdjusterPopupWindow = ToolAdjusterPopupWindow(activity,mMenuHeight,filterExt,object :IToolAdjustedListener{
            override fun ok() {
                mToolAdapter.notifyDataSetChanged()
                tools_gallery_layout.visibility = View.VISIBLE
            }

            override fun onProgress(i: Int) {
                mEditImageProcessExt.requestRender()
            }

            override fun cancel() {
                mEditImageProcessExt.undoFilter(filterExt)
                mToolAdapter.notifyDataSetChanged()
                tools_gallery_layout.visibility = View.VISIBLE
            }

            override fun touchDown() {
                mEditImageProcessExt.disableAllFilters()
            }

            override fun touchUp() {
                mEditImageProcessExt.refreshAllFilters()
            }
        })

        toolAdjusterPopupWindow.setOnDismissListener {
            mStickerHelper.resumeTopViewShowController()
        }
        toolAdjusterPopupWindow.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        toolAdjusterPopupWindow.showAtLocation(activity.findViewById(R.id.edit_menu), Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM, 0, 1)

    }

    fun showNormalFilter(){
        intelligence_filter_layout.visibility = View.GONE
        tools_gallery.adapter = mNormalFilter
        tools_gallery_layout.visibility = View.VISIBLE

        val filter = mEditImageProcessExt.getNormalFilter()
        if(mEditImageProcessExt.isRealFilter()){
            if(filter is OriginNormalFilter || !onImageProcessListener.isNormalFilterTab()){
                hideNormalFilter()
            }else{
                filter_bar_layout.visibility = View.VISIBLE
                filter_bar_layout.post {
                    val adjuster = filter?.adjuster
                    adjuster?.run {
                        filter_seek_bar.progress = progress - start
                    }
                }
            }
        }else{
            hideNormalFilter()
        }

    }

    fun showMagicFilter() {
        intelligence_filter_layout.visibility = View.GONE
        tools_gallery.adapter = mMagicFilterAdapter
        tools_gallery_layout.visibility = View.VISIBLE
        if(mEditImageProcessExt.isMagicFilter()){
            val filter = mEditImageProcessExt.getMagicFilter()
            if(filter is OriginMagicFilterExt || !onImageProcessListener.isMagicTab()){
                hideMagicFilter()
            }else{
                magic_bar_layout.visibility = View.VISIBLE
                magic_bar_layout.post {
                    val adjuster = filter?.adjuster
                    adjuster?.run {
                        magic_seek_bar.progress = progress - start
                    }
                }
            }
        }else{
            hideMagicFilter()
        }
    }

    fun showToolsFilter() {
        intelligence_filter_layout.visibility = View.GONE
        tools_gallery.adapter = mToolAdapter
        tools_gallery_layout.visibility = View.VISIBLE
    }


    fun savePic(needWaterMark:Boolean){
        mEditImageProcessExt.getOutputBitmap(object : BitmapOutput.BitmapOutputCallback{
            override fun bitmapOutput(bitmap: Bitmap?) {
                if (bitmap != null) {
                    val copy = bitmap.copy(Bitmap.Config.ARGB_8888,true)
                    val canvas = Canvas(copy)
                    stickerHelper?.drawCanvas(canvas,copy.width,copy.height)
                    if(needWaterMark){
                        val width3 = (copy.width.toFloat() * 0.2f).toInt()
                        Canvas(copy).drawBitmap(bitmapWaterMarker,null,RectF((copy.width - width3).toFloat(),
                            (copy.height - (( width3 / bitmapWaterMarker.width) * bitmapWaterMarker.height)).toFloat(),
                             copy.width.toFloat(),
                            copy.height.toFloat()), Paint()
                        )
                    }

                    Storage.storageToDCIMJpgPath(copy)
                    Toast.makeText(activity,"save pic success",Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    fun hideMagicFilter() {
        magic_bar_layout.visibility = View.INVISIBLE
    }

    fun hideToolFilter() {
        tools_gallery_layout.visibility = View.INVISIBLE
    }

    fun hideNormalFilter() {
        filter_bar_layout.visibility = View.INVISIBLE
    }




}