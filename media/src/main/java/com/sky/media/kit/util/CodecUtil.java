package com.sky.media.kit.util;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaCodecList;
import android.os.Build.VERSION;
import android.util.Log;

import java.nio.ByteBuffer;

public class CodecUtil {
    private static int[] ColorFormatList = new int[]{21, 19};

    public static ByteBuffer getInputBuffer(MediaCodec mediaCodec, int i) {
        if (VERSION.SDK_INT >= 21) {
            return mediaCodec.getInputBuffer(i);
        }
        return mediaCodec.getInputBuffers()[i];
    }

    public static ByteBuffer getOutputBuffer(MediaCodec mediaCodec, int i) {
        if (VERSION.SDK_INT >= 21) {
            return mediaCodec.getOutputBuffer(i);
        }
        return mediaCodec.getOutputBuffers()[i];
    }

    public static MediaCodecInfo getMediaCodecInfo(String str) {
        int codecCount = MediaCodecList.getCodecCount();
        for (int i = 0; i < codecCount; i++) {
            MediaCodecInfo codecInfoAt = MediaCodecList.getCodecInfoAt(i);
            if (codecInfoAt.isEncoder()) {
                for (String equalsIgnoreCase : codecInfoAt.getSupportedTypes()) {
                    if (equalsIgnoreCase.equalsIgnoreCase(str) && CodecUtil.getColorFormatByType(codecInfoAt, str) > 0) {
                        return codecInfoAt;
                    }
                }
                continue;
            }
        }
        return null;
    }

    public static int getColorFormatByType(MediaCodecInfo mediaCodecInfo, String str) {
        int i = 0;
        try {
            Thread.currentThread().setPriority(10);
            CodecCapabilities capabilitiesForType = mediaCodecInfo.getCapabilitiesForType(str);
            for (int i2 = i; i2 < capabilitiesForType.colorFormats.length; i2++) {
                int i3 = capabilitiesForType.colorFormats[i2];
                if (CodecUtil.matchColorFormat(i3)) {
                    i = i3;
                    break;
                }
            }
            if (i == 0) {
                Log.e("MediaKit", "couldn't find a good color format for " + mediaCodecInfo.getName() + " / " + str);
            }
            return i;
        } finally {
            Thread.currentThread().setPriority(5);
        }
    }

    public static boolean matchColorFormat(int i) {
        int length = ColorFormatList != null ? ColorFormatList.length : 0;
        for (int i2 = 0; i2 < length; i2++) {
            if (ColorFormatList[i2] == i) {
                return true;
            }
        }
        return false;
    }

    public static int fixSize(int i) {
        if (i % 16 > 0) {
            return ((i / 16) * 16) + 16;
        }
        return i;
    }
}
