package com.sky.medialib.ui.editvideo.segment;

import android.view.View;

import com.sky.medialib.R;
import com.sky.medialib.ui.editvideo.segment.entity.VideoEditData;
import com.sky.medialib.ui.editvideo.segment.proto.ToolbarProtocol;
import com.sky.medialib.ui.kit.common.base.AppActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoToolbarSegment extends BaseSegment<VideoEditData> implements ToolbarProtocol {
    @BindView(R.id.process_rightbar)
    View mRightBar;
    @BindView(R.id.process_topbar)
    View mTopBar;

    public VideoToolbarSegment(AppActivity baseActivity, VideoEditData videoEditData) {
        super(baseActivity, videoEditData);
        ButterKnife.bind(this,baseActivity);
    }

    public void showBar() {
        this.mTopBar.setVisibility(View.VISIBLE);
        this.mRightBar.setVisibility(View.VISIBLE);
    }

    public void hideBar() {
        this.mTopBar.setVisibility(View.GONE);
        this.mRightBar.setVisibility(View.GONE);
    }
}
