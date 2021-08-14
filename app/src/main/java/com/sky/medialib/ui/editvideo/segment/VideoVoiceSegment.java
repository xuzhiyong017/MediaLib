package com.sky.medialib.ui.editvideo.segment;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import com.sky.medialib.App;
import com.sky.medialib.R;
import com.sky.medialib.ui.editvideo.segment.entity.VideoEditData;
import com.sky.medialib.ui.kit.common.base.AppActivity;
import com.sky.medialib.util.PixelUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoVoiceSegment extends BaseSegment<VideoEditData> {
    @BindView(R.id.sound_btn)
    TextView mSoundButton;

    public VideoVoiceSegment(AppActivity baseActivity, VideoEditData videoEditData) {
        super(baseActivity, videoEditData);
        ButterKnife.bind((Object) this, (Activity) baseActivity);
        init();
    }

    private void init() {
        this.mSoundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mData.setKeepVoice(!mData.isKeepVoice());
                updateVoiceStatue();
            }
        });
    }


    private void updateVoiceStatue() {
        int a = PixelUtil.dip2px(42.0f);
        Drawable drawable;
        if (mData.isKeepVoice()) {
            drawable = activity.getResources().getDrawable(R.drawable.selector_video_sound_on);
            drawable.setBounds(0, 0, a, a);
            mSoundButton.setCompoundDrawables(null, drawable, null, null);
            mData.processExt.openVoice();
        }else{
            drawable = activity.getResources().getDrawable(R.drawable.selector_video_sound_off);
            drawable.setBounds(0, 0, a, a);
            mSoundButton.setCompoundDrawables(null, drawable, null, null);
            mData.processExt.closeVoice();
        }

    }

    public void initVoiceStart() {
        updateVoiceStatue();
    }
}
