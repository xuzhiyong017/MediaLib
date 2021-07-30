package com.sky.medialib.util

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build.VERSION
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore.*
import android.text.TextUtils
import java.lang.Exception
import java.net.URLDecoder

/**
 * @author: xuzhiyong
 * @date: 2021/7/30  上午11:03
 * @Email: 18971269648@163.com
 * @description:
 */
object FileUtil {

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }


    fun getPathFromUri(context: Context?, uri: Uri?): String? {
        var uri2: Uri? = null
        var decode = ""
        var query: Cursor? = null
        if (!(context == null || uri == null)) {
            try {
                decode = URLDecoder.decode(uri.toString(), "UTF-8")
                if (decode.startsWith("file://")) {
                    return decode.replace("file://", "")
                }
                if (decode.startsWith("/")) {
                    return decode
                }
                if (VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context, uri)) {
                    if (FileUtil.isExternalStorageDocument(uri)) {
                        val split = DocumentsContract.getDocumentId(uri).split(":").toTypedArray()
                        if ("primary".equals(split[0], ignoreCase = true)) {
                            return Environment.getExternalStorageDirectory()
                                .toString() + "/" + split[1]
                        }
                    } else if (FileUtil.isDownloadsDocument(uri)) {
                        return FileUtil.getDataColumn(
                            context, ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"),
                                java.lang.Long.valueOf(DocumentsContract.getDocumentId(uri))
                                    .toLong()
                            ), null, null
                        )
                    } else if (FileUtil.isMediaDocument(uri)) {
                        val split2 = DocumentsContract.getDocumentId(uri).split(":").toTypedArray()
                        val str = split2[0]
                        if ("image" == str) {
                            uri2 = Images.Media.EXTERNAL_CONTENT_URI
                        } else if ("video" == str) {
                            uri2 = Video.Media.EXTERNAL_CONTENT_URI
                        } else if ("audio" == str) {
                            uri2 = Audio.Media.EXTERNAL_CONTENT_URI
                        }
                        val str2 = "_id=?"
                        return FileUtil.getDataColumn(
                            context, uri2!!, "_id=?", arrayOf(
                                split2[1]
                            )
                        )
                    }
                }
                query = context.contentResolver.query(uri, arrayOf("_data"), null, null, null)
                if (query != null) {
                    query.moveToFirst()
                    val columnIndexOrThrow = query.getColumnIndexOrThrow("_data")
                    var str3 =
                        if (columnIndexOrThrow < query.count) query.getString(columnIndexOrThrow) else null
                    if (str3 == null) {
                        str3 = FileUtil.getPathFromUri44(context, uri)
                    }
                    return str3
                }
            } catch (e5: Exception) {
                e5.printStackTrace()
            } finally {
                query?.close()
            }
        }
        return null
    }

    private fun getPathFromUri44(context: Context, uri: Uri): String? {
        var str: String? = null
        if (VERSION.SDK_INT >= 19) {
            val query = context.contentResolver.query(uri, null, null, null, null)
            if (query != null) {
                try {
                    query.moveToFirst()
                    str = getPathFromUri44(
                        context,
                        query.getString(query.getColumnIndexOrThrow("document_id"))
                    )
                } catch (e2: Exception) {
                    e2.printStackTrace()
                } finally {
                    if (query != null) {
                        query.close()
                    }
                }
            }
        }
        return str
    }

    private fun getPathFromUri44(context: Context, str: String): String? {
        var str2: String? = null
        if (!TextUtils.isEmpty(str)) {
            val str3 = str.split(":").toTypedArray()[1]
            val strArr = arrayOf("_data")
            val query = context.contentResolver.query(
                Video.Media.EXTERNAL_CONTENT_URI,
                strArr,
                "_id=?",
                arrayOf(str3),
                null
            )
            try {
                if (query != null) {
                    val columnIndex = query.getColumnIndex(strArr[0])
                    if (query.moveToFirst()) {
                        str2 = query.getString(columnIndex)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                query?.close()
            }
        }
        return str2
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri,
        str: String?,
        strArr: Array<String>?
    ): String? {
        var query: Cursor? = null
        var str2: String? = "_data"
        try {
            query = context.contentResolver.query(uri, arrayOf("_data"), str, strArr, null)
            if (query != null) {
                if (query.moveToFirst()) {
                    str2 = query.getString(query.getColumnIndexOrThrow("_data"))
                }
            }
        } catch (th3: Exception) {
            th3.printStackTrace()
        } finally {
            query?.close()
        }
        return str2
    }
}