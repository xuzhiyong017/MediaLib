package com.sky.media.image.core.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.lang.IllegalArgumentException
import java.util.*

/**
 * @author: xuzhiyong
 * @date: 2021/7/29  上午10:27
 * @Email: 18971269648@163.com
 * @description:
 */
class BitmapUtil {

    companion object{
        fun  loadBitmap(context: Context, str: String): Bitmap? {
            try {
                return when {
                    Scheme.ASSETS.belongsTo(str) -> {
                        BitmapFactory.decodeStream(context.assets.open(Scheme.ASSETS.crop(str)))
                    }
                    Scheme.FILE.belongsTo(str) -> {
                        BitmapFactory.decodeFile(str)
                    }
                    Scheme.DRAWABLE.belongsTo(str) -> {
                        BitmapFactory.decodeFile(str)
                    }
                    else -> {
                        null
                    }
                }
            }catch (e:OutOfMemoryError){
                e.printStackTrace()
            }catch (e:Exception){
                e.printStackTrace()
            }
           return null
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