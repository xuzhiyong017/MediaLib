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
    public static StickerRender createWatermarkRender(Context context, String str, int x, int y, float scale, IBitmapCache iBitmapCache) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(str, options);
        int width = options.outWidth;
        int height = options.outHeight;
        StickerRender stickerRender = new StickerRender(context, iBitmapCache);
        Sticker sticker = new Sticker();
        sticker.component = new ArrayList();
        Sticker.Component component = new Component();
        component.type = 1;
        component.left = Math.round(((float) x) / scale);
        component.top = Math.round(((float) y) / scale);
        component.width = width;
        component.height = height;
        component.scale = scale;
        sticker.componentResourceMap = new LinkedHashMap();
        sticker.componentResourceMap.put(component, Collections.singletonList(str));
        stickerRender.setSticker(sticker);
        return stickerRender;
    }

    public static StickerRender createWatermarkRender(Context context, String str, int x, int y, float scale) {
        return createWatermarkRender(context, str, x, y, scale, ImageBitmapCache.INSTANCE);
    }
}
