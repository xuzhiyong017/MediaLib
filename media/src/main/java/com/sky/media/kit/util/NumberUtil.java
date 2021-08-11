package com.sky.media.kit.util;

import android.text.TextUtils;

public class NumberUtil {
    public static int parseInt(String str) {
        return parseInt(str, 0);
    }

    public static int parseInt(String str, int i) {
        if (TextUtils.isEmpty(str)) {
            return i;
        }
        try {
            return Integer.parseInt(str);
        } catch (Throwable e) {
            e.printStackTrace();
            return i;
        }
    }
}
