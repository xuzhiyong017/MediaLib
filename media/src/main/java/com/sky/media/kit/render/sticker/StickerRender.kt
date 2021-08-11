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
import com.sky.media.kit.render.sticker.Sticker.FacePoint
import com.sky.media.kit.render.sticker.trigger.OnTriggerStartListener
import com.sky.media.kit.render.sticker.trigger.TriggerActionFactory
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.util.*

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

    fun newTextureReady(i: Int, gLTextureOutputRenderer: TextureOutRender?, z: Boolean) {
        if (z) {
            markNeedDraw()
        }
        texture_in = i
        onDrawFrame()
    }

    override fun afterDrawFrame() {
        if (mFaces == null) {
            mFaces = arrayOfNulls(0)
        }
        val stickerResource: Map<Pair<Sticker.Component, Face>, Bitmap>? = mStickerRenderHelper.getStickerResource(
            mFaces
        )
        if (stickerResource != null) {
            GLES20.glEnable(3042)
            GLES20.glBlendFunc(1, 771)
            for ((key, value) in stickerResource) {
                val pair = key as Pair<Sticker.Component, Face>
                val component = pair.first as Sticker.Component
                val face = pair.second as Face
                if (!(component.face_index == -1 || component.type == 1)) {
                    var i = 0
                    while (i < mFaces!!.size) {
                        if (mFaces!![i] == face) {
                            break
                        }
                        i++
                    }
                    i = -1
                    if (component.face_index != i) {
                    }
                }
                val bitmap = value as Bitmap
                var facePoint: FacePoint
                var facePoint2: FacePoint
                var facePoint3: FacePoint
                var width: Int
                var f: Float
                var pointF: PointF
                var pointF2: PointF
                var pointFArr: Array<PointF>
                var z: Boolean
                if (component.type == 0) {
                    facePoint = component.faces[0] as FacePoint
                    facePoint2 = component.faces[1] as FacePoint
                    if (facePoint.x <= facePoint2.x) {
                        facePoint3 = facePoint2
                        facePoint2 = facePoint
                        facePoint = facePoint3
                    }
                    width = bitmap.width
                    f = width.toFloat() * 1.0f / component.width.toFloat()
                    pointF = PointF(facePoint2.x.toFloat() * f, facePoint2.y.toFloat() * f)
                    pointF2 = PointF(facePoint.x.toFloat() * f, f * facePoint.y.toFloat())
                    pointFArr = face.mFacePointArray
                    val calculateVerticesTraceFace = calculateVerticesTraceFace(
                        face,
                        pointFArr[FaceConst.sMirrorPointsMap[facePoint2.id]],
                        pointFArr[FaceConst.sMirrorPointsMap[facePoint.id]],
                        pointF,
                        pointF2,
                        component.scale * width.toFloat(),
                        component.scale * bitmap.height
                            .toFloat()
                    )
                    z = face.mFaceAction and component.trigger != 0 || component.trigger == 1
                    drawSticker(
                        component,
                        bitmap,
                        arrayOf(calculateVerticesTraceFace, textureVertices[2]!!),
                        z,
                        face
                    )
                } else if (1 == component.type) {
                    var z2: Boolean
                    val z3 = component.trigger == 1
                    for (face2 in mFaces!!) {
                        if (face2!!.mFaceAction and component.trigger != 0) {
                            z2 = true
                            break
                        }
                    }
                    z2 = z3
                    drawSticker(
                        component,
                        bitmap,
                        arrayOf(calculateVerticesScreen(component, bitmap), textureVertices[2]!!),
                        z2,
                        null
                    )
                } else if (2 == component.type) {
                    facePoint = component.faces[0] as FacePoint
                    facePoint2 = component.faces[1] as FacePoint
                    if (facePoint.x <= facePoint2.x) {
                        facePoint3 = facePoint2
                        facePoint2 = facePoint
                        facePoint = facePoint3
                    }
                    width = bitmap.width
                    f = width.toFloat() * 1.0f / component.width.toFloat()
                    pointF = PointF(facePoint2.x.toFloat() * f, facePoint2.y.toFloat() * f)
                    pointF2 = PointF(facePoint.x.toFloat() * f, f * facePoint.y.toFloat())
                    pointFArr = face.mFacePointArray
                    val calculateVerticesMakeupTraceFace = calculateVerticesMakeupTraceFace(
                        face,
                        pointFArr[FaceConst.sMirrorPointsMap[facePoint2.id]],
                        pointFArr[FaceConst.sMirrorPointsMap[facePoint.id]],
                        pointF,
                        pointF2,
                        component.scale * width.toFloat(),
                        component.scale * bitmap.height
                            .toFloat()
                    )
                    z = face.mFaceAction and component.trigger != 0 || component.trigger == 1
                    drawSticker(component, bitmap, calculateVerticesMakeupTraceFace, z, face)
                }
            }
            GLES20.glDisable(3042)
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

    private fun calculateVerticesMakeupTraceFace(
        face: Face,
        pointF: PointF,
        pointF2: PointF,
        pointF3: PointF,
        pointF4: PointF,
        f: Float,
        f2: Float
    ): Array<FloatBuffer> {
        var acos: Double
        var i: Int
        var pointF5 = PointF(pointF.x, pointF.y)
        var pointF6 = PointF(pointF2.x, pointF2.y)
        var distanceOf = distanceOf(pointF5, pointF6) / distanceOf(pointF3, pointF4)
        pointF3.x *= distanceOf
        pointF3.y *= distanceOf
        pointF4.x *= distanceOf
        pointF4.y *= distanceOf
        var f3 = f * distanceOf
        distanceOf *= f2
        var pointF7 = PointF(pointF5.x - pointF3.x, pointF5.y + pointF3.y)
        var pointF8 = PointF(pointF7.x, pointF7.y - distanceOf)
        val pointF9 = PointF(pointF7.x + f3, pointF7.y)
        val pointF10 = PointF(pointF9.x, pointF8.y)
        if (face.mRoll == 2.14748365E9f) {
            val pointF11 = PointF(pointF7.x + pointF4.x, pointF7.y - pointF4.y)
            distanceOf = distanceOf(pointF5, pointF11)
            f3 = distanceOf(pointF5, pointF6)
            val distanceOf2 = distanceOf(pointF11, pointF6)
            acos =
                Math.acos(((distanceOf * distanceOf + f3 * f3 - distanceOf2 * distanceOf2) / (distanceOf * 2.0f * f3)).toDouble())
            if (pointF6.x < pointF11.x && pointF6.y < 2.0f * pointF5.y - pointF11.y) {
                acos = -acos
            }
        } else {
            acos = (180.0 - face.mRoll.toDouble()) / 180.0 * 3.14
        }
        pointF6 = getRotateVertices(pointF7, pointF5, acos)
        pointF7 = getRotateVertices(pointF8, pointF5, acos)
        pointF8 = getRotateVertices(pointF9, pointF5, acos)
        var rotateVertices = getRotateVertices(pointF10, pointF5, acos)
        val transVerticesToOpenGL =
            transVerticesToOpenGL(pointF6, getWidth().toFloat(), getHeight().toFloat())
        pointF5 = transVerticesToOpenGL(pointF7, getWidth().toFloat(), getHeight().toFloat())
        pointF6 = transVerticesToOpenGL(pointF8, getWidth().toFloat(), getHeight().toFloat())
        pointF7 = transVerticesToOpenGL(rotateVertices, getWidth().toFloat(), getHeight().toFloat())
        val fArr = FloatArray(FaceConst.sVertexIndices114.size * 2)
        val fArr2 = FloatArray(FaceConst.sVertexIndices114.size * 2)
        val fArr3 = FloatArray(FaceConst.sTextureCoordinate114.size * 2)
        i = 0
        while (i < FaceConst.sTextureCoordinate114.size) {
            fArr3[i] = FaceConst.sTextureCoordinate114[i]
            fArr3[i + 1] = FaceConst.sTextureCoordinate114[i + 1]
            i += 2
        }
        val arrayList: MutableList<PointF> = ArrayList<PointF>()
        i = 0
        while (i < face.mFacePointArray.size) {
            arrayList.add(
                transVerticesToOpenGL(
                    face.mFacePointArray[FaceConst.sMirrorPointsMap[i]],
                    getWidth().toFloat(),
                    getHeight().toFloat()
                )
            )
            i++
        }
        rotateVertices = PointF(
            transVerticesToOpenGL.x / 2.0f + pointF6.x / 2.0f,
            transVerticesToOpenGL.y / 2.0f + pointF6.y / 2.0f
        )
        val pointF12 = PointF(
            pointF5.x / 2.0f + transVerticesToOpenGL.x / 2.0f,
            pointF5.y / 2.0f + transVerticesToOpenGL.y / 2.0f
        )
        val pointF13 =
            PointF(pointF5.x / 2.0f + pointF7.x / 2.0f, pointF5.y / 2.0f + pointF7.y / 2.0f)
        val pointF14 =
            PointF(pointF7.x / 2.0f + pointF6.x / 2.0f, pointF7.y / 2.0f + pointF6.y / 2.0f)
        arrayList.add(transVerticesToOpenGL)
        arrayList.add(pointF12)
        arrayList.add(pointF5)
        arrayList.add(pointF13)
        arrayList.add(pointF7)
        arrayList.add(pointF14)
        arrayList.add(pointF6)
        arrayList.add(rotateVertices)
        i = 0
        while (true) {
            val i2 = i
            if (i2 < FaceConst.sVertexIndices114.size) {
                val i3 = FaceConst.sVertexIndices114[i2]
                rotateVertices = arrayList[i3] as PointF
                fArr[i2 * 2] = rotateVertices.x
                fArr[i2 * 2 + 1] = rotateVertices.y
                fArr2[i2 * 2] = fArr3[i3 * 2]
                fArr2[i2 * 2 + 1] = fArr3[i3 * 2 + 1]
                i = i2 + 1
            } else {
                val r4 = ByteBuffer.allocateDirect(fArr.size * 4).order(ByteOrder.nativeOrder())
                    .asFloatBuffer().put(fArr)
                r4.position(0)
                val r5 = ByteBuffer.allocateDirect(fArr2.size * 4).order(ByteOrder.nativeOrder())
                    .asFloatBuffer().put(fArr2)
                r5.position(0)
                return arrayOf(r4, r5)
            }
        }
    }

    private fun calculateVerticesTraceFace(
        face: Face,
        pointF: PointF,
        pointF2: PointF,
        pointF3: PointF,
        pointF4: PointF,
        f: Float,
        f2: Float
    ): FloatBuffer {
        var acos: Double
        var pointF5 = PointF(pointF.x, pointF.y)
        var pointF6 = PointF(pointF2.x, pointF2.y)
        var distanceOf = distanceOf(pointF5, pointF6) / distanceOf(pointF3, pointF4)
        pointF3.x *= distanceOf
        pointF3.y *= distanceOf
        pointF4.x *= distanceOf
        pointF4.y *= distanceOf
        var f3 = f * distanceOf
        distanceOf *= f2
        var pointF7 = PointF(pointF5.x - pointF3.x, pointF5.y + pointF3.y)
        var pointF8 = PointF(pointF7.x, pointF7.y - distanceOf)
        val pointF9 = PointF(pointF7.x + f3, pointF7.y)
        val pointF10 = PointF(pointF9.x, pointF8.y)
        if (face.mRoll == 2.14748365E9f) {
            val pointF11 = PointF(pointF7.x + pointF4.x, pointF7.y - pointF4.y)
            distanceOf = distanceOf(pointF5, pointF11)
            f3 = distanceOf(pointF5, pointF6)
            val distanceOf2 = distanceOf(pointF11, pointF6)
            acos =
                Math.acos(((distanceOf * distanceOf + f3 * f3 - distanceOf2 * distanceOf2) / (distanceOf * 2.0f * f3)).toDouble())
            if (pointF6.x < pointF11.x && pointF6.y < 2.0f * pointF5.y - pointF11.y) {
                acos = -acos
            }
        } else {
            acos = (180.0 - face.mRoll.toDouble()) / 180.0 * 3.14
        }
        pointF6 = getRotateVertices(pointF7, pointF5, acos)
        pointF7 = getRotateVertices(pointF8, pointF5, acos)
        pointF8 = getRotateVertices(pointF9, pointF5, acos)
        var rotateVertices = getRotateVertices(pointF10, pointF5, acos)
        val transVerticesToOpenGL =
            transVerticesToOpenGL(pointF6, getWidth().toFloat(), getHeight().toFloat())
        pointF5 = transVerticesToOpenGL(pointF7, getWidth().toFloat(), getHeight().toFloat())
        pointF6 = transVerticesToOpenGL(pointF8, getWidth().toFloat(), getHeight().toFloat())
        rotateVertices =
            transVerticesToOpenGL(rotateVertices, getWidth().toFloat(), getHeight().toFloat())
        val fArr = floatArrayOf(
            rotateVertices.x,
            rotateVertices.y,
            pointF5.x,
            pointF5.y,
            pointF6.x,
            pointF6.y,
            transVerticesToOpenGL.x,
            transVerticesToOpenGL.y
        )
        val put =
            ByteBuffer.allocateDirect(fArr.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
                .put(fArr)
        put.position(0)
        return put
    }

    private fun calculateVerticesScreen(component: Sticker.Component, bitmap: Bitmap): FloatBuffer {
        var pointF: PointF
        var pointF2: PointF
        val fArr = FloatArray(8)
        var width = component.scale * bitmap.width.toFloat()
        var height = bitmap.height.toFloat() * component.scale
        val f = width / height
        val f2 = component.scale * component.top.toFloat()
        val f3 = component.scale * component.right.toFloat()
        val f4 = component.scale * component.left.toFloat()
        val f5 = component.scale * component.bottom.toFloat()
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
                pointF4 = PointF(f4, getHeight().toFloat() - f2)
                pointF6 = PointF(pointF4.x + width, pointF4.y)
                pointF5 = PointF(pointF4.x, pointF4.y - height)
                pointF = PointF(width + pointF4.x, pointF5.y)
                pointF2 = pointF6
                pointF6 = pointF5
                pointF5 = pointF4
            }
            1 -> {
                pointF6 = PointF(getWidth().toFloat() - f3, getHeight().toFloat() - f2)
                pointF5 = PointF(pointF6.x - width, pointF6.y)
                pointF2 = PointF(pointF5.x, pointF5.y - height)
                pointF = PointF(pointF6.x, pointF2.y)
                pointF7 = pointF6
                pointF6 = pointF2
                pointF2 = pointF7
            }
            2 -> {
                pointF5 = PointF(f4, f5)
                pointF6 = PointF(width + pointF5.x, pointF5.y)
                pointF2 = PointF(pointF6.x, height + pointF6.y)
                pointF7 = pointF6
                pointF6 = pointF5
                pointF5 = PointF(pointF5.x, pointF2.y)
                pointF = pointF7
            }
            3 -> {
                pointF6 = PointF(getWidth().toFloat() - f3, f5)
                pointF5 = PointF(pointF6.x - width, pointF6.y)
                pointF2 = PointF(pointF5.x, height + pointF5.y)
                pointF7 = pointF6
                pointF6 = pointF5
                pointF5 = pointF2
                pointF2 = PointF(pointF6.x, pointF2.y)
                pointF = pointF7
            }
            4 -> {
                pointF5 = PointF(
                    (getWidth() / 2).toFloat() - width / 2.0f + f4,
                    (getHeight() / 2).toFloat() + height / 2.0f - f2
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
                    (getWidth() / 2).toFloat() - width / 2.0f + f4,
                    getHeight().toFloat() - f2
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
                    getWidth().toFloat() - f3,
                    (getHeight() / 2).toFloat() + height / 2.0f - f2
                )
                pointF5 = PointF(pointF6.x - width, pointF6.y)
                pointF2 = PointF(pointF5.x, pointF5.y - height)
                pointF = PointF(pointF6.x, pointF2.y)
                pointF7 = pointF6
                pointF6 = pointF2
                pointF2 = pointF7
            }
            7 -> {
                pointF5 = PointF((getWidth() / 2).toFloat() - width / 2.0f + f4, f5)
                pointF6 = PointF(width + pointF5.x, pointF5.y)
                pointF2 = PointF(pointF5.x, height + pointF5.y)
                pointF7 = pointF6
                pointF6 = pointF5
                pointF5 = pointF2
                pointF2 = PointF(pointF6.x, pointF2.y)
                pointF = pointF7
            }
            8 -> {
                pointF4 = PointF(f4, (getHeight() / 2).toFloat() + height / 2.0f - f2)
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
        GLES20.glActiveTexture(33986)
        GLES20.glBindTexture(3553, mTexture)
        GLES20.glUniform1i(textureHandle, 2)
        GLES20.glVertexAttribPointer(positionHandle, 2, 5126, false, 0, floatBufferArr[0])
        GLES20.glVertexAttribPointer(texCoordHandle, 2, 5126, false, 0, floatBufferArr[1])
        if (floatBufferArr[0].limit() > 8) {
            GLES20.glDrawArrays(4, 0, floatBufferArr[0].limit() / 2)
        } else {
            GLES20.glDrawArrays(5, 0, 4)
        }
        GLES20.glBindTexture(3553, 0)
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
        return Math.sqrt(((pointF.x - pointF2.x) * (pointF.x - pointF2.x) + (pointF.y - pointF2.y) * (pointF.y - pointF2.y)).toDouble())
            .toFloat()
    }

}