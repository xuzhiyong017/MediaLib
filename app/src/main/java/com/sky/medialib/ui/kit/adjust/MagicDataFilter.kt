package com.sky.medialib.ui.kit.adjust

import android.content.Context
import android.opengl.GLES20
import android.text.TextUtils
import com.sky.media.image.core.base.TextureOutRender
import com.sky.media.image.core.cache.ImageBitmapCache
import com.sky.media.image.core.filter.IAdjustable
import com.sky.media.image.core.render.MultiBmpInputRender
import com.sky.media.image.core.util.TextureBindUtil
import com.sky.medialib.ui.kit.model.json.magic.JsonMirror
import com.sky.medialib.util.AESUtil
import com.sky.medialib.util.FileUtil
import com.sky.medialib.util.ToastUtils
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * @author: xuzhiyong
 * @date: 2021/8/2  上午11:37
 * @Email: 18971269648@163.com
 * @description:
 */
class MagicDataFilter(override var mContext: Context?, private val jsonMirrors:List<JsonMirror>)
    : MultiBmpInputRender(ImageBitmapCache.getInstance()),IAdjustable {

    private var mAspectRatioHandle = 0
    private var mMaskAspectRatioHandle = 0
    private var mMaskAspectRatioValue = 0f
    private var mMixHandle = 0
    private var mMix = 0f

    private var initStatus: IntArray
    private var avshList: Array<String?>
    private var afshList: Array<String?>

    private var pos = 0

    init {
        val size = jsonMirrors.size
        initStatus = IntArray(size)
        for (i in 0 until size) {
            initStatus[i] = -1
        }
        avshList = arrayOfNulls(size)
        afshList = arrayOfNulls(size)
        checkFileStatus()
    }

    fun getJsonMirrorsSize(): Int {
        return jsonMirrors!!.size
    }

    fun setCurJsonMirrors(i: Int) {
        pos = i
    }

    private fun deleteFile() {
        File(jsonMirrors[pos].cacheUnzipDirPath).delete()
    }

    private fun checkFileStatus() {
        if (initStatus[pos] != 2) {
            if (jsonMirrors[pos].haveCache()) {
                if (initStatus[pos] != 0) {
                    initStatus[pos] = 0
                    val file: File = File(jsonMirrors[pos].cacheUnzipDirPath)
                    val file2 = File(file, "shader.json")
                    if (file.exists() && file.isDirectory && file2.exists() && file2.isFile) {
                        parseConfig(file)
                        return
                    }
                    try {
                        FileUtil.unZipFile(jsonMirrors[pos].cachePath, jsonMirrors[pos].cacheUnzipDirPath)
                        parseConfig(file)
                    } catch (e: Throwable) {
                        e.printStackTrace()
                        initStatus[pos] = 3
                        deleteFile()
                    }
                }
            }
        }
    }

    private fun parseConfig(file: File) {
        val file2 = File(file, "shader.json")
        if (file2.exists() && file2.isFile) {
            try {
                val jSONObject = JSONObject(String(FileUtil.readFile2Bytes(file2.absolutePath)))
                val optJSONArray: JSONArray = jSONObject.optJSONArray("textures")
                val strArr = arrayOfNulls<String>(optJSONArray.length())
                var i = 0
                while (true) {
                    val i2 = i
                    if (i2 < optJSONArray.length()) {
                        strArr[i2] =
                            "file:///" + (jsonMirrors!![pos] as JsonMirror).cacheUnzipDirPath
                                .toString() + "/" + optJSONArray.optString(i2)
                        i = i2 + 1
                    } else {
                        setImages(mContext, strArr)
                        avshList[pos] = jSONObject.optString("avsh")
                        afshList[pos] = AESUtil().decodeString(jSONObject.optString("afsh"))
                        initStatus[pos] = 2
                        return
                    }
                }
            } catch (e: Throwable) {
                initStatus[pos] = 3
                deleteFile()
                return
            }
        }
        initStatus[pos] = 3
        deleteFile()
    }

    override var mFragmentShader: String = ""
        get() = if (TextUtils.isEmpty(afshList[pos]) || !isValidTexture()) {
            super.mFragmentShader
        } else afshList[pos]!!

    override var mVertexShader: String = ""
        get() = if (TextUtils.isEmpty(avshList[pos]) || !isValidTexture()) {
            super.mVertexShader
        } else avshList[pos]!!

    override fun initShaderHandles() {
        super.initShaderHandles()
        mAspectRatioHandle = GLES20.glGetUniformLocation(programHandle, "u_AspectRatio")
        mMixHandle = GLES20.glGetUniformLocation(programHandle, "u_mix")
        mMaskAspectRatioHandle = GLES20.glGetUniformLocation(programHandle, "u_MaskAspectRatio")
    }

    override fun bindShaderValues() {
        super.bindShaderValues()
        GLES20.glUniform1f(mAspectRatioHandle, height * 1.0f / width)
        GLES20.glUniform1f(mMixHandle, mMix)
        GLES20.glUniform1f(mMaskAspectRatioHandle, mMaskAspectRatioValue)
    }

    override fun dealNextTexture(gLTextureOutputRenderer: TextureOutRender, i: Int, z: Boolean) {
        checkFileStatus()
        if (z) {
            markNeedDraw()
        }
        texture_in = i
        if (mTextureNum > 1) {
            for (i2 in mTextures!!.indices) {
                if (mTextures!![i2] == 0) {
                    mTextures!![i2] = TextureBindUtil.bindBitmap(getBitmap(i2))
                }
            }
            val bitmap = getBitmap(mTextures!!.size - 1)
            if (!(bitmap == null || bitmap.isRecycled)) {
                mMaskAspectRatioValue = bitmap.width.toFloat() * 1.0f / bitmap.height
                    .toFloat()
            }
        }
        width = gLTextureOutputRenderer.width
        height = gLTextureOutputRenderer.height
        onDrawFrame()
    }

    private fun isValidTexture(): Boolean {
        for (i in mTextures!!) {
            if (i <= 0) {
                return false
            }
        }
        return true
    }

    override fun adjust(i: Int, i2: Int, i3: Int) {
        mMix = (i - i2).toFloat() * 1.0f / (i3 - i2).toFloat()
    }
}