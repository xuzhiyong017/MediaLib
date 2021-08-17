package com.sky.media.image.core.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.IOException
import java.io.InputStream
import java.util.*





/**
 * @author: xuzhiyong
 * @date: 2021/7/29  上午10:27
 * @Email: 18971269648@163.com
 * @description:
 */
class BitmapUtil {

    companion object{
        @JvmStatic
        fun  loadBitmap(context: Context, str: String): Bitmap? {
            var inputStream: InputStream? = null
            try {
                val options = BitmapFactory.Options()
                options.inScaled = false
                options.inDither = false
                options.inInputShareable = true
                options.inPurgeable = true
                if (Scheme.ASSETS.belongsTo(str)) {
                    inputStream = context.assets.open(Scheme.ASSETS.crop(str))
                    return BitmapFactory.decodeStream(inputStream, null, options)
                } else if (Scheme.FILE.belongsTo(str)) {
                    return BitmapFactory.decodeFile(Scheme.FILE.crop(str), options)
                } else if (Scheme.DRAWABLE.belongsTo(str)) {
                    return BitmapFactory.decodeResource(context.resources, Scheme.DRAWABLE.crop(str).toInt(), options)
                } else {
                    return BitmapFactory.decodeFile(str, options)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e8: OutOfMemoryError) {
                e8.printStackTrace()
            } catch (th: Throwable) {
                th.printStackTrace()
            }finally {
                try {
                    inputStream?.close()
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
            return null
        }

        fun calculateInSampleSize(
            options: BitmapFactory.Options,
            maxWidth: Int,
            maxHeight: Int
        ): Int {
            var height = options.outHeight
            var width = options.outWidth
            var inSampleSize = 1
            while (height > maxHeight || width > maxWidth) {
                height = height shr 1
                width = width shr 1
                inSampleSize = inSampleSize shl 1
            }
            return inSampleSize
        }


        @JvmStatic
        fun loadBitmap(context: Context, str: String, width: Int, height: Int): Bitmap? {
            var inputStream: InputStream? = null

            var bitmap: Bitmap? = null
            val options = BitmapFactory.Options()
            try {
                options.inJustDecodeBounds = true
                when {
                    Scheme.ASSETS.belongsTo(str) -> {
                        inputStream = context.assets.open(Scheme.ASSETS.crop(str))
                        BitmapFactory.decodeStream(inputStream, null, options)
                    }
                    Scheme.FILE.belongsTo(str) -> {
                        BitmapFactory.decodeFile(Scheme.FILE.crop(str), options)
                    }
                    Scheme.DRAWABLE.belongsTo(str) -> {
                        BitmapFactory.decodeResource(context.resources, Scheme.DRAWABLE.crop(str).toInt(), options)
                    }
                    else -> {
                        BitmapFactory.decodeFile(str, options)
                    }
                }
                options.inSampleSize = calculateInSampleSize(options,width,height)
                options.inJustDecodeBounds = false

                if (Scheme.ASSETS.belongsTo(str)) {
                    val open: InputStream = context.assets.open(Scheme.ASSETS.crop(str))
                    if (open != null) {
                        bitmap = BitmapFactory.decodeStream(open, null, options)
                        try {
                            open.close()
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }
                } else if (Scheme.FILE.belongsTo(str)) {
                    bitmap = BitmapFactory.decodeFile(Scheme.FILE.crop(str), options)
                } else if (Scheme.DRAWABLE.belongsTo(str)) {
                    bitmap = BitmapFactory.decodeResource(
                        context.resources,
                        Scheme.DRAWABLE.crop(str).toInt(),
                        options
                    )
                } else {
                    bitmap = BitmapFactory.decodeFile(str, options)
                }
                return bitmap
            } catch (e5: Exception) {
                e5.printStackTrace()
            } finally {
                inputStream?.close()
            }
            return bitmap
        }
    }

    enum class Scheme(open val scheme: String) {
        FILE("file://"), ASSETS("file:///android_asset/"), DRAWABLE("drawable://"), UNKNOWN("");

        fun belongsTo(str: String): Boolean {
            return str.toLowerCase(Locale.US).startsWith(scheme)
        }

        fun wrap(str: String): String {
            return scheme + str
        }

        fun crop(str: String): String {
            if (belongsTo(str)) {
                return str.substring(scheme.length)
            }
            throw IllegalArgumentException(
                String.format(
                    "URI [%1\$s] doesn't have expected scheme [%2\$s]", *arrayOf<Any>(
                        str,
                        scheme
                    )
                )
            )
        }

        companion object {
            fun ofUri(str: String?): Scheme {
                if (str != null) {
                    for (scheme in Scheme.values()) {
                        if (scheme.belongsTo(str)) {
                            return scheme
                        }
                    }
                }
                return UNKNOWN
            }
        }
    }
}