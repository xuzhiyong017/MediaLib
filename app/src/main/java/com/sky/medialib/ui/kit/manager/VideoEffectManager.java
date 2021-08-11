package com.sky.medialib.ui.kit.manager;


import com.sky.media.image.core.base.BaseRender;
import com.sky.media.image.core.filter.Adjuster;
import com.sky.media.image.core.filter.Filter;
import com.sky.media.kit.model.FilterExt;
import com.sky.media.kit.render.videorender.GridRender;
import com.sky.media.kit.render.videorender.LightedEdgeRender;
import com.sky.media.kit.render.videorender.MirrorRender;
import com.sky.media.kit.render.videorender.ShakeRender;
import com.sky.media.kit.render.videorender.SobelEdgeDetectionRender;
import com.sky.media.kit.render.videorender.SoulRender;
import com.sky.medialib.R;
import com.sky.medialib.ui.kit.model.VideoEffect;
import com.sky.medialib.ui.kit.view.SequenceSeekBar;
import com.sky.medialib.ui.kit.view.SequenceSeekBar.*;

import java.util.ArrayList;
import java.util.List;

public class VideoEffectManager {

    private static VideoEffectManager sInstance = new VideoEffectManager();
    private List<VideoEffect> filterEffectList = new ArrayList();
    private List<VideoEffect> timeEffectList = new ArrayList();
    private VideoEffectExt videoEffectExt;

    public static class VideoEffectExt {
        public VideoEffect videoEffect;
        public List<SequenceExt> sequenceExtList = new ArrayList();
    }

    private VideoEffectManager() {
        FilterExt filterExt = new FilterExt();
        filterExt.setName("正常");
        filterExt.setMIconResource(R.drawable.gif_normal);
        VideoEffect videoEffect = new VideoEffect(1);
        videoEffect.setReverse(false);
        videoEffect.setAlias("normal");
        videoEffect.setFilter(filterExt);
        videoEffect.setColor(-855679137);
        this.timeEffectList.add(videoEffect);
        filterExt = new FilterExt();
        filterExt.setName("倒放");
        filterExt.setMIconResource(R.drawable.gif_reversed);
        videoEffect = new VideoEffect(1);
        videoEffect.setReverse(true);
        videoEffect.setAlias("upend");
        videoEffect.setFilter(filterExt);
        videoEffect.setColor(-872366593);
        this.timeEffectList.add(videoEffect);
        filterExt = new FilterExt();
        filterExt.setMId(200001);
        filterExt.setName("灵魂出窍");
        filterExt.setMIconResource(R.drawable.gif_0);
        filterExt.setAdjuster(new Adjuster(new SoulRender()));
        videoEffect = new VideoEffect(0);
        videoEffect.setAlias("linghun");
        videoEffect.setFilter(filterExt);
        videoEffect.setColor(-256);
        this.filterEffectList.add(videoEffect);
        filterExt = new FilterExt();
        filterExt.setMId(200002);
        filterExt.setName("动感分格");
        filterExt.setMIconResource(R.drawable.gif_1);
        filterExt.setAdjuster(new Adjuster(new GridRender()));
        videoEffect = new VideoEffect(0);
        videoEffect.setAlias("fenge");
        videoEffect.setFilter(filterExt);
        videoEffect.setColor(-65281);
        this.filterEffectList.add(videoEffect);
        filterExt = new FilterExt();
        filterExt.setMId(200003);
        filterExt.setName("暗黑幻境");
        filterExt.setMIconResource(R.drawable.gif_2);
        filterExt.setAdjuster(new Adjuster(new SobelEdgeDetectionRender()));
        videoEffect = new VideoEffect(0);
        videoEffect.setAlias("huanjing");
        videoEffect.setFilter(filterExt);
        videoEffect.setColor(-16776961);
        this.filterEffectList.add(videoEffect);
        filterExt = new FilterExt();
        filterExt.setMId(200004);
        filterExt.setName("酷炫抖动");
        filterExt.setMIconResource(R.drawable.gif_3);
        filterExt.setAdjuster(new Adjuster(new ShakeRender()));
        videoEffect = new VideoEffect(0);
        videoEffect.setAlias("doudong");
        videoEffect.setFilter(filterExt);
        videoEffect.setColor(-16711936);
        this.filterEffectList.add(videoEffect);
        filterExt = new FilterExt();
        filterExt.setMId(200005);
        filterExt.setName("神秘蓝光");
        filterExt.setMIconResource(R.drawable.gif_4);
        filterExt.setAdjuster(new Adjuster(new LightedEdgeRender()));
        videoEffect = new VideoEffect(0);
        videoEffect.setAlias("languang");
        videoEffect.setFilter(filterExt);
        videoEffect.setColor(-16711681);
        this.filterEffectList.add(videoEffect);
        filterExt = new FilterExt();
        filterExt.setMId(200006);
        filterExt.setName("虚拟镜像");
        filterExt.setMIconResource(R.drawable.gif_5);
        filterExt.setAdjuster(new Adjuster(new MirrorRender()));
        videoEffect = new VideoEffect(0);
        videoEffect.setAlias("jingxiang");
        videoEffect.setFilter(filterExt);
        videoEffect.setColor(-65536);
        this.filterEffectList.add(videoEffect);
    }

