package com.sky.media.kit.util;

import android.media.MediaFormat;


public class MediaHelper {
    public static int DEFAULT_SIMPLE_HZ = 44100;
    public static int Default_ChannelCount = 2;

    public static int getBitrate(int width, int height) {
        return (int) ((5.0f * ((float) width)) * ((float) height));
    }

    public static MediaFormat getVideoFormat(int width, int height, int colorFormat) {
        return MediaHelper.getVideoFormat(width, height, MediaHelper.getBitrate(width, height), colorFormat);
    }

    public static MediaFormat getVideoFormat(int width, int height, int bitrate, int colorFormat) {
        MediaFormat createVideoFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height);
        createVideoFormat.setInteger("color-format", colorFormat);
        createVideoFormat.setInteger("bitrate", bitrate);
        createVideoFormat.setInteger("frame-rate", 25);
        createVideoFormat.setInteger("i-frame-interval", 1);
        return createVideoFormat;
    }

    public static MediaFormat getAudioFormat(int sampleRate, int channelCount) {
        MediaFormat createAudioFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, sampleRate, channelCount);
        createAudioFormat.setInteger("aac-profile", 2);
        createAudioFormat.setInteger("channel-mask", 12);
        createAudioFormat.setInteger("channel-count", channelCount);
        createAudioFormat.setInteger("bitrate", 128000);
        createAudioFormat.setInteger("max-input-size", 262144);
        return createAudioFormat;
    }

    public static int getColorFormat() {
        return CodecUtil.getColorFormatByType(CodecUtil.getMediaCodecInfo(MediaFormat.MIMETYPE_VIDEO_AVC), "video/avc");
    }
}
