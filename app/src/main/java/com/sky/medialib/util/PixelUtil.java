package com.sky.medialib.util;

import android.content.Context;
import android.content.res.Resources;

public class PixelUtil {

    public static int dip2px(float f) {
        return (int) ((Resources.getSystem().getDisplayMetrics().density * f) + 0.5f);
    }

    public static int sp2Px(float f) {
        return (int) ((Resources.getSystem().getDisplayMetrics().scaledDensity * f) + 0.5f);
    }

    public static int dip2px(Context context, float f) {
        return (int) ((context.getResources().getDisplayMetrics().density * f) + 0.5f);
    }

    public static float dip2px(float f, Context context) {
        return ((((float) context.getResources().getDisplayMetrics().densityDpi) * f) / 160.0f) + 0.5f;
    }
}
