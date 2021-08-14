package com.sky.medialib.ui.editvideo

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceHolder
import android.widget.RelativeLayout
import com.sky.medialib.R
import com.sky.medialib.ui.kit.common.base.AppActivity
import com.sky.medialib.ui.kit.model.PublishVideo
import kotlinx.android.synthetic.main.activity_video_edit.*
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import kotlin.math.ceil

const val VIDEO_PATH = "video_path"

class VideoEditActivity : AppActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_edit)
        val publishVideo: PublishVideo? = intent.getSerializableExtra("key_video") as PublishVideo?
        surface_view.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                IjkMediaPlayer().apply {
                    setDataSource(publishVideo?.videoPath,null)
                    setOnVideoSizeChangedListener{ _,_,_,_,_ ->
                        var  width = videoWidth
                        var  height = videoHeight
                        val surfaceWidth = surface_view.width
                        val surfaceHeight = surface_view.height

                        var max = 1.0f;
                        max = if (resources.configuration.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                            //竖屏模式下按视频宽度计算放大倍数值
                            (videoWidth / surfaceWidth.toFloat()).coerceAtLeast(videoHeight / surfaceHeight.toFloat());
                        } else {
                            //横屏模式下按视频高度计算放大倍数值
                            (videoWidth / surfaceHeight.toFloat()
                                .coerceAtLeast(videoHeight / surfaceWidth.toFloat()))
                        }

                        width = ceil(videoWidth / max).toInt()
                        height = ceil(videoHeight / max).toInt()

                        surface_view.layoutParams = RelativeLayout.LayoutParams(width,height).apply {
                            addRule(RelativeLayout.CENTER_VERTICAL)
                        }
                    }
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
                surface_view.layoutParams
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {

            }
        })


    }

}