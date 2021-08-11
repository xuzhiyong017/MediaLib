package com.sky.medialib.util;

import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.sky.media.kit.BaseMediaApplication;


public class AppBroadcastHelper {
    public static void sendBroadcast(Intent intent) {
        LocalBroadcastManager.getInstance(BaseMediaApplication.sContext).sendBroadcast(intent);
    }
}
