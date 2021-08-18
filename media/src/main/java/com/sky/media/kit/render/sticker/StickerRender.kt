package com.sky.media.kit.render.sticker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import android.opengl.GLES20
import android.util.Pair
import com.sky.media.image.core.base.BaseRender
import com.sky.media.image.core.base.TextureOutRender
import com.sky.media.image.core.cache.IBitmapCache
import com.sky.media.image.core.util.TextureBindUtil
import com.sky.media.kit.face.Face
import com.sky.media.kit.render.sticker.trigger.OnTriggerStartListener
import com.sky.media.kit.render.sticker.trigger.TriggerActionFactory
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.sqrt

class StickerRender(context: Context?, iBitmapCache: IBitmapCache?) : BaseRender() {
    private var mFaces: Array<Face?>? = null
    private var mSticker: Sticker? = null
    private val mStickerRenderHelper: StickerRenderHelper =
        StickerRenderHelper(context, iBitmapCache)
    private var mTexture = 0
    private var mTriggerStartListener: OnTriggerStartListener? = null
    var sticker: Sticker?
        get() = mSticker
        set(sticker) {
            mSticker = sticker
            mStickerRenderHelper.sticker = sticker
        }

    fun setOnTriggerStartListener(onTriggerStartListener: OnTriggerStartListener?) {
        mTriggerStartListener = onTriggerStartListener
    }

    override fun destroy() {
        super.destroy()
        if (mTexture != 0) {
            GLES20.glDeleteTextures(1, intArrayOf(mTexture), 0)
            mTexture = 0
        }
        mStickerRenderHelper.reset()
    }

    fun setFaces(faceArr: Array<Face?>?) {
        if (mSticker!!.face_count <= 0 || faceArr == null) {
            mFaces = faceArr
            return
        }
        val min = faceArr.size.coerceAtMost(mSticker!!.face_count)
        mFaces = arrayOfNulls(min)
        System.arraycopy(faceArr, 0, mFaces, 0, min)
    }

    override fun dealNextTexture(
        textureOutRender: TextureOutRender,
        textureId: Int,
        needDraw: Boolean
    ) {
        if (needDraw) {
            markNeedDraw()
        }
        texture_in = textureId
        setWidth(textureOutRender.getWidth())
        setHeight(textureOutRender.getHeight())
        onDrawFrame()
    }

