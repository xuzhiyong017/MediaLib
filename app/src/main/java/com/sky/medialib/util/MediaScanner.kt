package com.sky.medialib.util

import android.media.MediaScannerConnection
import android.net.Uri
import android.webkit.MimeTypeMap
import com.sky.media.kit.BaseMediaApplication
import java.util.ArrayList

/**
 * @author: xuzhiyong
 * @date: 2021/7/31  下午3:35
 * @Email: 18971269648@163.com
 * @description:
 */
class MediaScanner : MediaScannerConnection.MediaScannerConnectionClient{

    private val mMediaScannerConnection = MediaScannerConnection(BaseMediaApplication.sContext, this)
    private val mNotifyList: ArrayList<Array<String>> = ArrayList<Array<String>>()
    private var mCurrent: Array<String>? = null
    private var scanCount = 0

    private fun isConnected():Boolean{
        return mMediaScannerConnection.isConnected
    }

    fun notifyUrl(url:String ){
        notifyUrlExt(arrayOf(url))
    }

    private fun notifyUrlExt(list: Array<String>?) {
        if(list != null && list.isNotEmpty()){
            mNotifyList.add(list)
            updateList()
        }
    }

    private fun updateList() {
        if(!isConnected() && mNotifyList.size > 0){
            mCurrent = mNotifyList.removeAt(0)
            mMediaScannerConnection.connect()
        }
    }

    override fun onScanCompleted(path: String?, uri: Uri?) {
        scanCount++
        if(mCurrent == null){
            mMediaScannerConnection.disconnect()
            scanCount = 0
        }else if(scanCount == mCurrent!!.size){
            mMediaScannerConnection.disconnect()
            scanCount = 0
            mCurrent = null
            updateList()
        }
    }

    override fun onMediaScannerConnected() {
        mCurrent?.forEach {
            mMediaScannerConnection.scanFile(it,MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(it)))
        }
    }
}