    public boolean isNoChange(VideoEffectExt videoEffectExt, VideoEffectExt videoEffectExt2) {
        if (videoEffectExt2 == null) {
            boolean z;
            if (!videoEffectExt.sequenceExtList.isEmpty() || (videoEffectExt.videoEffect != null && videoEffectExt.videoEffect.isReverse())) {
                z = false;
            } else {
                z = true;
            }
            return z;
        } else if (videoEffectExt.sequenceExtList.size() != videoEffectExt2.sequenceExtList.size() || videoEffectExt.videoEffect.isReverse() != videoEffectExt2.videoEffect.isReverse()) {
            return false;
        } else {
            for (int i = 0; i < videoEffectExt.sequenceExtList.size(); i++) {
                SequenceExt sequenceExt = (SequenceExt) videoEffectExt.sequenceExtList.get(i);
                SequenceExt sequenceExt2 = (SequenceExt) videoEffectExt2.sequenceExtList.get(i);
                if (sequenceExt.start != sequenceExt2.start || sequenceExt.end != sequenceExt2.end || sequenceExt.color != sequenceExt2.color) {
                    return false;
                }
            }
            return true;
        }
    }

    public VideoEffect getVideoEffectByFilterId(int i) {
        for (VideoEffect videoEffect : this.filterEffectList) {
            if (videoEffect.getFilter().getMId() == i) {
                return videoEffect;
            }
        }
        return null;
    }

    public SequenceExt copySequenceExt(SequenceExt sequenceExt) {
        SequenceExt sequenceExt2 = new SequenceExt();
        sequenceExt2.start = sequenceExt.start;
        sequenceExt2.end = sequenceExt.end;
        sequenceExt2.isReverse = sequenceExt.isReverse;
        sequenceExt2.total = sequenceExt.total;
        sequenceExt2.color = sequenceExt.color;
        for (Filter filter : sequenceExt.filter) {
            if (filter instanceof FilterExt) {
                sequenceExt2.filter.add(copyFilterExt((FilterExt) filter));
            }
        }
        return sequenceExt2;
    }

    public FilterExt copyFilterExt(FilterExt filterExt) {
        FilterExt filterExt2 = new FilterExt();
        filterExt2.setMId(filterExt.getMId());
        filterExt2.setName(filterExt.getName());
        filterExt2.setMIconResource(filterExt.getIconResource());
        BaseRender render = filterExt.getAdjuster().getMRender();
        if (render instanceof LightedEdgeRender) {
            filterExt2.setAdjuster(new Adjuster(new LightedEdgeRender()));
        } else if (render instanceof SobelEdgeDetectionRender) {
            filterExt2.setAdjuster(new Adjuster(new SobelEdgeDetectionRender()));
        } else if (render instanceof GridRender) {
            filterExt2.setAdjuster(new Adjuster(new GridRender()));
        } else if (render instanceof ShakeRender) {
            filterExt2.setAdjuster(new Adjuster(new ShakeRender()));
        } else if (render instanceof SoulRender) {
            filterExt2.setAdjuster(new Adjuster(new SoulRender()));
        } else if (render instanceof MirrorRender) {
            filterExt2.setAdjuster(new Adjuster(new MirrorRender()));
        }
        return filterExt2;
    }

    public static VideoEffectManager getInstance() {
        return sInstance;
    }

    public List<VideoEffect> getFilterEffectList() {
        return this.filterEffectList;
    }

    public List<VideoEffect> getTimeEffectList() {
        return this.timeEffectList;
    }

    public void setVideoEffectExt(VideoEffectExt videoEffectExt) {
        this.videoEffectExt = videoEffectExt;
    }

    public VideoEffectExt getVideoEffectExt() {
        return this.videoEffectExt;
    }

    public void release() {
        this.videoEffectExt = null;
    }
}
