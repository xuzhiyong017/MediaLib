package com.sky.medialib.ui.music.entity

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.sky.medialib.R
import com.sky.medialib.ui.kit.common.base.adapter.IAdapter
import com.sky.medialib.ui.kit.common.base.adapter.Item
import com.sky.medialib.ui.kit.model.Music
import java.lang.StringBuilder

/**
 * @author: xuzhiyong
 * @date: 2021/8/12  下午4:33
 * @Email: 18971269648@163.com
 * @description:
 */
class MusicItem :Item<Music> {

    lateinit var cover:ImageView
    lateinit var singerName: TextView
    lateinit var songName:TextView

    override fun bindView(view: View?, iAdapter: IAdapter<Music>?) {
        view?.run {
            cover = findViewById(R.id.cover)
            singerName = findViewById(R.id.singer_name)
            songName = findViewById(R.id.song_name)
        }
    }

    override fun setData(music: Music?, i: Int) {
        if (music != null) {
            Glide.with(cover).load(music.photo).apply(RequestOptions.bitmapTransform(CircleCrop())).into(cover)
            val sb = StringBuilder()
            if (music.artist != null && music.artist.size > 0) {
                val it: Iterator<String> = music.artist.iterator()
                while (it.hasNext()) {
                    sb.append(it.next())
                }
            }else{
                sb.append(music.singer)
            }
            this.singerName.text = sb.toString()
            this.songName.text = music.name
        }
    }

    override fun getLayoutResId() = R.layout.item_music
}