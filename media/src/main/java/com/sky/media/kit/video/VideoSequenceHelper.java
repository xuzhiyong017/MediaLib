package com.sky.media.kit.video;

import android.util.Pair;

import com.sky.media.image.core.base.BaseRender;
import com.sky.media.image.core.filter.Adjuster;
import com.sky.media.image.core.filter.Filter;
import com.sky.media.image.core.render.EmptyRender;
import com.sky.media.image.core.render.GroupRender;
import com.sky.media.image.core.render.MultiInputRender;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class VideoSequenceHelper {

    private Stack<BaseSequence> mSequences = new Stack();
    private int width;
    private int height;
    private BaseSequence mCurrentSequence;
    private BaseRender mCurrentRender;

    public static class BaseSequence {
        public long start;
        public long end;
        public List<Filter> filter = new ArrayList();
    }

    public VideoSequenceHelper(int i, int i2) {
        this.width = i;
        this.height = i2;
    }

    public void setWidth(int i) {
        this.width = i;
    }

    public void setHeight(int i) {
        this.height = i;
    }

    public void replaceList(List<BaseSequence> list) {
        this.mSequences.clear();
        this.mSequences.addAll(list);
    }

    public Stack<BaseSequence> getSequences() {
        return this.mSequences;
    }

    public void clear() {
        this.mSequences.clear();
    }

    public void pushSequence(BaseSequence baseSequence) {
        this.mSequences.push(baseSequence);
    }

    public BaseSequence popSequence() {
        if (this.mSequences.isEmpty()) {
            return null;
        }
        return (BaseSequence) this.mSequences.pop();
    }

    public BaseSequence getSequence(long timeStamp) {
        Pair b = getCurrentSequence(timeStamp);
        if (b.first == null) {
            this.mCurrentRender = null;
        } else if (b.second != null) {
            this.mCurrentRender = (BaseRender) b.second;
        }
        return (BaseSequence) b.first;
    }

    public BaseRender getRender() {
        return this.mCurrentRender;
    }

    private boolean isSameSequence(BaseSequence baseSequence, BaseSequence baseSequence2) {
        if (baseSequence == null || baseSequence2 == null) {
            return false;
        }
        List<Filter> list = baseSequence.filter;
        List<Filter> list2 = baseSequence2.filter;
        for (Filter filter : list) {
            for (Filter adjuster : list2) {
                if (!filter.getAdjuster().getMRender().getClass().getName().equals(adjuster.getAdjuster().getMRender().getClass().getName())) {
                    return false;
                }
            }
        }
        return true;
    }

    private Pair<BaseSequence, BaseRender> getCurrentSequence(long timeStamp) {
        int size;
        BaseSequence baseSequence = null;
        BaseRender groupRender = null;
        int i = 0;
        for (size = this.mSequences.size() - 1; size >= 0; size--) {
            BaseSequence baseSequence2 = this.mSequences.get(size);
            if (baseSequence2.start <= timeStamp && baseSequence2.end > timeStamp) {
                baseSequence = baseSequence2;
                break;
            }
        }

        if (baseSequence == null && this.mCurrentSequence != null) {
            groupRender = null;
        } else if (baseSequence == null) {
            groupRender = null;
        } else if (baseSequence.equals(this.mCurrentSequence) || isSameSequence(baseSequence, this.mCurrentSequence)) {
            groupRender = null;
        } else {
            BaseRender render;
            List list = baseSequence.filter;
            List arrayList = new ArrayList();
            for (size = 0; size < list.size(); size++) {
                Adjuster adjuster = ((Filter) list.get(size)).getAdjuster();
                if (adjuster != null) {
                    render = adjuster.getMRender();
                    if (!(render == null || arrayList.contains(render))) {
                        render.clearNextRenders();
                        render.reInitialize();
                        if (render instanceof MultiInputRender) {
                            ((MultiInputRender) render).clearRegisteredFilterLocations();
                        }
                        arrayList.add(render);
                    }
                }
            }
            BaseRender basicRender;
            if (list.isEmpty()) {
                groupRender = new EmptyRender();
                groupRender.setRenderSize(this.width, this.height);
            } else if (list.size() == 1) {
                GroupRender groupRender1 = new GroupRender();
                basicRender = (BaseRender) arrayList.get(0);
                basicRender.addNextRender(groupRender1);
                groupRender1.registerInitialFilter(basicRender);
                groupRender1.registerTerminalFilter(basicRender);
                groupRender1.setRenderSize(this.width, this.height);
                groupRender = groupRender1;
            } else if (list.size() == 2) {
                GroupRender groupRender2 = new GroupRender();
                basicRender = (BaseRender) arrayList.get(0);
                render = (BaseRender) arrayList.get(1);
                basicRender.addNextRender(render);
                render.addNextRender(groupRender2);
                groupRender2.registerInitialFilter(basicRender);
                groupRender2.registerTerminalFilter(render);
                groupRender2.setRenderSize(this.width, this.height);
                groupRender = groupRender2;
            } else {
                GroupRender groupRender3 = new GroupRender();
                render = (BaseRender) arrayList.get(list.size() - 1);
                groupRender3.registerInitialFilter((BaseRender) arrayList.get(0));
                while (i < arrayList.size() - 1) {
                    basicRender = (BaseRender) arrayList.get(i);
                    basicRender.addNextRender((BaseRender) arrayList.get(i + 1));
                    groupRender3.registerFilter(basicRender);
                    i++;
                }
                render.addNextRender(groupRender3);
                groupRender3.registerTerminalFilter(render);
                groupRender3.setRenderSize(this.width, this.height);
                groupRender = groupRender3;
            }
        }
        this.mCurrentSequence = baseSequence;
        return new Pair(baseSequence, groupRender);
    }
}