    override fun afterDrawFrame() {
        if (mFaces == null) {
            mFaces = arrayOfNulls(0)
        }
        val stickerResource: Map<Pair<Sticker.Component, Face?>, Bitmap>? = mStickerRenderHelper.getStickerResource(
            mFaces
        )
        if (stickerResource != null) {
            GLES20.glEnable(GLES20.GL_BLEND)
            GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA)
            for ((key, value) in stickerResource) {
                val pair = key as Pair<Sticker.Component, Face?>
                val component = pair.first as Sticker.Component
                val bitmap = value as Bitmap
                if (1 == component.type) {
                    val z3 = component.trigger == 1

                    drawSticker(
                        component,
                        bitmap,
                        arrayOf(calculateVerticesScreen(component, bitmap), textureVertices[2]!!),
                        z3,
                        null
                    )
                }
            }
            GLES20.glDisable(GLES20.GL_BLEND)
        }
    }

    private fun drawSticker(
        component: Sticker.Component,
        bitmap: Bitmap,
        floatBufferArr: Array<FloatBuffer>,
        z: Boolean,
        face: Face?
    ) {
        val createTriggerAction = TriggerActionFactory.createTriggerAction(component.action)
        if (createTriggerAction == null) {
            drawSticker(floatBufferArr, bitmap)
        } else if (createTriggerAction.check(
                Pair<Sticker.Component, Face>(component, face),
                z,
                mStickerRenderHelper,
                mTriggerStartListener
            )
        ) {
            drawSticker(floatBufferArr, bitmap)
        }
    }

    private fun calculateVerticesScreen(component: Sticker.Component, bitmap: Bitmap): FloatBuffer {
        var pointF: PointF
        var pointF2: PointF
        val fArr = FloatArray(8)
        var width = component.scale * bitmap.width.toFloat()
        var height = bitmap.height.toFloat() * component.scale
        val f = width / height
        val top = component.scale * component.top.toFloat()
        val right = component.scale * component.right.toFloat()
        val left = component.scale * component.left.toFloat()
        val bottom = component.scale * component.bottom.toFloat()
        when (component.full) {
            1 -> {
                width = getWidth().toFloat()
                height = width / f
            }
            2 -> {
                height = getHeight().toFloat()
                width = height * f
            }
            3 -> {
                if (f <= getWidth().toFloat() * 1.0f / getHeight().toFloat()) {
                    width = getWidth().toFloat()
                    height = width / f
                }else{
                    height = getHeight().toFloat()
                    width = height * f
                }
            }
        }
        val pointF3 = PointF(0.0f, 0.0f)
        var pointF4 = PointF(0.0f, 0.0f)
        var pointF5 = PointF(0.0f, 0.0f)
        var pointF6 = PointF(0.0f, 0.0f)
        val pointF7: PointF
        when (component.anchor) {
            0 -> {
                pointF4 = PointF(left, getHeight().toFloat() - top)
                pointF6 = PointF(pointF4.x + width, pointF4.y)
                pointF5 = PointF(pointF4.x, pointF4.y - height)
                pointF = PointF(width + pointF4.x, pointF5.y)
                pointF2 = pointF6
                pointF6 = pointF5
                pointF5 = pointF4
            }
            1 -> {
                pointF6 = PointF(getWidth().toFloat() - right, getHeight().toFloat() - top)
                pointF5 = PointF(pointF6.x - width, pointF6.y)
                pointF2 = PointF(pointF5.x, pointF5.y - height)
                pointF = PointF(pointF6.x, pointF2.y)
                pointF7 = pointF6
                pointF6 = pointF2
                pointF2 = pointF7
            }
            2 -> {
                pointF5 = PointF(left, bottom)
                pointF6 = PointF(width + pointF5.x, pointF5.y)
                pointF2 = PointF(pointF6.x, height + pointF6.y)
                pointF7 = pointF6
                pointF6 = pointF5
                pointF5 = PointF(pointF5.x, pointF2.y)
                pointF = pointF7
            }
            3 -> {
                pointF6 = PointF(getWidth().toFloat() - right, bottom)
                pointF5 = PointF(pointF6.x - width, pointF6.y)
                pointF2 = PointF(pointF5.x, height + pointF5.y)
                pointF7 = pointF6
                pointF6 = pointF5
                pointF5 = pointF2
                pointF2 = PointF(pointF7.x, pointF2.y)
                pointF = pointF7
            }
            4 -> {
                pointF5 = PointF(
                    (getWidth() / 2).toFloat() - width / 2.0f + left,
                    (getHeight() / 2).toFloat() + height / 2.0f - top
                )
                pointF6 = PointF(width + pointF5.x, pointF5.y)
                pointF2 = PointF(pointF5.x, pointF5.y - height)
                pointF = PointF(pointF6.x, pointF2.y)
                pointF7 = pointF6
                pointF6 = pointF2
                pointF2 = pointF7
            }
            5 -> {
                pointF5 = PointF(
                    (getWidth() / 2).toFloat() - width / 2.0f + left,
                    getHeight().toFloat() - top
                )
                pointF6 = PointF(width + pointF5.x, pointF5.y)
                pointF2 = PointF(pointF5.x, pointF5.y - height)
                pointF = PointF(pointF6.x, pointF2.y)
                pointF7 = pointF6
                pointF6 = pointF2
                pointF2 = pointF7
            }
            6 -> {
                pointF6 = PointF(
                    getWidth().toFloat() - right,
                    (getHeight() / 2).toFloat() + height / 2.0f - top
                )
                pointF5 = PointF(pointF6.x - width, pointF6.y)
                pointF2 = PointF(pointF5.x, pointF5.y - height)
                pointF = PointF(pointF6.x, pointF2.y)
                pointF7 = pointF6
                pointF6 = pointF2
                pointF2 = pointF7
            }
            7 -> {
                pointF5 = PointF((getWidth() / 2).toFloat() - width / 2.0f + left, bottom)
                pointF6 = PointF(width + pointF5.x, pointF5.y)
                pointF2 = PointF(pointF5.x, height + pointF5.y)
                pointF7 = pointF6
                pointF6 = pointF5
                pointF5 = pointF2
                pointF2 = PointF(pointF6.x, pointF2.y)
                pointF = pointF7
            }
            8 -> {
                pointF4 = PointF(left, (getHeight() / 2).toFloat() + height / 2.0f - top)
                pointF6 = PointF(pointF4.x + width, pointF4.y)
                pointF5 = PointF(pointF4.x, pointF4.y - height)
                pointF = PointF(width + pointF4.x, pointF5.y)
                pointF2 = pointF6
                pointF6 = pointF5
                pointF5 = pointF4
            }
            else -> {
                pointF = pointF6
                pointF2 = pointF5
                pointF6 = pointF4
                pointF5 = pointF3
            }
        }
        pointF5 = transVerticesToOpenGL(pointF5, getWidth().toFloat(), getHeight().toFloat())
        pointF6 = transVerticesToOpenGL(pointF6, getWidth().toFloat(), getHeight().toFloat())
        pointF2 = transVerticesToOpenGL(pointF2, getWidth().toFloat(), getHeight().toFloat())
        pointF = transVerticesToOpenGL(pointF, getWidth().toFloat(), getHeight().toFloat())
        fArr[0] = pointF.x
        fArr[1] = pointF.y
        fArr[2] = pointF6.x
        fArr[3] = pointF6.y
        fArr[4] = pointF2.x
        fArr[5] = pointF2.y
        fArr[6] = pointF5.x
        fArr[7] = pointF5.y

        val put =
            ByteBuffer.allocateDirect(fArr.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
                .put(fArr)
        put.position(0)
        return put
    }

    private fun drawSticker(floatBufferArr: Array<FloatBuffer>, bitmap: Bitmap?) {
        floatBufferArr[0].position(0)
        floatBufferArr[1].position(0)
        if (mTexture != 0) {
            GLES20.glDeleteTextures(1, intArrayOf(mTexture), 0)
            mTexture = 0
        }
        if (!(bitmap == null || bitmap.isRecycled)) {
            mTexture = TextureBindUtil.bindBitmap(bitmap)
        }
        GLES20.glActiveTexture(GLES20.GL_TEXTURE2)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture)
        GLES20.glUniform1i(textureHandle, 2)
        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, floatBufferArr[0])
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, floatBufferArr[1])
        if (floatBufferArr[0].limit() > 8) {
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, floatBufferArr[0].limit() / 2)
        } else {
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
    }

    private fun getRotateVertices(pointF: PointF, pointF2: PointF, d: Double): PointF {
        return PointF(
            ((pointF.x - pointF2.x).toDouble() * Math.cos(d) - (pointF.y - pointF2.y).toDouble() * Math.sin(
                d
            ) + pointF2.x.toDouble()).toFloat(),
            ((pointF.x - pointF2.x).toDouble() * Math.sin(d) + (pointF.y - pointF2.y).toDouble() * Math.cos(
                d
            ) + pointF2.y.toDouble()).toFloat()
        )
    }

    private fun transVerticesToOpenGL(pointF: PointF, f: Float, f2: Float): PointF {
        return PointF((pointF.x - f / 2.0f) / (f / 2.0f), (pointF.y - f2 / 2.0f) / (f2 / 2.0f))
    }

    private fun distanceOf(pointF: PointF, pointF2: PointF): Float {
        return sqrt(((pointF.x - pointF2.x) * (pointF.x - pointF2.x) + (pointF.y - pointF2.y) * (pointF.y - pointF2.y)).toDouble())
            .toFloat()
    }

}