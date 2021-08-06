package com.sky.medialib.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class UIHelper {
    public static <T extends View> T inflateView(Context context, int layoutRes, ViewGroup viewGroup, boolean attachRoot) {
        return (T) LayoutInflater.from(context).inflate(layoutRes, viewGroup, attachRoot);
    }

    public static <T extends View> T inflateView(Context context, int layoutRes, ViewGroup viewGroup) {
        return UIHelper.inflateView(context, layoutRes, viewGroup, viewGroup != null);
    }

    public static <T extends View> T inflateView(Context context, int layoutRes) {
        return UIHelper.inflateView(context, layoutRes, null);
    }
}
