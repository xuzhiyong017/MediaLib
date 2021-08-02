package com.sky.medialib.ui.kit.model

import java.io.Serializable

/**
 * @author: xuzhiyong
 * @date: 2021/7/31  下午4:32
 * @Email: 18971269648@163.com
 * @description:
 */
data class ShowRect(val x: Int = 0,val y: Int = 0,val width: Int = 0,val height: Int = 0) : Serializable {

    override fun toString(): String {
        return "ShowRect{x='$x', y='$y', width='$width', height='$height'}"
    }
}