package com.sky.media.kit.render.sticker.trigger;

import android.util.Pair;


import com.sky.media.kit.face.Face;
import com.sky.media.kit.render.sticker.Sticker.Component;
import com.sky.media.kit.render.sticker.StickerRenderHelper;

public class ActionShowAlways implements ITriggerAction {
    public boolean check(Pair<Component, Face> pair, boolean z, StickerRenderHelper stickerRenderHelper, OnTriggerStartListener onTriggerStartListener) {
        boolean booleanValue = ((Boolean) stickerRenderHelper.mIsLastFrameTriggerMap.get(pair)).booleanValue();
        if (booleanValue) {
            return true;
        }
        if (!(booleanValue || !z || onTriggerStartListener == null)) {
            onTriggerStartListener.onTriggerStart(new TriggerEvent(pair, 6));
        }
        stickerRenderHelper.mIsLastFrameTriggerMap.put(pair, Boolean.valueOf(z));
        return false;
    }
}
