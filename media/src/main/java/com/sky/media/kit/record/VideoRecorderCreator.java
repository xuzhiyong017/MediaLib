package com.sky.media.kit.record;

import android.content.Context;

import com.sky.media.kit.record.encoder.VideoRecorderCompat;
import com.sky.media.kit.util.DeviceUtil;
import com.weibo.soundtouch.SoundTouch;

public class VideoRecorderCreator {

    public static IVideoRecorder buildRecorder(Context context, int width, int height, String path, SoundTouch soundTouch) {
        if (DeviceUtil.isMateX()) {
            return new VideoRecorderCompat(context, width, height, path, null);
        }
        return new VideoRecorderCompat(context, width, height, path, soundTouch);
    }
}
