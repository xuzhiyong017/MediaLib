package com.sky.media.kit.render.sticker;

import com.sky.media.image.core.cache.ImageBitmapCache;
import com.sky.media.kit.render.sticker.Sticker.Component;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;


import com.sky.media.image.core.cache.IBitmapCache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

public class WatermarkRenderCreator {
    public static StickerRender createWatermarkRender(Context context, String str, int i, int i2, float f, IBitmapCache iBitmapCache) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(str, options);
        int i3 = options.outWidth;
        int i4 = options.outHeight;
        StickerRender stickerRender = new StickerRender(context, iBitmapCache);
        Sticker sticker = new Sticker();
        sticker.component = new ArrayList();
        Sticker.Component component = new Component();
        component.type = 1;
        component.left = Math.round(((float) i) / f);
        component.top = Math.round(((float) i2) / f);
        component.width = i3;
        component.height = i4;
        component.scale = f;
        sticker.componentResourceMap = new LinkedHashMap();
        sticker.componentResourceMap.put(component, Collections.singletonList(str));
        stickerRender.setSticker(sticker);
        return stickerRender;
    }

    public static StickerRender createWatermarkRender(Context context, String str, int i, int i2, float f) {
        return createWatermarkRender(context, str, i, i2, f, ImageBitmapCache.INSTANCE);
    }
}
