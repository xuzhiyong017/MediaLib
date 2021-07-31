package com.sky.medialib.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.location.Location;
import android.net.Uri;
import android.os.Build.VERSION;
import android.text.TextUtils;

import androidx.lifecycle.LifecycleOwner;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class Util {

    public static Pattern mPattern = Pattern.compile("<[^>]+>");

    private static final SimpleDateFormat SIMPLE_DATE = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.CHINA);

    public static boolean isNotEmptyList(List list) {
        return (list == null || list.isEmpty()) ? false : true;
    }


    public static String getCurTime() {
        return SIMPLE_DATE.format(new Date());
    }

    public static String getNewVideoPath() {
        return "VID_" + Util.getCurTime() + ".mp4";
    }

    public static String getNewMp3Path() {
        return "MUS_" + Util.getCurTime() + ".mp3";
    }

    public static String getNewJpgPath() {
        return "IMG_" + Util.getCurTime() + ".jpg";
    }

    public static String getNewPngPath() {
        return "IMG_" + Util.getCurTime() + ".png";
    }

    public static String formatShowTime(long j) {
        long j2 = (j / 1000) / 60;
        long j3 = (j / 1000) % 60;
        StringBuilder stringBuilder = new StringBuilder();
        if (j2 < 10) {
            stringBuilder.append("0").append(j2).append(":");
        } else {
            stringBuilder.append(j2).append(":");
        }
        if (j3 < 10) {
            stringBuilder.append("0").append(j3);
        } else {
            stringBuilder.append(j3);
        }
        return stringBuilder.toString();
    }

    public static void notifyMediaCenter(Context context, String str) {
        if (VERSION.SDK_INT >= 19) {
            new MediaScanner().notifyUrl(str);
            return;
        }
        try {
            Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
            intent.setData(Uri.parse("file://" + str));
            context.sendBroadcast(intent);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static String getGPSLocation(double d) {
        String str;
        String[] split = Location.convert(Math.abs(d), 2).split(":");
        String[] split2 = split[2].split("\\.");
        if (split2.length == 0) {
            str = split[2];
        } else {
            str = split2[0];
        }
        return split[0] + "/1," + split[1] + "/1," + str + "/1";
    }

    public static int getMinNum(int i, int i2, int i3) {
        if (i > i3) {
            return i3;
        }
        return i < i2 ? i2 : i;
    }

    public static void scaleMatrix(Matrix matrix, boolean z, int i, int i2, int i3) {
        matrix.setScale(z ? -1.0f : 1.0f, 1.0f);
        matrix.postRotate((float) i);
        matrix.postScale(((float) i2) / 2000.0f, ((float) i3) / 2000.0f);
        matrix.postTranslate(((float) i2) / 2.0f, ((float) i3) / 2.0f);
    }

    public static void correctRect(RectF rectF, Rect rect) {
        rect.left = Math.round(rectF.left);
        rect.top = Math.round(rectF.top);
        rect.right = Math.round(rectF.right);
        rect.bottom = Math.round(rectF.bottom);
    }

    public static String getUA() {
        String property = System.getProperty("http.agent");
        if (TextUtils.isEmpty(property)) {
            return "android";
        }
        try {
            return URLEncoder.encode(property, "GBK");
        } catch (Throwable e) {
            e.printStackTrace();
            return "android";
        }
    }


    public static float[] getStrickTextureData(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            float[] fArr = new float[9];
            String[] split = str.split(",");
            for (int i = 0; i < split.length; i++) {
                fArr[i] = Float.valueOf(split[i]).floatValue();
            }
            return fArr;
        } catch (Exception e) {
            return null;
        }
    }

    public static void moveTaskToBack(Activity activity) {
        activity.moveTaskToBack(true);
    }

    public static LifecycleOwner getLifeCycleOwer(Context context) {
        if (context == null || !(context instanceof LifecycleOwner)) {
            return null;
        }
        return (LifecycleOwner) context;
    }
}
