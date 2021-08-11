package com.sky.medialib.ui.kit.media;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.text.TextUtils;


import com.sky.media.ffmpeg.executor.FFmpegExecutor;
import com.sky.media.image.core.base.BaseRender;
import com.sky.media.image.core.filter.Adjuster;
import com.sky.media.image.core.filter.Filter;
import com.sky.media.image.core.filter.IAdjustable;
import com.sky.media.image.core.render.SwitchRender;
import com.sky.media.kit.filter.BuffingTool;
import com.sky.media.kit.filter.WhiteningTool;
import com.sky.media.kit.mediakit.MediaKit;
import com.sky.media.kit.mediakit.MediaKitCompat;
import com.sky.media.kit.mediakit.Metadata;
import com.sky.media.kit.model.FilterExt;
import com.sky.media.kit.render.sticker.WatermarkRenderCreator;
import com.sky.media.kit.transfer.OutputSurface;
import com.sky.media.kit.video.OffscreenVideoProcess;
import com.sky.media.kit.video.OffscreenVideoRender;
import com.sky.media.kit.video.VideoSequenceHelper;
import com.sky.media.kit.video.VideoSequenceHelper.*;
import com.sky.medialib.ui.kit.effect.EffectRender;
import com.sky.medialib.ui.kit.manager.ToolFilterManager;
import com.sky.medialib.ui.kit.manager.VideoEffectManager;
import com.sky.medialib.ui.kit.view.SequenceSeekBar.*;
import com.sky.medialib.util.FileUtil;
import com.sky.medialib.util.Storage;
import com.sky.medialib.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class MediaKitExt {

    public static boolean processVideoWithFilter(Context context, Stack<VideoSequenceHelper.BaseSequence> stack, List<Filter> list, String str, String str2, List<Watermark> list2, EffectRender effectRender) {
        FilterExt filterExt;
        Metadata a = MediaKit.getVideoInfo(str);
        final OffscreenVideoProcess offscreenVideoProcess = new OffscreenVideoProcess(a.width, a.height);
        List arrayList = new ArrayList();
        SequenceExt sequenceExt = new SequenceExt();
        sequenceExt.start = 0;
        sequenceExt.end = a.duration * 1000;
        if (Util.isNotEmptyList((List) list2)) {
            for (Watermark watermark : list2) {
                Adjuster adjuster = new Adjuster(WatermarkRenderCreator.createWatermarkRender(context, watermark.path, watermark.x, watermark.y, watermark.scale));
                adjuster.setInitProgress(100);
                filterExt = new FilterExt();
                filterExt.setAdjuster(adjuster);
                sequenceExt.filter.add(filterExt);
            }
        }
        if (effectRender != null) {
            Adjuster adjuster2 = new Adjuster(effectRender);
            adjuster2.setInitProgress(100);
            FilterExt filterExt2 = new FilterExt();
            filterExt2.setAdjuster(adjuster2);
            sequenceExt.filter.add(filterExt2);
        }
        if (Util.isNotEmptyList((List) list2) || effectRender != null) {
            arrayList.add(sequenceExt);
        }
        Iterator it = stack.iterator();
        while (it.hasNext()) {
            BaseSequence a2 = VideoEffectManager.getInstance().copySequenceExt((SequenceExt) ((BaseSequence) it.next()));
            if (Util.isNotEmptyList((List) list2)) {
                for (Watermark watermark2 : list2) {
                    Adjuster adjuster3 = new Adjuster(WatermarkRenderCreator.createWatermarkRender(context, watermark2.path, watermark2.x, watermark2.y, watermark2.scale));
                    adjuster3.setInitProgress(100);
                    filterExt = new FilterExt();
                    filterExt.setAdjuster(adjuster3);
                    a2.filter.add(filterExt);
                }
            }
            arrayList.add(a2);
        }
        offscreenVideoProcess.addAllSequence(arrayList);
        List arrayList2 = new ArrayList();
        for (Filter filter2 : list) {
            Filter filter3 = null;
            Adjuster adjuster4 = filter2.getAdjuster();
            if (filter2 instanceof BuffingTool) {
                filter3 = ToolFilterManager.INSTANCE.getVideoBuffingTool();
            } else if (filter2 instanceof WhiteningTool) {
                filter3 = ToolFilterManager.INSTANCE.getVideoWhiteningTool();
            } else if (filter2 instanceof FilterExt) {
                BaseRender render = adjuster4.getMRender();
                if (render instanceof SwitchRender) {
                    for (Filter filter22 : ToolFilterManager.INSTANCE.getEditVideoFilterList()) {
                        if ((filter22 instanceof FilterExt) && filter22.getAdjuster().getMRender() == ((SwitchRender) render).getCurrentRender()) {
                            filter3 = ToolFilterManager.INSTANCE.getEditVideoFilterById(((FilterExt) filter22).getMId());
                            break;
                        }
                    }
                    filter3 = null;
                } else {
                    filter3 = ToolFilterManager.INSTANCE.getEditVideoFilterById(((FilterExt) filter2).getMId());
                }
            }
            if (filter3 != null) {
                BaseRender render2 = filter3.getAdjuster().getMRender();
                if (render2 instanceof IAdjustable) {
                    ((IAdjustable) render2).adjust(adjuster4.getProgress(), adjuster4.getStart(), adjuster4.getEnd());
                }
                arrayList2.add(filter3);
            }
        }
        offscreenVideoProcess.initFilters(arrayList2);
        final OffscreenVideoRender a3 = offscreenVideoProcess.getOffscreenVideoRender();
        boolean a4 = MediaKitCompat.processVideo(str, str2, new OutputSurface.OnProcessCallback() {
            public void processVideo(long j) {
                offscreenVideoProcess.onDrawFilterTime(j);
                a3.onDrawFrame();
            }

            public SurfaceTexture getSurfaceTexture() {
                return a3.getSurfaceTexture();
            }
        }, context);
        offscreenVideoProcess.destroy();
        return a4;
    }

    public static boolean doMusicAndVideo(String str, String str2, String str3, boolean z, Context context) {
        boolean z2 = false;
        if (!TextUtils.isEmpty(str3)) {
            long d = MediaKit.getVideoDuration(str);
            String a = Storage.getFilePathByType(3);
            String str4 = a + Util.getCurTime() + "_temp.mp3";
            a = a + Util.getCurTime() + "_temp.aac";
            if (MediaKit.alignAudioToVideoDuration(str3, str4, d)) {
                if (z && MediaKit.containsAudio(str)) {
                    if (FFmpegExecutor.getInstance(context).mixAudioToVideo(str, str4, str2) == 0) {
                        z2 = true;
                    }
                    if (!z2 && FFmpegExecutor.getInstance(context).audioToAAC(str4, a) == 0) {
                        z2 = MediaKitCompat.mergeAudioAndVideo(str, a, str2, context);
                    }
                } else if (FFmpegExecutor.getInstance(context).audioToAAC(str4, a) == 0) {
                    z2 = MediaKitCompat.mergeAudioAndVideo(str, a, str2, context);
                }
            }
            FileUtil.INSTANCE.deleteFiles(str4);
            FileUtil.INSTANCE.deleteFiles(a);
            return z2;
        } else if (z) {
            return FileUtil.INSTANCE.copy(str, str2);
        } else {
            if (FFmpegExecutor.getInstance(context).doPureVideo(str, str2) != 0) {
                return false;
            }
            return true;
        }
    }

    public static boolean covertMusicToAACAndMergeVideo(String str, String str2, String str3, Context context) {
        boolean z = false;
        String a = Storage.getFilePathByType(3);
        long d = MediaKit.getVideoDuration(str);
        String str4 = a + Util.getCurTime() + "_temp.mp3";
        a = a + Util.getCurTime() + "_temp.aac";
        if (MediaKit.alignAudioToVideoDuration(str3, str4, d) && FFmpegExecutor.getInstance(context).audioToAAC(str4, a) == 0) {
            z = MediaKitCompat.mergeAudioAndVideo(str, a, str2, context);
        }
        FileUtil.INSTANCE.deleteFiles(str4);
        FileUtil.INSTANCE.deleteFiles(a);
        return z;
    }

    public static String concatVideo(List<String> list, HashMap<String, Integer> hashMap, Context context) {
        int i;
        int i2 = 0;
        String str = Storage.getFilePathByType(3) + Util.getCurTime() + ".mp4";
        if (Util.isNotEmptyList((List) list)) {
            int size = list.size();
            String[] strArr = new String[size];
            for (int i3 = 0; i3 < size; i3++) {
                String str2 = (String) list.get(i3);
                strArr[i3] = MediaKitExt.covertSpeedToVideo(str2, ((Integer) hashMap.get(str2)).intValue(), context);
            }
            if (FFmpegExecutor.getInstance(context).concatVideoSegment(str, strArr) == 0) {
                i = 1;
            } else {
                i = 0;
            }
            int length = strArr.length;
            while (i2 < length) {
                String str3 = strArr[i2];
                if (!list.contains(str3)) {
                    FileUtil.INSTANCE.deleteFiles(str3);
                }
                i2++;
            }
        } else {
            i = 0;
        }
        return i != 0 ? str : "";
    }

    public static String covertSpeedToVideo(String str, int speedMode, Context context) {
        String replace = str.replace(".mp4", "_" + Util.getCurTime() + ".mp4");
        float speed = MediaKitExt.getSpeedByType(speedMode);
        return (speed == 1.0f || !MediaKitCompat.covertSpeedToVideo(str, replace, speed, context)) ? str : replace;
    }

    public static float getSpeedByType(int speedMode) {
        switch (speedMode) {
            case 1:
                return 0.25f;
            case 2:
                return 0.5f;
            case 4:
                return 2.0f;
            case 5:
                return 4.0f;
            default:
                return 1.0f;
        }
    }
}
