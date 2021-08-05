package com.sky.medialib.ui.kit.view.crop;

import android.graphics.RectF;

public class RectUtils {

    public static float[] getCornersFromRect(RectF rectF) {
        return new float[]{
                rectF.left, rectF.top,
                rectF.right, rectF.top,
                rectF.right, rectF.bottom,
                rectF.left, rectF.bottom};
    }

    public static float[] getRectSidesFromCorners(float[] fArr) {
        return new float[]{(float) Math.sqrt(Math.pow((double) (fArr[0] - fArr[2]), 2.0d) + Math.pow((double) (fArr[1] - fArr[3]), 2.0d)),
                (float) Math.sqrt(Math.pow((double) (fArr[2] - fArr[4]), 2.0d) + Math.pow((double) (fArr[3] - fArr[5]), 2.0d))};
    }

    public static float[] getCenterFromRect(RectF rectF) {
        return new float[]{rectF.centerX(), rectF.centerY()};
    }

    public static RectF trapToRect(float[] array) {
        RectF r = new RectF(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY,
                Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
        for (int i = 1; i < array.length; i += 2) {
            float x = Math.round(array[i - 1] * 10) / 10.f;
            float y = Math.round(array[i] * 10) / 10.f;
            r.left = (x < r.left) ? x : r.left;
            r.top = (y < r.top) ? y : r.top;
            r.right = (x > r.right) ? x : r.right;
            r.bottom = (y > r.bottom) ? y : r.bottom;
        }
        r.sort();
        return r;
    }
}
