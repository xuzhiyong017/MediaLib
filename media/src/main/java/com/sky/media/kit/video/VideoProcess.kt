package com.sky.media.kit.video

import android.graphics.Rect
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.sky.media.image.core.base.BaseRender
import com.sky.media.image.core.base.OnScreenEndPoint
import com.sky.media.image.core.filter.IRequireProgress
import com.sky.media.image.core.pipeline.RenderPipeline
import com.sky.media.image.core.process.base.BaseOnscreenProcess
import com.sky.media.image.core.render.GroupRender
import com.sky.media.image.core.view.IContainerView
import com.sky.media.image.core.view.IRenderView
import com.sky.media.kit.player.IMediaPlayer
import com.sky.media.kit.player.SimpleMediaPlayer
import com.sky.media.kit.util.NumberUtil
import com.sky.media.kit.video.VideoSequenceHelper.BaseSequence
import java.util.*

open class VideoProcess(iContainerView: IContainerView, iRenderView: IRenderView) :
    BaseOnscreenProcess<VideoInput>(iContainerView, iRenderView) {
    private val mVideoSequenceHelper: VideoSequenceHelper =
        VideoSequenceHelper(iContainerView.getPreviewWidth(), iContainerView.getPreviewHeight())
    private var mLastRender: BaseRender? = null

    class MediaPlayerBuilder {
        var previewWidth = 0
        var previewHeight = 0
        var isLooping = false
            private set
        var isNeedPlay = true
            private set
        var preparedListener: IMediaPlayer.OnPreparedListener? = null
        var completionListener: IMediaPlayer.OnCompletionListener? = null
        var mediaPlayerEx: IMediaPlayer = SimpleMediaPlayer()
        var volume = 1.0f
            private set

        fun volume(f: Float): MediaPlayerBuilder {
            volume = f
            return this
        }

        fun setPreviewSize(i: Int, i2: Int): MediaPlayerBuilder {
            previewWidth = i
            previewHeight = i2
            return this
        }

        fun setLooping(z: Boolean): MediaPlayerBuilder {
            isLooping = z
            return this
        }

        fun setNeedPlay(z: Boolean): MediaPlayerBuilder {
            isNeedPlay = z
            return this
        }

        fun setOnPreparedListener(onPreparedListener: IMediaPlayer.OnPreparedListener?): MediaPlayerBuilder {
            this.preparedListener = onPreparedListener
            return this
        }

        fun setOnCompletionListener(onCompletionListener: IMediaPlayer.OnCompletionListener?): MediaPlayerBuilder {
            this.completionListener = onCompletionListener
            return this
        }

        fun setMediaPlayer(iMediaPlayer: IMediaPlayer): MediaPlayerBuilder {
            mediaPlayerEx = iMediaPlayer
            return this
        }
    }

    val sequences: Stack<BaseSequence>
        get() = mVideoSequenceHelper.sequences

    fun clear() {
        mVideoSequenceHelper.clear()
    }

    fun pushSequence(baseSequence: BaseSequence?) {
        if (baseSequence != null) {
            mVideoSequenceHelper.pushSequence(baseSequence)
        }
    }

    fun popSequence(): BaseSequence {
        return mVideoSequenceHelper.popSequence()
    }

    fun processVideo(timeStamp: Long) {
        if (mInput != null) {
            val baseSequence = mVideoSequenceHelper.getSequence(timeStamp)
            val render: BaseRender? = mVideoSequenceHelper.render
            if (render == null) {
                if (mGroupRender != null) {
                    if (mLastRender != null) {
                        mGroupRender!!.addNextRender(mOnscreenEndpoint)
                        mGroupRender!!.removeRenderIn(mLastRender)
                        mLastRender!!.removeRenderIn(mOnscreenEndpoint)
                    }
                } else if (mLastRender != null) {
                    mInput!!.addNextRender(mOnscreenEndpoint)
                    mInput!!.removeRenderIn(mLastRender)
                    mLastRender!!.removeRenderIn(mOnscreenEndpoint)
                }
            } else if (render != mLastRender) {
                if (mGroupRender != null) {
                    if (mLastRender != null) {
                        mGroupRender!!.addNextRender(render)
                        render.addNextRender(mOnscreenEndpoint)
                        mGroupRender!!.removeRenderIn(mLastRender)
                        mLastRender!!.removeRenderIn(mOnscreenEndpoint)
                    } else {
                        mGroupRender!!.addNextRender(render)
                        render.addNextRender(mOnscreenEndpoint)
                        mGroupRender!!.removeRenderIn(mOnscreenEndpoint)
                    }
                } else if (mLastRender != null) {
                    mInput!!.addNextRender(render)
                    render.addNextRender(mOnscreenEndpoint)
                    mInput!!.removeRenderIn(mLastRender)
                    mLastRender!!.removeRenderIn(mOnscreenEndpoint)
                } else {
                    mInput!!.addNextRender(render)
                    render.addNextRender(mOnscreenEndpoint)
                    mInput!!.removeRenderIn(mOnscreenEndpoint)
                }
            }
            mLastRender = render
            if (baseSequence != null) {
                val arrayList: MutableList<BaseRender> = ArrayList<BaseRender>()
                if (mGroupRender is GroupRender) {
                    arrayList.addAll((mGroupRender as GroupRender).filters)
                } else if (mGroupRender != null) {
                    arrayList.add(mGroupRender!!)
                }
                if (mLastRender is GroupRender) {
                    arrayList.addAll((mLastRender as GroupRender).filters)
                } else if (mLastRender != null) {
                    arrayList.add(mLastRender!!)
                }
                for (basicRender in arrayList) {
                    if (basicRender is IRequireProgress) {
                        val duration = (timeStamp - baseSequence.start) * 1.0f / 1000.0f / (basicRender as IRequireProgress).getDuration()
                        (basicRender as IRequireProgress).setProgress(duration - duration.toInt().toFloat())
                    }
                }
            }
        }
    }

    fun seekTo(i: Int) {
        if (mInput != null) {
            mInput!!.seekTo(i)
        }
    }

    fun pauseVideo() {
        if (mInput != null) {
            mInput!!.pause()
        }
    }

    fun startPlay() {
        if (mInput != null) {
            mInput!!.startPlay()
        }
    }

    val isPlaying: Boolean
        get() = mInput != null && mInput!!.isPlaying()


    fun initMediaPlayer(uri: Uri?, mediaPlayerBuilder: MediaPlayerBuilder) {
        var haschangeSize2: Boolean
        val previewWidth = mediaPlayerBuilder.previewWidth
        val previewHeight = mediaPlayerBuilder.previewHeight
        val mediaPlayer = mediaPlayerBuilder.mediaPlayerEx
        val looping = mediaPlayerBuilder.isLooping
        val volume = mediaPlayerBuilder.volume
        val needPlay = mediaPlayerBuilder.isNeedPlay
        val onPreparedListener = mediaPlayerBuilder.preparedListener
        checkIsMainThread()
        val mediaMetadataRetriever = MediaMetadataRetriever()
        var haschangeSize = true
        try {
            mediaMetadataRetriever.setDataSource(this.iRenderView.getContext(), uri)
            val videoWidth: Int =
                NumberUtil.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH))
            val videoHeight: Int =
                NumberUtil.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT))
            haschangeSize = if (NumberUtil.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)) / 90 % 2 !== 0
            ) {
                mContainerView.setAspectRatio(videoHeight.toFloat() * 1.0f / videoWidth.toFloat(), previewWidth, previewHeight)
            } else {
                mContainerView.setAspectRatio(videoWidth.toFloat() * 1.0f / videoHeight.toFloat(), previewWidth, previewHeight)
            }
            mediaMetadataRetriever.release()
            haschangeSize2 = haschangeSize
        } catch (e2: Throwable) {
            e2.printStackTrace()
        } finally {
            haschangeSize2 = haschangeSize
            mediaMetadataRetriever.release()
        }
        mPipeline!!.pauseRendering()
        if (mInput == null) {
            mInput = VideoInput(iRenderView)
            mPipeline!!.setRootRenderer(mInput)
        }
        if (mOnscreenEndpoint == null) {
            mOnscreenEndpoint = OnScreenEndPoint(mPipeline!!)
            mInput!!.addNextRender(mOnscreenEndpoint)
        }
        try {
            mInput!!.setMediaPlayerAndUri(mediaPlayer, uri)
        } catch (th2: Throwable) {
            th2.printStackTrace()
        }
        mInput!!.setLooping(looping)
        mInput!!.setVolumeLeftAndRight(volume, volume)
        if (mContainerView.getPreviewWidth() > 0 && mContainerView.getPreviewHeight() > 0) {
            mVideoSequenceHelper.setWidth(mContainerView.getPreviewWidth())
            mVideoSequenceHelper.setHeight(mContainerView.getPreviewHeight())
        }
        if (haschangeSize2) {
            mPipeline!!.addOnSizeChangedListener(object : RenderPipeline.OnSizeChangedListener {
                override fun getSize(): Rect {
                    return Rect(
                        0,
                        0,
                        mContainerView.getPreviewWidth(),
                        mContainerView.getPreviewHeight()
                    )
                }

                override fun onSizeChanged(i: Int, i2: Int) {
                    mInput!!.setRenderSize(
                        mContainerView.getPreviewWidth(),
                        mContainerView.getPreviewHeight()
                    )
                    mOnscreenEndpoint!!.setRenderSize(
                        mContainerView.getPreviewWidth(),
                        mContainerView.getPreviewHeight()
                    )
                    resetRenderSize()
                    iRenderView.requestRender()
                }
            })
        } else {
            mInput!!.setRenderSize(
                mContainerView.getPreviewWidth(),
                mContainerView.getPreviewHeight()
            )
            mOnscreenEndpoint!!.setRenderSize(
                mContainerView.getPreviewWidth(),
                mContainerView.getPreviewHeight()
            )
            iRenderView.requestRender()
        }
        mInput!!.setOnPreparedListener(object : VideoInput.OnPreparedListener {
            override fun onPrepared(iMediaPlayer: IMediaPlayer) {
                onPreparedListener?.onPrepared(iMediaPlayer)
                iMediaPlayer.setOnCompletionListener { iMediaPlayer ->
                    mediaPlayerBuilder.completionListener?.onCompletion(
                        iMediaPlayer
                    )
                }
            }
        })
        if (needPlay) {
            mInput!!.startPlay()
        }
        mPipeline!!.startRendering()
        iRenderView.requestRender()
    }

}