package com.sky.medialib.ui.kit.view.circlegif;

import android.content.res.Resources;
import android.util.TypedValue;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.RawRes;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;

/**
 * @author: xuzhiyong
 * @date: 2021/8/16  下午4:46
 * @Email: 18971269648@163.com
 * @description:
 */
public class CircleGifDrawable extends GifDrawable {

    float scale = 1.0f;

    public CircleGifDrawable(@NonNull java.lang.String filePath) throws IOException {
        super(filePath);
        init();
    }

    public CircleGifDrawable(@NonNull Resources res, int id) throws Resources.NotFoundException, IOException {
        super(res, id);
        init();
        scale = getDensityScale(res,id);
    }

    private void init(){
        setTransform(new CircleTransform( (int) (getIntrinsicWidth() / scale),(int) (getIntrinsicHeight() / scale)));
    }

    static float getDensityScale(@NonNull Resources res, @DrawableRes @RawRes int id) {
        TypedValue value = new TypedValue();
        res.getValue(id, value, true);
        int resourceDensity = value.density;
        int density;
        if (resourceDensity == 0) {
            density = 160;
        } else if (resourceDensity != 65535) {
            density = resourceDensity;
        } else {
            density = 0;
        }

        int targetDensity = res.getDisplayMetrics().densityDpi;
        return density > 0 && targetDensity > 0 ? (float)targetDensity / (float)density : 1.0F;
    }
}
