package com.sky.medialib.ui.music.entity

import java.io.Serializable

/**
 * @author: xuzhiyong
 * @date: 2021/8/12  下午4:49
 * @Email: 18971269648@163.com
 * @description:
 */
data class JsonMusic(
    val id:Int,
    val type:Int,
    val delFlag:Int,
    val typeName:String?,
    val name:String?,
    val coverUrl:String?,
    val url:String?,
    val authorName:String?,
    val videoTimeSize:String,
    val createTime:String?,
    val modifyTime:String?,
):Serializable