package com.sky.media.kit.util;

import android.text.TextUtils;

import java.io.File;

public class Util {
    public static void deleteFile(String str) {
        if (!TextUtils.isEmpty(str)) {
            File file = new File(str);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public static int getInteger(String str, int i) {
        try {
            return Integer.parseInt(str);
        } catch (Throwable e) {
            e.printStackTrace();
            return i;
        }
    }

    public static int getInteger(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Throwable e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static long getLong(String str) {
        try {
            return Long.parseLong(str);
        } catch (Throwable e) {
            e.printStackTrace();
            return 0;
        }
    }
}
