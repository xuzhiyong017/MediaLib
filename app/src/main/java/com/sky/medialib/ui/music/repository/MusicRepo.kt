package com.sky.medialib.ui.music.repository

import com.sky.medialib.ui.kit.model.Music
import com.sky.medialib.ui.music.api.APIMusic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author: xuzhiyong
 * @date: 2021/8/12  下午5:02
 * @Email: 18971269648@163.com
 * @description:
 */

class MusicRepo {
    suspend fun getMusicList():List<Music>{
        return withContext(Dispatchers.IO){
            val list = APIMusic().getMusicList()
            list.map {
                Music().apply {
                    duration = covertToInt(it.videoTimeSize)
                    id = it.id.toString()
                    name = it.name
                    singer = it.authorName
                    photo = it.coverUrl
                    url = it.url


                }
            }
        }
    }

    private fun covertToInt(videoTimeSize: String): Int {
        val list = videoTimeSize.split(":")
        var sum = 0;
        if(list.size > 1){
            sum = list[0].toInt() * 60
            sum += list[1].toInt()
        }
        else{
            sum = list[0].toInt()
        }
        return sum
    }
}