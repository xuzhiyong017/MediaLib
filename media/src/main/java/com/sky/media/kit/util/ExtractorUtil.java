package com.sky.media.kit.util;

import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

public class ExtractorUtil {

    public static class MediaInfo {
        public int videoTrack;
        public String videoMime;
        public MediaFormat mVideoFormat;
        public int width;
        public int height;
        public int audioTrack;
        public String audioMime;
        public MediaFormat mAudioFormat;

        private MediaInfo() {
        }
    }

    public static MediaInfo parseMediaInfo(MediaExtractor mediaExtractor) {
        MediaInfo mediaInfo = new MediaInfo();
        mediaInfo.videoTrack = -1;
        mediaInfo.audioTrack = -1;
        int trackCount = mediaExtractor.getTrackCount();
        for (int i = 0; i < trackCount; i++) {
            MediaFormat trackFormat = mediaExtractor.getTrackFormat(i);
            String string = trackFormat.getString("mime");
            if (mediaInfo.videoTrack < 0 && string.startsWith("video/")) {
                mediaInfo.videoTrack = i;
                mediaInfo.videoMime = string;
                mediaInfo.mVideoFormat = trackFormat;
                mediaInfo.width = trackFormat.getInteger("width");
                mediaInfo.height = trackFormat.getInteger("height");
            } else if (mediaInfo.audioTrack < 0 && string.startsWith("audio/")) {
                mediaInfo.audioTrack = i;
                mediaInfo.audioMime = string;
                mediaInfo.mAudioFormat = trackFormat;
            }
            if (mediaInfo.videoTrack >= 0 && mediaInfo.audioTrack >= 0) {
                break;
            }
        }
        if (mediaInfo.videoTrack >= 0 || mediaInfo.audioTrack >= 0) {
            return mediaInfo;
        }
        Log.e("MediaKit", "Not found video/audio track.");
        return null;
    }
}
