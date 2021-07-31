package com.sky.medialib.util;

import android.text.TextUtils;

import java.math.BigDecimal;

public class NumberUtil {

    public static String formatNum(int i) {
        if (i <= 9999) {
            return String.valueOf(i);
        }
        if (i <= 999999) {
            return String.format("%1$d万", new Object[]{Integer.valueOf(i / 10000)});
        } else if (i > 99999999) {
            return "1亿+";
        } else {
            return String.format("%1$d万", new Object[]{Integer.valueOf(i / 10000)});
        }
    }

    public static String trimString(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        try {
            return NumberUtil.formatNum(NumberUtil.valueOfInt(str.trim()));
        } catch (Throwable e) {
            e.printStackTrace();
            return str;
        }
    }

    public static Integer valueOfInteger(String str, int i) {
        if (TextUtils.isEmpty(str)) {
            return Integer.valueOf(i);
        }
        try {
            return Integer.valueOf(Integer.parseInt(str));
        } catch (NumberFormatException e) {
            return Integer.valueOf(i);
        }
    }

    public static Long valueOfLong(String str, long j) {
        if (TextUtils.isEmpty(str)) {
            return Long.valueOf(j);
        }
        try {
            return Long.valueOf(Long.parseLong(str));
        } catch (NumberFormatException e) {
            return Long.valueOf(j);
        }
    }

    public static Float valueOfLong(String str, float f) {
        if (TextUtils.isEmpty(str)) {
            return Float.valueOf(f);
        }
        try {
            return Float.valueOf(Float.parseFloat(str));
        } catch (NumberFormatException e) {
            return Float.valueOf(f);
        }
    }

    public static int valueOfInt(String str) {
        return NumberUtil.valueOfInteger(str, 0).intValue();
    }

    public static Long valueOfLong(String str) {
        return NumberUtil.valueOfLong(str, 0);
    }

    public static float valueOffloat(String str) {
        return NumberUtil.valueOfLong(str, 0.0f).floatValue();
    }

    public static float valueOffloat(float f, int i) {
        return new BigDecimal(String.valueOf(f)).setScale(i, 4).floatValue();
    }
}
