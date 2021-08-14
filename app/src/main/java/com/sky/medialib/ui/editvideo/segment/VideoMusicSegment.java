package com.sky.medialib.ui.editvideo.segment;

import static com.sky.medialib.ui.music.MusicChooseActivityKt.KEY_IS_FROM_CAMERA;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sky.media.kit.base.BaseActivity;
import com.sky.medialib.R;
import com.sky.medialib.ui.dialog.BottomSheetDialog;
import com.sky.medialib.ui.editvideo.segment.entity.VideoEditData;
import com.sky.medialib.ui.editvideo.segment.proto.MusicPlayerProtocol;
import com.sky.medialib.ui.kit.common.base.AppActivity;
import com.sky.medialib.ui.music.MusicChooseActivity;
import com.sky.medialib.ui.music.event.CutMusicInfoEvent;
import com.sky.medialib.util.EventBusHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class VideoMusicSegment extends BaseSegment<VideoEditData> implements MusicPlayerProtocol {

    private IjkMediaPlayer ijkMediaPlayer;
    @BindView(R.id.music_btn)
    RelativeLayout mMusicButton;
    @BindView(R.id.music_cover)
    ImageView mMusicCover;
    @BindView(R.id.music_cover_mask)
    ImageView mMusicCoverMask;
    @BindView(R.id.music_name)
    TextView mMusicName;

    public VideoMusicSegment(AppActivity baseActivity, VideoEditData videoEditData) {
        super(baseActivity, videoEditData);
        ButterKnife.bind((Object) this, (Activity) baseActivity);
        bindListener();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        EventBusHelper.register(this);
    }

    public void onDestroy() {
        super.onDestroy();
        EventBusHelper.unregister(this);
    }

    private void bindListener() {
        this.mMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mData.isCameraMusic()){
                    if(mData.getMusic() != null){
                        new BottomSheetDialog(activity).addList(
                                activity.getString(R.string.change_music),
                                activity.getString(R.string.cancel_music)
                        ).setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                switch (position) {
                                    case 0:
                                        Intent intent = new Intent(activity, MusicChooseActivity.class);
                                        intent.putExtra(KEY_IS_FROM_CAMERA, false);
                                         activity.startActivity(intent);
                                        return;
                                    case 1:
                                        mData.setMusic(null);
                                        mData.setMusicPath(null);
                                        stopMusicPlay();
                                        initmusic();
                                        return;
                                    default:
                                        return;
                                }
                            }
                        }).show();
                    }else{
                        Intent intent = new Intent(activity, MusicChooseActivity.class);
                        intent.putExtra(KEY_IS_FROM_CAMERA, false);
                        activity.startActivity(intent);
                    }
                }
            }
        });
    }

    private void initmusic() {
        if (((VideoEditData) this.mData).getMusic() == null) {
            this.mMusicCover.setImageResource(0);
            this.mMusicCoverMask.setImageResource(R.drawable.selector_video_music_off);
        } else if (((VideoEditData) this.mData).isCameraMusic()) {
            Glide.with(activity).load(mData.getMusic().photo)
                    .placeholder(R.drawable.shoot_button_music_disabled)
                    .into(this.mMusicCover);
            this.mMusicCoverMask.setImageResource(R.drawable.shoot_button_music_cover_disabled);
            this.mMusicName.setTextColor(-1711276033);
        } else {
            Glide.with(activity).load(mData.getMusic().photo)
                    .placeholder(R.drawable.shoot_button_music_normal)
                    .into(this.mMusicCover);
            this.mMusicCoverMask.setImageResource(R.drawable.shoot_button_music_cover);
        }
    }

    public void initMusicStart() {
        initmusic();
    }

    public void startMusicPlay() {
        String g = ((VideoEditData) this.mData).getMusicPath();
        if (TextUtils.isEmpty(g)) {
            stopMusicPlay();
            return;
        }
        try {
            if (this.ijkMediaPlayer == null) {
                this.ijkMediaPlayer = new IjkMediaPlayer();
            } else {
                this.ijkMediaPlayer.reset();
            }
            this.ijkMediaPlayer.setDataSource(g);
            this.ijkMediaPlayer.setVolume(0.4f, 0.4f);
            this.ijkMediaPlayer.setLooping(true);
            this.ijkMediaPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(IMediaPlayer iMediaPlayer) {
                    startPlay();
                }
            });
            this.ijkMediaPlayer.prepareAsync();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void startPlay() {
        if (this.ijkMediaPlayer != null) {
            this.ijkMediaPlayer.start();
        }
    }

    public void pausePlay() {
        if (this.ijkMediaPlayer != null) {
            this.ijkMediaPlayer.pause();
        }
    }

    public void stopMusicPlay() {
        if (this.ijkMediaPlayer != null) {
            this.ijkMediaPlayer.stop();
            this.ijkMediaPlayer.release();
            this.ijkMediaPlayer = null;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CutMusicInfoEvent cutMusicInfoEvent) {
        if (!cutMusicInfoEvent.mIsFromCamera) {
            ((VideoEditData) this.mData).setMusicPath(cutMusicInfoEvent.mCutMusicPath);
            ((VideoEditData) this.mData).setMusic(cutMusicInfoEvent.mMusic);
            initmusic();
        }
    }
}
