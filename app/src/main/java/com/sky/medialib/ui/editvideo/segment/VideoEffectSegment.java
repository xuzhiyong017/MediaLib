package com.sky.medialib.ui.editvideo.segment;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.sky.media.image.core.base.BaseRender;
import com.sky.media.image.core.filter.Adjuster;
import com.sky.media.image.core.filter.Filter;
import com.sky.media.image.core.render.SwitchRender;
import com.sky.media.kit.base.BaseActivity;
import com.sky.media.kit.filter.BuffingTool;
import com.sky.media.kit.filter.WhiteningTool;
import com.sky.media.kit.model.FilterExt;
import com.sky.media.kit.video.VideoSequenceHelper;
import com.sky.medialib.R;
import com.sky.medialib.ui.editvideo.segment.entity.VideoDraft;
import com.sky.medialib.ui.editvideo.segment.entity.VideoDraftEffect;
import com.sky.medialib.ui.editvideo.segment.entity.VideoEditData;
import com.sky.medialib.ui.editvideo.segment.proto.VideoPlayerProtocol;
import com.sky.medialib.ui.kit.common.base.AppActivity;
import com.sky.medialib.ui.kit.manager.ToolFilterManager;
import com.sky.medialib.ui.kit.manager.VideoEffectManager;
import com.sky.medialib.ui.kit.media.VideoProcessCenter;
import com.sky.medialib.ui.kit.model.VideoEffect;
import com.sky.medialib.ui.kit.view.SequenceSeekBar;
import com.sky.medialib.util.ToastUtils;
import  com.sky.medialib.ui.editvideo.effect.VideoEffectActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoEffectSegment extends BaseSegment<VideoEditData> {

    private VideoPlayerProtocol videoPlayerProtocol;
    @BindView(R.id.effect_btn)
    TextView mEffectBtn;

    class OnVideoProcessListener implements VideoProcessCenter.OnVideoProcessListener {
        OnVideoProcessListener() {
        }

        public void OnProcessSuccess(String str, String str2) {
            ((VideoEditData) VideoEffectSegment.this.mData).setVideoReversePath(str2);
            ((VideoEditData) VideoEffectSegment.this.mData).setReverse(true);
            VideoEffectSegment.this.videoPlayerProtocol.initPlayer();
        }

        public void onStart(String str) {
            ToastUtils.INSTANCE.showToast("倒放特效正在处理中");
        }

        public void onFailed(String str) {
        }
    }

    public VideoEffectSegment(AppActivity baseActivity, VideoEditData videoEditData) {
        super(baseActivity, videoEditData);
        ButterKnife.bind((Object) this, (Activity) baseActivity);
        init();
    }

    public void setVideoPlayerProtocol(VideoPlayerProtocol videoPlayerProtocol) {
        this.videoPlayerProtocol = videoPlayerProtocol;
    }

    private void init() {
        this.mEffectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                VideoEffectActivity.EffectConfig aVar = new VideoEffectActivity.EffectConfig();
                aVar.isKeepVoice = mData.isKeepVoice();
                aVar.videoPath = mData.getVideoPath();
                aVar.musicPath = mData.getMusicPath();
                for (Filter filter : mData.processExt.getUsedFilters()) {
                    Adjuster adjuster = filter.getAdjuster();
                    if (filter instanceof BuffingTool) {
                        aVar.hasBuffingTool = true;
                        aVar.buffingLevel = adjuster.getProgress();
                    }else if (filter instanceof WhiteningTool) {
                        aVar.hasWhiteningTool = true;
                        aVar.whiteningLevel = adjuster.getProgress();
                    } else if (filter instanceof FilterExt) {
                        BaseRender render = adjuster.getMRender();
                        if (render instanceof SwitchRender) {
                            Iterator<FilterExt> it = ToolFilterManager.INSTANCE.getEditVideoFilterList().iterator();
                            while (true) {
                                if (!it.hasNext()) {
                                    break;
                                }
                                Filter next = it.next();
                                if ((next instanceof FilterExt) && next.getAdjuster().getMRender() == ((SwitchRender) render).getCurrentRender()) {
                                    aVar.hasSwitchRender = true;
                                    aVar.switchRenderId = ((FilterExt) next).getMId();
                                    break;
                                }
                            }
                        }
                    }
                }
                VideoEffectActivity.launch(activity, aVar, 5);
            }
        });
    }

    public void initEffect() {
        VideoEffectManager.VideoEffectExt a = buildEffectExt(((VideoEditData) this.mData).getVideoDraft());
        if (a != null) {
            VideoEffectManager.getInstance().setVideoEffectExt(a);
            ((VideoEditData) this.mData).processExt.clear();
            for (SequenceSeekBar.SequenceExt a2 : a.sequenceExtList) {
                ((VideoEditData) this.mData).processExt.pushSequence((VideoSequenceHelper.BaseSequence) a2);
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();
        VideoEffectManager.getInstance().release();
    }

    private VideoEffectManager.VideoEffectExt buildEffectExt(VideoDraft videoDraft) {
        if (!videoDraft.isReverse && (videoDraft.effects == null || videoDraft.effects.isEmpty())) {
            return null;
        }
        VideoEffectManager.VideoEffectExt videoEffectExt = new VideoEffectManager.VideoEffectExt();
        if (videoDraft.isReverse) {
            videoEffectExt.videoEffect = (VideoEffect) VideoEffectManager.getInstance().getTimeEffectList().get(1);
        } else {
            videoEffectExt.videoEffect = (VideoEffect) VideoEffectManager.getInstance().getTimeEffectList().get(0);
        }
        List<VideoDraftEffect> list = videoDraft.effects;
        if (list != null) {
            for (VideoDraftEffect videoDraftEffect : list) {
                SequenceSeekBar.SequenceExt sequenceExt = new SequenceSeekBar.SequenceExt();
                sequenceExt.start = videoDraftEffect.start;
                sequenceExt.end = videoDraftEffect.end;
                sequenceExt.total = videoDraftEffect.total;
                sequenceExt.isReverse = videoDraftEffect.isReverse;
                for (Integer intValue : videoDraftEffect.filterIds) {
                    VideoEffect a = VideoEffectManager.getInstance().getVideoEffectByFilterId(intValue.intValue());
                    if (a != null) {
                        sequenceExt.filter.add(VideoEffectManager.getInstance().copyFilterExt(a.getFilter()));
                        sequenceExt.color = a.getColor();
                    }
                }
                videoEffectExt.sequenceExtList.add(sequenceExt);
            }
        }
        return videoEffectExt;
    }

    private void setVideoEffectExt(VideoEffectManager.VideoEffectExt videoEffectExt) {
        List arrayList = new ArrayList();
        for (SequenceSeekBar.SequenceExt sequenceExt : videoEffectExt.sequenceExtList) {
            VideoDraftEffect videoDraftEffect = new VideoDraftEffect();
            videoDraftEffect.start = sequenceExt.start;
            videoDraftEffect.end = sequenceExt.end;
            videoDraftEffect.total = sequenceExt.total;
            videoDraftEffect.isReverse = sequenceExt.isReverse;
            videoDraftEffect.filterIds = new ArrayList();
            for (Filter filter : sequenceExt.filter) {
                if (filter instanceof FilterExt) {
                    videoDraftEffect.filterIds.add(Integer.valueOf(((FilterExt) filter).getMId()));
                }
            }
            arrayList.add(videoDraftEffect);
        }
        ((VideoEditData) this.mData).setEffectList(arrayList);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode != 0) {
            switch (requestCode) {
                case 5:
                    VideoEffectManager.VideoEffectExt d = VideoEffectManager.getInstance().getVideoEffectExt();
                    if (d != null) {
                        setVideoEffectExt(d);
                        ((VideoEditData) this.mData).processExt.clear();
                        for (SequenceSeekBar.SequenceExt a : d.sequenceExtList) {
                            ((VideoEditData) this.mData).processExt.pushSequence((VideoSequenceHelper.BaseSequence) a);
                        }
                        VideoEffect videoEffect = d.videoEffect;
                        if (videoEffect == null) {
                            return;
                        }
                        if (videoEffect.isReverse()) {
                            VideoProcessCenter.getInstance().processVideo(((VideoEditData) this.mData).getVideoPath(), new OnVideoProcessListener());
                            return;
                        }
                        ((VideoEditData) this.mData).setReverse(false);
                        this.videoPlayerProtocol.initPlayer();
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }
}
