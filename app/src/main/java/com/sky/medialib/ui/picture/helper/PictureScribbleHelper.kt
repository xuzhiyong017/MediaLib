package com.sky.medialib.ui.picture.helper

import android.app.Activity
import android.graphics.Bitmap
import android.view.View
import android.widget.*
import com.sky.media.image.core.filter.Filter
import com.sky.media.kit.filter.scribble.ScribbleAdjuster
import com.sky.medialib.R
import com.sky.medialib.ui.kit.manager.ToolFilterManager.clipScribbleTool
import com.sky.medialib.ui.kit.manager.ToolFilterManager.paintScribbleTool
import com.sky.medialib.ui.kit.view.DrawView
import com.sky.medialib.ui.kit.view.DrawView.onDrawChangeListener
import com.sky.medialib.ui.picture.process.ImageProcessExt

class PictureScribbleHelper(
    private val activity: Activity,
    private val mEditImageProcess: ImageProcessExt,
    private val listener: OnListener?
) {
    private var scribble_layout: RelativeLayout? = null
    private var scribble_cx: ImageView? = null
    private var texturesGroup: RadioGroup? = null
    private var brush_size: RadioGroup? = null
    private var scribble_ca: ImageView? = null
    private var scribble_draw_view: DrawView? = null
    private var bottom_bar: View? = null
    private var foot_nav_cancel: View? = null
    private var foot_nav_done: View? = null
    private var title: TextView? = null

    interface OnListener {
        fun onApply()
        fun onCancel()
    }

    private fun init() {
        scribble_layout = activity.findViewById<View>(R.id.scribble_layout) as RelativeLayout
        scribble_draw_view = activity.findViewById<View>(R.id.scribble_draw_view) as DrawView
        scribble_draw_view!!.paintStyle = -16777165
        scribble_draw_view!!.setDrawListener(object : onDrawChangeListener {
            override fun onChangeBitmap(bitmap: Bitmap, bitmap2: Bitmap) {
                val b: Filter? = clipScribbleTool
                val c: Filter? = paintScribbleTool
                if (!mEditImageProcess.getUserFilters()!!.contains(b)) {
                    mEditImageProcess.addFilter(b)
                    mEditImageProcess.addFilter(c)
                }
                (b!!.adjuster as ScribbleAdjuster?)!!.setBitmap(bitmap, bitmap2)
                (c!!.adjuster as ScribbleAdjuster?)!!.setBitmap(bitmap, bitmap2)
                scribble_cx!!.isEnabled = scribble_draw_view!!.isEnable
                mEditImageProcess.requestRender()
            }

            override fun aleadyBitmap(bitmap: Bitmap, bitmap2: Bitmap) {
                val b = clipScribbleTool
                val c = paintScribbleTool
                (b!!.adjuster as ScribbleAdjuster?)!!.setBitmap(bitmap, bitmap2)
                (c!!.adjuster as ScribbleAdjuster?)!!.setBitmap(bitmap, bitmap2)
                mEditImageProcess.refreshAllFilters()
            }
        })
        texturesGroup = activity.findViewById<View>(R.id.textures) as RadioGroup
        texturesGroup!!.setOnCheckedChangeListener { group: RadioGroup?, checkedId: Int ->
            if (checkedId > 0 && (activity.findViewById<View>(checkedId) as RadioButton).isChecked) {
                var paintStyle = scribble_draw_view!!.paintStyle
                if (checkedId == R.id.huabi1) {
                    paintStyle = -16777165
                } else if (checkedId == R.id.huabi2) {
                    paintStyle = -16777080
                } else if (checkedId == R.id.aixing) {
                    paintStyle = -16776961
                } else if (checkedId == R.id.caisedian) {
                    paintStyle = -16764160
                } else if (checkedId == R.id.houzi) {
                    paintStyle = -16742400
                } else if (checkedId == R.id.shuye) {
                    paintStyle = -16711936
                } else if (checkedId == R.id.songshu) {
                    paintStyle = -13434880
                } else if (checkedId == R.id.tanhao) {
                    paintStyle = -7864320
                } else if (checkedId == R.id.xingxing) {
                    paintStyle = -65536
                }
                scribble_draw_view!!.paintStyle = paintStyle
                if (scribble_ca!!.isSelected) {
                    scribble_ca!!.isSelected = false
                    brush_size!!.check(
                        getIdByPosition(
                            brush_size,
                            scribble_draw_view!!.paintSizeIndex
                        )
                    )
                }
            }
        }
        brush_size = activity.findViewById<View>(R.id.brush_size) as RadioGroup
        brush_size!!.setOnCheckedChangeListener { group, checkedId ->
            scribble_draw_view!!.setBrushLevel(
                getPositionById(brush_size, checkedId)
            )
        }
        scribble_cx = activity.findViewById<View>(R.id.scribble_cx) as ImageView
        scribble_cx!!.setOnClickListener { v: View? ->
            scribble_draw_view!!.undoLast()
            scribble_cx!!.isEnabled = scribble_draw_view!!.isEnable
        }
        scribble_ca = activity.findViewById<View>(R.id.scribble_ca) as ImageView
        scribble_ca!!.setOnClickListener { v: View? ->
            if (scribble_draw_view!!.paintStyle != -16777216) {
                scribble_ca!!.isSelected = true
                texturesGroup!!.clearCheck()
                scribble_draw_view!!.paintStyle = -16777216
                brush_size!!.check(getIdByPosition(brush_size, scribble_draw_view!!.paintSizeIndex))
            }
        }
        bottom_bar = activity.findViewById(R.id.bottom_bar)
        foot_nav_cancel = activity.findViewById(R.id.foot_nav_cancel)
        foot_nav_done = activity.findViewById(R.id.foot_nav_done)
        title = activity.findViewById<View>(R.id.title) as TextView
    }

    fun setBitmap(bitmap: Bitmap?) {
        scribble_draw_view!!.setBitmap(bitmap)
    }

    fun refreshDrawView() {
        scribble_draw_view!!.requestLayout()
    }

    fun showDauber() {
        bottom_bar!!.visibility = View.VISIBLE
        title!!.text = activity.getString(R.string.draw)
        scribble_layout!!.visibility = View.VISIBLE
        scribble_draw_view!!.visibility = View.VISIBLE
        scribble_cx!!.isEnabled = scribble_draw_view!!.isEnable
        foot_nav_cancel!!.setOnClickListener { v: View? ->
            scribble_draw_view!!.cancelAll()
            hideAllView()
            listener?.onCancel()
        }
        foot_nav_done!!.setOnClickListener { v: View? ->
            hideAllView()
            listener?.onApply()
        }
    }

    val bottomMenuHeight: Int
        get() = scribble_layout!!.height + bottom_bar!!.height

    fun hideAllView() {
        scribble_layout!!.visibility = View.INVISIBLE
        bottom_bar!!.visibility = View.INVISIBLE
        scribble_draw_view!!.visibility = View.GONE
        scribble_draw_view!!.saveLastFrame()
    }

    private fun getPositionById(radioGroup: RadioGroup?, id: Int): Int {
        var i2 = 0
        var i3 = 0
        while (i2 < radioGroup!!.childCount) {
            val childAt = radioGroup.getChildAt(i2)
            if (childAt is RadioButton) {
                if (childAt.getId() == id) {
                    break
                }
                i3++
            }
            i2++
        }
        return i3
    }

    private fun getIdByPosition(radioGroup: RadioGroup?, i: Int): Int {
        var i2 = 0
        var i3 = 0
        while (i2 < radioGroup!!.childCount) {
            val childAt = radioGroup.getChildAt(i2)
            if (childAt is RadioButton) {
                if (i3 == i) {
                    return childAt.getId()
                }
                i3++
            }
            i2++
        }
        return -1
    }

    companion object {
        private const val f8705a = R.id.huabi1
    }

    init {
        init()
    }
}