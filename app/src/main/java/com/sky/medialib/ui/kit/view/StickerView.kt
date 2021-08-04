package com.sky.medialib.ui.kit.view

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.sky.medialib.R
import px
import java.util.*
import kotlin.math.*

class StickerView @JvmOverloads constructor(
    context: Context?,
    attributeSet: AttributeSet? = null,
    i: Int = 0
) : View(context, attributeSet, i) {
    private var mTextPadding = 0
    private var mMatrix: Matrix? = null
    private var mDrawTextPos: FloatArray? = null
    var isFlipper = false
        private set
    var isShowDrawController: Boolean
    private var aspectRatio: Float
    private var f9199G: Float
    private var f9200H: Float
    private var f9201I = 0.0
    var isSupportInput = false
    var text: String? = null
        private set
    private var mInputTips: String? = null
    private var mMaxAvailableSize = 0
    private var mOriginTextOutBound: FloatArray? = null
    private lateinit var mTextOutBound: FloatArray
    private var mOriginShowRect: RectF? = null
    private var mShowRect: RectF? = null
    private var mTextPaint: TextPaint? = null
    private var mTextWidth = 0f
    private var drawTextList: ArrayList<String>? = null
    private var f9213U = false
    private var mOnStickerClickListener: OnStickerClickListener? = null
    private var mOnStickerDeleteListener: OnStickerDeleteListener? = null
    private var textSize: Int = 0
    private var mOnStickerChangedListener: OnStickerChangedListener? = null
    private val mDashPathEffect: PathEffect
    private var textColor: Int
    private var mTouchable: Boolean
    var stickerType: Int // 2:输入文本 1:图片贴纸
        private set
    private var isFaceSticker = false
    private val showTextDebug = false
    private val mDrawFilter: PaintFlagsDrawFilter
    private val textPaintSize: Int
    private var f9226c: FloatArray? = null
    private lateinit var mRoundCornerPos: FloatArray
    private var f9228e: RectF? = null
    private var f9229f: RectF? = null
    private var originRectF: RectF? = null
    private var mDownx = 0f
    private var mDownY = 0f
    private var waterBitmap: Bitmap? = null
    private lateinit var mScaleBitmap: Bitmap
    private lateinit var mCloseBitmap: Bitmap
    private lateinit var mRotateBitmap: Bitmap
    private var mRotateMatrix: Matrix? = null
    private var emptyPaint: Paint? = null
    private var mPaint: Paint? = null
    private var mScaleBitmapWidth = 0f
    private var mScaleBitmapHeight = 0f
    private var mCloseBitmapWidth = 0f
    private var mCloseBitmapHeight = 0f
    private var mRotateBitmapWidth = 0f
    private var mRotateBitmapHeight = 0f
    private var f9246w = false
    private var f9247x = false
    private var f9248y = false
    private var f9249z = false

    interface OnStickerClickListener {
        fun onClick()
    }

    interface OnStickerDeleteListener {
        fun onClick(tag:Any?)
    }

    interface OnStickerChangedListener {
        fun onChange()
    }

    private fun init() {
        emptyPaint = Paint()
        mPaint = Paint(emptyPaint)
        mPaint!!.style = Paint.Style.STROKE
        mPaint!!.strokeWidth = 1.0f.px
        mPaint!!.color = Color.parseColor("#FF000000")
        mScaleBitmap = BitmapFactory.decodeResource(resources, R.drawable.camera_paster_zoom)
        mScaleBitmapWidth = mScaleBitmap.getWidth().toFloat()
        mScaleBitmapHeight = mScaleBitmap.getHeight().toFloat()
        mCloseBitmap = BitmapFactory.decodeResource(resources, R.drawable.camera_paster_close)
        mCloseBitmapWidth = mCloseBitmap.getWidth().toFloat()
        mCloseBitmapHeight = mCloseBitmap.getHeight().toFloat()
        mRotateBitmap = BitmapFactory.decodeResource(resources, R.drawable.camera_paster_turn)
        mRotateBitmapWidth = mRotateBitmap.getWidth().toFloat()
        mRotateBitmapHeight = mRotateBitmap.getHeight().toFloat()
        mTextPaint = TextPaint()
        mTextPaint!!.textSize = textSize.toFloat()
        mTextPaint!!.setShadowLayer(5.0f, 2.0f, 2.0f, -16777216)
        mTextPadding = 20.0f.px.toInt()
        mMaxAvailableSize = ((resources.displayMetrics.widthPixels - mTextPadding * 2 - 24.0f.px) - mCloseBitmapWidth / 2.0f - mRotateBitmapWidth / 2.0f).toInt()
        mInputTips = resources.getString(R.string.camera_sticker_input_hint)
        mTextWidth = mTextPaint!!.measureText(mInputTips)
        mOriginShowRect = RectF(
            0.0f,
            0.0f,
            resources.displayMetrics.widthPixels.toFloat(),
            resources.displayMetrics.heightPixels.toFloat()
        )
        mRoundCornerPos = FloatArray(10)
        mTextOutBound = FloatArray(10)
        mRotateMatrix = Matrix()
        f9229f = RectF()
        mShowRect = RectF()
    }

    fun setText(str: String?, stickerType: Int) {
        this.stickerType = stickerType
        if (stickerType == 1) {
            text = str
            mTextPaint!!.textSize = textSize.toFloat()
        } else if (stickerType == 2) {
            if (TextUtils.isEmpty(str)) {
                (parent as ViewGroup).removeView(this)
                return
            }
            text = str
            mTextPaint!!.textSize = textSize.toFloat()
            drawTextList = getMaxShowTextList(mMaxAvailableSize.toDouble(), text, Int.MAX_VALUE)
            val measureText = (mTextPadding * 2).toFloat() + mTextPaint!!.measureText(
                drawTextList!![0]
            )
            f9226c = floatArrayOf(
                0.0f,
                0.0f,
                measureText,
                0.0f,
                measureText,
                0f,
                0.0f,
                0f,
                measureText / 2.0f,
                (mTextPaint!!.textSize * drawTextList!!.size.toFloat() + (mTextPadding * 2).toFloat()) / 2.0f
            )
            mMatrix = Matrix()
            mDrawTextPos = FloatArray(10)
            f9199G = Math.min(
                5.0f,
                Math.min(
                    resources.displayMetrics.widthPixels.toFloat() * 1.0f / measureText,
                    resources.displayMetrics.heightPixels.toFloat() * 1.0f / measureText
                )
            )
            mTextPaint!!.textSize = textPaintSize.toFloat()
            f9200H = (mTextPaint!!.measureText(drawTextList!![0]) + (mTextPadding * 2).toFloat()) / measureText
            mTextPaint!!.textSize = textSize.toFloat()
        }
        postInvalidate()
        notifyChange()
    }

    fun setTextRect(rectF: RectF) {
        mOriginShowRect = rectF
        mShowRect = RectF()
        mOriginTextOutBound = floatArrayOf(
            rectF.left,
            rectF.top,
            rectF.right,
            rectF.top,
            rectF.right,
            rectF.bottom,
            rectF.left,
            rectF.bottom,
            rectF.centerX(),
            rectF.centerY()
        )
        mTextOutBound = FloatArray(10)
        postInvalidate()
    }

    val textOriginalSize: PointF?
        get() = if (f9226c == null) {
            null
        } else PointF(f9226c!![4], f9226c!![5])

    fun setTextColor(i: Int) {
        textColor = i
        notifyChange()
    }

    fun getTextColor(): Int {
        return textColor
    }

    fun setWaterMark(bitmap: Bitmap?) {
        if (bitmap != null) {
            waterBitmap = bitmap
            aspectRatio = 1.0f
            f9199G = Math.min(
                5.0f, Math.min(
                    resources.displayMetrics.widthPixels.toFloat() * 1.0f / waterBitmap!!.width
                        .toFloat(),
                    resources.displayMetrics.heightPixels.toFloat() * 1.0f / waterBitmap!!.height
                        .toFloat()
                )
            )
            isFocusable = true
            val width = waterBitmap!!.width.toFloat()
            val height = waterBitmap!!.height.toFloat()
            f9226c = floatArrayOf(
                0.0f,
                0.0f,
                width,
                0.0f,
                width,
                height,
                0.0f,
                height,
                width / 2.0f,
                height / 2.0f
            )
            mRoundCornerPos = FloatArray(10)
            f9228e = RectF(0.0f, 0.0f, width, height)
            f9229f = RectF()
            mOriginTextOutBound = floatArrayOf(
                0.0f,
                0.0f,
                width,
                0.0f,
                width,
                height,
                0.0f,
                height,
                width / 2.0f,
                height / 2.0f
            )
            mTextOutBound = FloatArray(10)
            mOriginShowRect = RectF(0.0f, 0.0f, width, height)
            mShowRect = RectF()
            mRotateMatrix = Matrix()
            postInvalidate()
        }
    }

    fun translateXY(x: Float, y: Float) {
        mRotateMatrix!!.postTranslate(x, y)
        notifyChange()
    }

    fun scaleXY(width: Float, height: Float) {
        if (waterBitmap != null) {
            aspectRatio = width * 1.0f / waterBitmap!!.width.toFloat()
            mRotateMatrix!!.postScale(
                width * 1.0f / waterBitmap!!.width
                    .toFloat(), height * 1.0f / waterBitmap!!.height.toFloat()
            )
            notifyChange()
        }
    }

    private fun notifyChange() {
        if (mOnStickerChangedListener != null) {
            mOnStickerChangedListener!!.onChange()
        }
    }

    fun isInRectFArea(rectF: RectF): Boolean {
        val pointFArr = arrayOf(
            PointF(rectF.left, rectF.top),
            PointF(rectF.right, rectF.top),
            PointF(rectF.right, rectF.bottom),
            PointF(rectF.left, rectF.bottom)
        )
        for (a in arrayOf(
            PointF(mRoundCornerPos[0], mRoundCornerPos[1]),
            PointF(
                mRoundCornerPos[2], mRoundCornerPos[3]
            ),
            PointF(mRoundCornerPos[4], mRoundCornerPos[5]),
            PointF(mRoundCornerPos[6], mRoundCornerPos[7])
        )) {
            if (isOccurArea(a, pointFArr)) {
                return true
            }
        }
        return false
    }

    private val rotate: Double
        private get() {
            val fArr = FloatArray(9)
            mRotateMatrix!!.getValues(fArr)
            return atan2(fArr[3].toDouble(), fArr[0].toDouble())
        }
    private val scale: Double
        private get() {
            val fArr = FloatArray(9)
            mRotateMatrix!!.getValues(fArr)
            return sqrt(
                fArr[3].toDouble().pow(2.0) + fArr[0].toDouble().pow(2.0)
            )
        }
    private val minTextShowLength: Double
        private get() {
            val measureText = mTextPaint!!.measureText("宽").toDouble()
            val rotate = rotate
            return measureText.coerceAtLeast(
                abs(
                    sqrt(
                        ((mTextOutBound[0] - mTextOutBound[6]) * (mTextOutBound[0] - mTextOutBound[6])
                                + (mTextOutBound[1] - mTextOutBound[7]) * (mTextOutBound[1] - mTextOutBound[7])).toDouble()
                    )
                            / sin(rotate)
                ).coerceAtMost(
                    abs(
                        sqrt(
                            ((mTextOutBound[0] - mTextOutBound[2]) * (mTextOutBound[0] - mTextOutBound[2])
                                    + (mTextOutBound[1] - mTextOutBound[3]) * (mTextOutBound[1] - mTextOutBound[3])).toDouble()
                        )
                                / cos(rotate)
                    )
                )
            )
        }

    private fun getMaxShowTextList(avalibleSize: Double, str: String?, round: Int): ArrayList<String> {
        var i2 = 0
        val arrayList: ArrayList<String> = ArrayList<String>()
        if (round <= 1) {
            val stringBuilder = StringBuilder()
            while (i2 < str!!.length) {
                stringBuilder.append(str[i2])
                if (mTextPaint!!.measureText(stringBuilder.toString()).toDouble() >= avalibleSize) {
                    stringBuilder.deleteCharAt(stringBuilder.length - 1)
                    break
                }
                i2++
            }
            arrayList.add(stringBuilder.toString())
            return arrayList
        }
        var i3 = 0
        var i4 = 0
        var i5 = 1
        var f = 0.0f
        while (i3 < str!!.length - 1) {
            f += mTextPaint!!.measureText(str.substring(i3, i3 + 1))
            if (f.toDouble() > avalibleSize) {
                arrayList.add(str.substring(i4, i3))
                i4 = i3
                i5++
                f = 0.0f
            } else {
                i3++
            }
        }
        arrayList.add(str.substring(i4))
        if (round == Int.MAX_VALUE) {
            return arrayList
        }
        val arrayList2: ArrayList<String> = ArrayList<String>()
        val i6 = (round - i5) / 2
        i3 = (round - i5) / 2 + arrayList.size
        while (i2 < round) {
            if (i2 < i6) {
                arrayList2.add("")
            } else if (i2 < i3) {
                arrayList2.add(arrayList[i2 - i6])
            } else {
                arrayList2.add("")
            }
            i2++
        }
        return arrayList2
    }

    val centerPoint: PointF
        get() {
            if (mRoundCornerPos[8] == 0.0f || mRoundCornerPos[9] == 0.0f) {
                mRotateMatrix!!.mapPoints(mRoundCornerPos, f9226c)
            }
            return PointF(mRoundCornerPos[8], mRoundCornerPos[9])
        }

    fun setStickerFlipped(z: Boolean) {
        isFlipper = z
    }

    var stickerMatrix: Matrix?
        get() = mRotateMatrix
        set(matrix) {
            mRotateMatrix!!.set(matrix)
            notifyChange()
        }

    fun setNeedWidth(f: Float) {
        aspectRatio = 1.0f * f / waterBitmap!!.width.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawFilter = mDrawFilter
        val f: Float
        var sqrt: Float
        var i: Int
        if (stickerType == 2) {
            if (aspectRatio > f9199G) {
                f = f9199G / aspectRatio
                mRotateMatrix!!.postScale(f, f, mRoundCornerPos[8], mRoundCornerPos[9])
                aspectRatio = f9199G
            }
            mRotateMatrix!!.mapPoints(mRoundCornerPos, f9226c)
            sqrt = Math.sqrt(
                ((mRoundCornerPos[4] - mRoundCornerPos[2]) * (mRoundCornerPos[4] - mRoundCornerPos[2]) + (mRoundCornerPos[5] - mRoundCornerPos[3]) * (mRoundCornerPos[5] - mRoundCornerPos[3])).toDouble()
            ).toFloat()
            adjustGap(
                drawTextList!![0], Math.sqrt(
                    ((mRoundCornerPos[2] - mRoundCornerPos[0]) * (mRoundCornerPos[2] - mRoundCornerPos[0]) + (mRoundCornerPos[3] - mRoundCornerPos[1]) * (mRoundCornerPos[3] - mRoundCornerPos[1])).toDouble()
                ).toFloat() - (mTextPadding * 2).toFloat()
            )
            val fontMetrics = mTextPaint!!.fontMetrics
            val abs = Math.abs(fontMetrics.ascent).toInt()
            val fontHeight = abs + fontMetrics.descent.toInt()
            val size = (sqrt - (drawTextList!!.size * fontHeight).toFloat()).toInt() / 2
            mTextPaint!!.color = textColor
            canvas.save()
            canvas.rotate(Math.toDegrees(rotate).toFloat(), mRoundCornerPos[8], mRoundCornerPos[9])
            mMatrix!!.reset()
            mMatrix!!.postRotate(
                -Math.toDegrees(rotate).toFloat(),
                mRoundCornerPos[8],
                mRoundCornerPos[9]
            )
            mMatrix!!.mapPoints(mDrawTextPos, mRoundCornerPos)
            var lineNum = 0
            while (lineNum < drawTextList!!.size){
                canvas.drawText(
                    drawTextList!![lineNum]!!,
                    (mTextPadding.toFloat() + (mDrawTextPos?.get(0) ?: 0).toFloat()),
                    (size.toFloat() + (mDrawTextPos?.get(1) ?: 0).toFloat() + abs.toFloat() + (fontHeight * lineNum).toFloat()),
                    mTextPaint!!
                )
                lineNum++
            }
            i = 0
            while (true) {
                val lineNum = i
                if (lineNum >= drawTextList!!.size) {
                    break
                }
                canvas.drawText(
                    drawTextList!![lineNum]!!,
                    (mTextPadding.toFloat() + (mDrawTextPos?.get(0) ?: 0).toFloat()),
                    (size.toFloat() + (mDrawTextPos?.get(1) ?: 0).toFloat() + abs.toFloat() + (fontHeight * lineNum).toFloat()),
                    mTextPaint!!
                )
                i = lineNum + 1
            }
            canvas.restore()
        } else if (stickerType == 1) {
            mRotateMatrix!!.mapPoints(mRoundCornerPos, f9226c)
            if (waterBitmap != null) {
                mRotateMatrix!!.mapRect(f9229f, f9228e)
                if (isFlipper) {
                    canvas.save()
                    canvas.scale(-1.0f, 1.0f, mRoundCornerPos[8], mRoundCornerPos[9])
                    canvas.rotate(
                        (-2.0 * Math.toDegrees(rotate)).toFloat(),
                        mRoundCornerPos[8],
                        mRoundCornerPos[9]
                    )
                    canvas.drawBitmap(waterBitmap!!, mRotateMatrix!!, emptyPaint)
                    canvas.restore()
                } else {
                    canvas.drawBitmap(waterBitmap!!, mRotateMatrix!!, emptyPaint)
                }
            }
            if (isSupportInput) {
                mRotateMatrix!!.mapPoints(mTextOutBound, mOriginTextOutBound)
                mRotateMatrix!!.mapRect(mShowRect, mOriginShowRect)
                canvas.save()
                if (showTextDebug) {
                    canvas.drawLine(
                        mTextOutBound[0],
                        mTextOutBound[1],
                        mTextOutBound[2],
                        mTextOutBound[3],
                        mTextPaint!!
                    )
                    canvas.drawLine(
                        mTextOutBound[2],
                        mTextOutBound[3],
                        mTextOutBound[4],
                        mTextOutBound[5],
                        mTextPaint!!
                    )
                    canvas.drawLine(
                        mTextOutBound[4],
                        mTextOutBound[5],
                        mTextOutBound[6],
                        mTextOutBound[7],
                        mTextPaint!!
                    )
                    canvas.drawLine(
                        mTextOutBound[6],
                        mTextOutBound[7],
                        mTextOutBound[0],
                        mTextOutBound[1],
                        mTextPaint!!
                    )
                }
                var canvas2: Canvas
                if (text == null || text!!.length <= 0) {
                    mTextPaint!!.color = -65536
                    f = mTextPaint!!.measureText(mInputTips)
                    mTextPaint!!.style = Paint.Style.STROKE
                    mTextPaint!!.pathEffect = mDashPathEffect
                    canvas2 = canvas
                    canvas2.drawRect(
                        mShowRect!!.centerX() - 10.0f - f / 2.0f,
                        mShowRect!!.centerY() - 10.0f - mTextPaint!!.textSize / 2.0f,
                        mShowRect!!.centerX() + 10.0f + f / 2.0f,
                        mTextPaint!!.textSize / 2.0f + (mShowRect!!.centerY() + 10.0f),
                        mTextPaint!!
                    )
                    mTextPaint!!.style = Paint.Style.FILL
                    mTextPaint!!.pathEffect = null
                    canvas.drawText(
                        mInputTips!!,
                        mShowRect!!.centerX() - mTextWidth / 2.0f,
                        mShowRect!!.centerY() + mTextPaint!!.textSize / 2.0f - 4.0f,
                        mTextPaint!!
                    )
                } else {
                    mTextPaint!!.color = textColor
                    val textShowSize = minTextShowLength
                    val min = Math.min(
                        Math.abs(mTextOutBound[7] - mTextOutBound[3]), Math.abs(
                            mTextOutBound[5] - mTextOutBound[1]
                        )
                    )
                    if (showTextDebug) {
                        canvas2 = canvas
                        canvas2.drawLine(
                            mTextOutBound[8],
                            mTextOutBound[9] - min / 2.0f,
                            mTextOutBound[8],
                            min / 2.0f + mTextOutBound[9],
                            mTextPaint!!
                        )
                    }
                    val tan = Math.tan(rotate)
                    val textSize = 10.0f + mTextPaint!!.textSize
                    val round = Math.round(min / textSize) - 1
                    val a: List<String> = getMaxShowTextList(textShowSize, text, round)
                    if (round > 0) {
                        i = 0
                        while (true) {
                            val i4 = i
                            if (i4 >= round) {
                                break
                            }
                            var d: Double
                            var d2 =
                                ((-(round - 1) / 2 + i4).toFloat() * (textSize.toDouble() / tan).toFloat()).toDouble()
                            if (java.lang.Double.isNaN(d2)) {
                                d2 = 0.0
                            }
                            sqrt = d2.toFloat() + (mShowRect!!.centerX()
                                .toDouble() - textShowSize / 2.0).toFloat()
                            val centerX = d2.toFloat() + (mShowRect!!.centerX()
                                .toDouble() + textShowSize / 2.0).toFloat()
                            d =
                                if ((sqrt >= mTextOutBound[0] || sqrt >= mTextOutBound[2] || sqrt >= mTextOutBound[4] || sqrt >= mTextOutBound[6]) && (centerX <= mTextOutBound[0] || centerX <= mTextOutBound[2] || centerX <= mTextOutBound[4] || centerX <= mTextOutBound[6])) {
                                    d2
                                } else {
                                    0.0
                                }
                            if (showTextDebug) {
                                canvas2 = canvas
                                canvas2.drawLine(
                                    (mShowRect!!.centerX()
                                        .toDouble() - textShowSize / 2.0).toFloat() + d.toFloat(),
                                    (-(round - 1) / 2 + i4).toFloat() * textSize + mShowRect!!.centerY(),
                                    (mShowRect!!.centerX()
                                        .toDouble() + textShowSize / 2.0).toFloat() + d.toFloat(),
                                    (-(round - 1) / 2 + i4).toFloat() * textSize + mShowRect!!.centerY(),
                                    mTextPaint!!
                                )
                            }
                            val measureText = mTextPaint!!.measureText(a[i4])
                            canvas.drawText(
                                a[i4]!!,
                                d.toFloat() + mShowRect!!.centerX() - measureText / 2.0f,
                                mShowRect!!.centerY() + (-(round - 1) / 2 + i4).toFloat() * textSize + mTextPaint!!.textSize / 2.0f,
                                mTextPaint!!
                            )
                            i = i4 + 1
                        }
                    } else {
                        if (showTextDebug) {
                            canvas.drawLine(
                                (mTextOutBound[8]
                                    .toDouble() - textShowSize / 2.0).toFloat(),
                                mTextOutBound[9],
                                (mTextOutBound[8]
                                    .toDouble() + textShowSize / 2.0).toFloat(),
                                mTextOutBound[9],
                                mTextPaint!!
                            )
                        }
                        val str = a[0]
                        canvas.drawText(
                            str!!,
                            mTextOutBound[8] - mTextPaint!!.measureText(str) / 2.0f,
                            mTextOutBound[9] + mTextPaint!!.textSize / 2.0f,
                            mTextPaint!!
                        )
                    }
                }
                canvas.restore()
            }
        }
        if (isShowDrawController) {
            canvas.drawLine(
                mRoundCornerPos[0],
                mRoundCornerPos[1],
                mRoundCornerPos[2],
                mRoundCornerPos[3],
                mPaint!!
            )
            canvas.drawLine(
                mRoundCornerPos[2],
                mRoundCornerPos[3],
                mRoundCornerPos[4],
                mRoundCornerPos[5],
                mPaint!!
            )
            canvas.drawLine(
                mRoundCornerPos[4],
                mRoundCornerPos[5],
                mRoundCornerPos[6],
                mRoundCornerPos[7],
                mPaint!!
            )
            canvas.drawLine(
                mRoundCornerPos[6],
                mRoundCornerPos[7],
                mRoundCornerPos[0],
                mRoundCornerPos[1],
                mPaint!!
            )
            if (isFaceSticker) {
                canvas.drawBitmap(
                    mCloseBitmap!!,
                    mRoundCornerPos[2] - mCloseBitmapWidth / 2.0f,
                    mRoundCornerPos[3] - mCloseBitmapHeight / 2.0f,
                    mPaint
                )
            } else if (stickerType == 2) {
                canvas.drawBitmap(
                    mScaleBitmap!!,
                    mRoundCornerPos[4] - mScaleBitmapWidth / 2.0f,
                    mRoundCornerPos[5] - mScaleBitmapHeight / 2.0f,
                    mPaint
                )
                canvas.drawBitmap(
                    mCloseBitmap!!,
                    mRoundCornerPos[0] - mCloseBitmapWidth / 2.0f,
                    mRoundCornerPos[1] - mCloseBitmapHeight / 2.0f,
                    mPaint
                )
            } else {
                canvas.drawBitmap(
                    mScaleBitmap!!,
                    mRoundCornerPos[4] - mScaleBitmapWidth / 2.0f,
                    mRoundCornerPos[5] - mScaleBitmapHeight / 2.0f,
                    mPaint
                )
                canvas.drawBitmap(
                    mCloseBitmap!!,
                    mRoundCornerPos[2] - mCloseBitmapWidth / 2.0f,
                    mRoundCornerPos[3] - mCloseBitmapHeight / 2.0f,
                    mPaint
                )
                canvas.drawBitmap(
                    mRotateBitmap!!,
                    mRoundCornerPos[6] - mRotateBitmapWidth / 2.0f,
                    mRoundCornerPos[7] - mRotateBitmapHeight / 2.0f,
                    mPaint
                )
            }
        }
    }

    val bitmap: Bitmap
        get() {
            val createBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(createBitmap)
            val z = isShowDrawController
            isShowDrawController = false
            draw(canvas)
            isShowDrawController = z
            canvas.save()
            return createBitmap
        }

    fun setTouchable(z: Boolean) {
        mTouchable = z
    }

    fun setIsFaceSticker(z: Boolean) {
        isFaceSticker = z
    }

    private fun isScaleArea(x: Float, y: Float): Boolean {
        val f3 = mRoundCornerPos[4]
        val f4 = mRoundCornerPos[5]
        return RectF(
            f3 - mScaleBitmapWidth / 2.0f,
            f4 - mScaleBitmapHeight / 2.0f,
            f3 + mScaleBitmapWidth / 2.0f,
            f4 + mScaleBitmapHeight / 2.0f
        ).contains(x, y)
    }

    private fun isCloseArea(f: Float, f2: Float): Boolean {
        var i = 2
        if (stickerType == 2) {
            i = 0
        }
        val f3 = mRoundCornerPos[i]
        val f4 = mRoundCornerPos[i + 1]
        return RectF(
            f3 - mCloseBitmapWidth / 2.0f,
            f4 - mCloseBitmapHeight / 2.0f,
            f3 + mCloseBitmapWidth / 2.0f,
            f4 + mCloseBitmapHeight / 2.0f
        ).contains(f, f2)
    }

    private fun isRotateArea(f: Float, f2: Float): Boolean {
        val f3 = mRoundCornerPos[6]
        val f4 = mRoundCornerPos[7]
        return RectF(
            f3 - mRotateBitmapWidth / 2.0f,
            f4 - mRotateBitmapHeight / 2.0f,
            f3 + mRotateBitmapWidth / 2.0f,
            f4 + mRotateBitmapHeight / 2.0f
        ).contains(f, f2)
    }

    private fun adjustGap(str: String?, f: Float) {
        val textSize = mTextPaint!!.textSize
        val measureText = (f - mTextPaint!!.measureText(str)).toInt() / str!!.length
        if (measureText != 0) {
            mTextPaint!!.textSize = textSize + measureText.toFloat()
        }
    }

    private fun isOccurArea(point: PointF, pointFArr: Array<PointF>): Boolean {
        var i = 0
        for (i2 in pointFArr.indices) {
            val point1 = pointFArr[i2]
            val point2 = pointFArr[(i2 + 1) % pointFArr.size]
            if (point1.y != point2.y && point.y >= Math.min(
                    point1.y,
                    point2.y
                ) && point.y < Math.max(point1.y, point2.y)
            ) {
                if (point1.x.toDouble() + (point.y - point1.y).toDouble() * (point2.x - point1.x).toDouble() / (point2.y - point1.y).toDouble() > point.x.toDouble()) {
                    i++
                }
            }
        }
        return i % 2 == 1
    }

    fun showViewController() {
        bringToFront()
        val viewGroup = parent as ViewGroup
        for (i in 0 until viewGroup.childCount) {
            val childAt = viewGroup.getChildAt(i)
            if (childAt is StickerView) {
                childAt.isShowDrawController = false
            }
        }
        isShowDrawController = true
        invalidate()
    }

    val vertexCoordinate: Array<Point>
        get() {
            var i: Int
            val point = Point(Int.MAX_VALUE, Int.MAX_VALUE)
            val point2 = Point(Int.MIN_VALUE, Int.MIN_VALUE)
            i = 0
            while (i < mRoundCornerPos.size) {
                if (point.x.toFloat() > mRoundCornerPos[i]) {
                    point.x = mRoundCornerPos[i].toInt()
                }
                if (point.y.toFloat() > mRoundCornerPos[i + 1]) {
                    point.y = mRoundCornerPos[i + 1].toInt()
                }
                if (point2.x.toFloat() < mRoundCornerPos[i]) {
                    point2.x = mRoundCornerPos[i].toInt()
                }
                if (point2.y.toFloat() < mRoundCornerPos[i + 1]) {
                    point2.y = mRoundCornerPos[i + 1].toInt()
                }
                i += 2
            }
            point.x = if (point.x > 0) point.x else 0
            i = if (point.y > 0) {
                point.y
            } else {
                0
            }
            point.y = i
            i = width
            val height = height
            if (point2.x <= i) {
                i = point2.x
            }
            point2.x = i
            point2.y = if (point2.y > height) height else point2.y
            return arrayOf(point, point2)
        }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            originRectF = RectF(0.0f, 0.0f, measuredWidth.toFloat(), measuredHeight.toFloat())
        }
    }

    override fun dispatchTouchEvent(motionEvent: MotionEvent): Boolean {
        var isDispatch = false
        if (mTouchable) {
            if (originRectF == null) {
                originRectF = RectF(0.0f, 0.0f, measuredWidth.toFloat(), measuredHeight.toFloat())
            }
            val x = motionEvent.x
            val y = motionEvent.y
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> run {
                    if (!isScaleArea(x, y) || isFaceSticker) {
                        var z2: Boolean
                        if (!isRotateArea(x, y) || isFaceSticker) {
                            if (!isCloseArea(x, y)) {
                                if (isOccurArea(
                                        PointF(x, y), arrayOf(
                                            PointF(mRoundCornerPos[0], mRoundCornerPos[1]),
                                            PointF(mRoundCornerPos[2], mRoundCornerPos[3]),
                                            PointF(mRoundCornerPos[4], mRoundCornerPos[5]),
                                            PointF(mRoundCornerPos[6], mRoundCornerPos[7]))
                                    )
                                ) {
                                    mDownY = y
                                    mDownx = x
                                    f9247x = true
                                    f9213U = true
                                    showViewController()
                                }
                                z2 = f9246w || f9248y || f9247x || f9249z
                                if (!z2) {
                                    isShowDrawController = false
                                    invalidate()
                                    return@run
                                }
                            }else{
                                f9248y = true
                                return@run
                            }
                        }else{
                            f9249z = true
                            z2 = !isFlipper
                            isFlipper = z2
                            invalidate()
                            showViewController()
                            return@run
                        }
                    }else{
                        f9246w = true
                        mDownY = y
                        mDownx = x
                        showViewController()
                    }
                }
                MotionEvent.ACTION_UP -> run {
                    if (!isCloseArea(x, y) || !f9248y) {
                        if (f9213U) {
                            f9213U = false
                            if (mOnStickerClickListener != null) {
                                mOnStickerClickListener!!.onClick()
                            }
                        }
                        mDownY = 0.0f
                        mDownx = 0.0f
                        f9246w = false
                        f9248y = false
                        f9247x = false
                        f9249z = false
                    }else{
                        removeView()
                        mDownY = 0.0f
                        mDownx = 0.0f
                        f9246w = false
                        f9248y = false
                        f9247x = false
                        f9249z = false
                    }

                }
                MotionEvent.ACTION_MOVE -> run {
                    var f: Float
                    var f2: Float
                    if (!f9246w || isFaceSticker) {
                        if (f9247x && !isFaceSticker) {
                            f = x - mDownx
                            f2 = y - mDownY
                            f9246w = false
                            val sqrt = sqrt((f * f + f2 * f2).toDouble())
                                .toFloat()
                            if (sqrt > 10.0f) {
                                f9213U = false
                            }
                            if (sqrt > 2.0f && m11862f(f, f2)) {
                                mRotateMatrix!!.postTranslate(f, f2)
                                postInvalidate()
                                mDownx = x
                                mDownY = y
                                return@run
                            }
                        }else{
                            return true
                        }
                    }else{
                        val h = m11865h()
                        if (h > f9201I && h > 85.0 && h < 90.0) {
                            mRotateMatrix!!.postRotate(
                                (90.0 - h).toFloat(),
                                mRoundCornerPos[8],
                                mRoundCornerPos[9]
                            )
                        } else if (h < f9201I && h > 0.0 && h < 5.0) {
                            mRotateMatrix!!.postRotate(
                                (-h).toFloat(),
                                mRoundCornerPos[8],
                                mRoundCornerPos[9]
                            )
                        } else if (h < f9201I && h > -90.0 && h < -85.0) {
                            mRotateMatrix!!.postRotate(
                                (-90.0 - h).toFloat(),
                                mRoundCornerPos[8],
                                mRoundCornerPos[9]
                            )
                        } else if (h <= f9201I || h >= 0.0 || h <= -5.0) {
                            mRotateMatrix!!.postRotate(
                                m11854a(motionEvent),
                                mRoundCornerPos[8],
                                mRoundCornerPos[9]
                            )
                        } else {
                            mRotateMatrix!!.postRotate(
                                (-h).toFloat(),
                                mRoundCornerPos[8],
                                mRoundCornerPos[9]
                            )
                        }
                        f9201I = h
                        f = calLineDistance(mRoundCornerPos[0], mRoundCornerPos[1])
                        f2 = calLineDistance(motionEvent.x, motionEvent.y)
                        if (sqrt(((f - f2) * (f - f2)).toDouble()) > 0.0) {
                            f = f2 / f
                            f2 = aspectRatio * f
                            if (f2 >= f9200H && f2 <= f9199G) {
                                mRotateMatrix!!.postScale(f, f, mRoundCornerPos[8], mRoundCornerPos[9])
                                aspectRatio = f2
                            }
                        }
                        invalidate()
                        mDownx = x
                        mDownY = y
                    }
                }
                MotionEvent.ACTION_CANCEL -> {
                    mDownY = 0.0f
                    mDownx = 0.0f
                    f9246w = false
                    f9248y = false
                    f9247x = false
                    f9249z = false
                }
            }
            notifyChange()
        }
        if (f9246w || f9248y || f9247x || f9249z) {
            isDispatch = true
        }
        return isDispatch
    }

    private fun m11865h(): Double {
        return Math.toDegrees(rotate) % 90.0
    }

    private fun removeView() {
        (parent as ViewGroup).removeView(this)
        if (mOnStickerDeleteListener != null) {
            mOnStickerDeleteListener!!.onClick(tag)
        }
    }

    private fun m11862f(f: Float, f2: Float): Boolean {
        return originRectF!!.contains(mRoundCornerPos[8] + f, mRoundCornerPos[9] + f2)
    }

    private fun calLineDistance(f: Float, f2: Float): Float {
        val f3 = f - mRoundCornerPos[8]
        val f4 = f2 - mRoundCornerPos[9]
        return sqrt((f3 * f3 + f4 * f4).toDouble()).toFloat()
    }

    private fun m11854a(motionEvent: MotionEvent): Float {
        return m11866h(motionEvent.x, motionEvent.y) - m11866h(mDownx, mDownY)
    }

    private fun m11866h(f: Float, f2: Float): Float {
        return Math.toDegrees(
            Math.atan2(
                (f2 - mRoundCornerPos[9]).toDouble(),
                (f - mRoundCornerPos[8]).toDouble()
            )
        ).toFloat()
    }

    fun setOnStickerClickListener(onStickerClickListener: OnStickerClickListener?) {
        mOnStickerClickListener = onStickerClickListener
    }

    fun setOnStickerDeleteListener(onStickerDeleteListener: OnStickerDeleteListener?) {
        mOnStickerDeleteListener = onStickerDeleteListener
    }

    fun setOnStickerChangedListener(onStickerChangedListener: OnStickerChangedListener?) {
        mOnStickerChangedListener = onStickerChangedListener
    }

    init {
        textSize = 17.0f.px.toInt()
        textPaintSize = 5.0f.px.toInt()
        isShowDrawController = true
        aspectRatio = 1.0f
        f9199G = 5.0f
        f9200H = 0.2f
        mDashPathEffect = DashPathEffect(floatArrayOf(4.0f, 4.0f, 4.0f, 4.0f), 1.0f)
        textColor = -1
        mTouchable = true
        stickerType = 1
        mDrawFilter = PaintFlagsDrawFilter(0, 3)
        init()
    }
}