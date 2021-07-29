package com.sky.media.kit.model

import android.text.TextUtils
import com.sky.media.image.core.filter.Filter

/**
 * @author: xuzhiyong
 * @date: 2021/7/28  上午9:53
 * @Email: 18971269648@163.com
 * @description:
 */
open class FilterExt : Filter() {
    open var mIconResource = 0
    open var mId = 0
    open var mNameBackgroundColor = 0

    open fun getIconResource(): Int {
        return mIconResource
    }

    open fun getIconRes(): String? {
        return if (TextUtils.isEmpty(icon)) {
            "drawable://" + getIconResource()
        } else icon
    }

}