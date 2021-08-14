package com.sky.medialib.ui.editvideo.segment;

import android.view.View;

import com.sky.medialib.R;
import com.sky.medialib.ui.editvideo.segment.entity.VideoEditData;
import com.sky.medialib.ui.editvideo.segment.proto.PublishLayoutProtocol;
import com.sky.medialib.ui.kit.common.base.AppActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author: xuzhiyong
 * @date: 2021/8/14  下午4:49
 * @Email: 18971269648@163.com
 * @description:
 */

public class PublishSegment extends BaseSegment<VideoEditData> implements PublishLayoutProtocol {

    @BindView(R.id.publish)
    View mPublish;
    @BindView(R.id.publish_layout)
    View mPublishLayout;
   @BindView(R.id.message_bar)
    View mMessageBar;

    public PublishSegment(AppActivity baseActivity, VideoEditData videoEditData) {
        super(baseActivity, videoEditData);
        ButterKnife.bind(this,baseActivity);
    }

    @Override
    public void hide() {
        mPublish.setVisibility(View.GONE);
        mMessageBar.setVisibility(View.GONE);
        mPublishLayout.setVisibility(View.GONE);
    }

    @Override
    public void show() {
        mPublish.setVisibility(View.VISIBLE);
        mMessageBar.setVisibility(View.VISIBLE);
        mPublishLayout.setVisibility(View.VISIBLE);
    }
}
