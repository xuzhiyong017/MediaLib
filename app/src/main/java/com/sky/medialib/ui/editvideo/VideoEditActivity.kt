package com.sky.medialib.ui.editvideo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceHolder
import com.sky.medialib.R
import com.sky.medialib.ui.kit.common.base.AppActivity
import kotlinx.android.synthetic.main.activity_video_edit.*
import tv.danmaku.ijk.media.player.IjkMediaPlayer

const val VIDEO_PATH = "video_path"

class VideoEditActivity : AppActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_edit)

        surface_view.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                IjkMediaPlayer().apply {
                    setDataSource(intent.getStringExtra(VIDEO_PATH),null)
                    setDisplay(holder)
                    prepareAsync()
                    start()
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {

            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {

            }
        })


    }
}