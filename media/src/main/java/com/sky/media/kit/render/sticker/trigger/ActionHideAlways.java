package com.sky.media.kit.render.sticker.trigger;

import android.util.Pair;

import com.sky.media.kit.face.Face;
import com.sky.media.kit.render.sticker.Sticker;
import com.sky.media.kit.render.sticker.StickerRenderHelper;

public class ActionHideAlways implements ITriggerAction {
    public boolean check(Pair<Sticker.Component, Face> pair, boolean z, StickerRenderHelper stickerRenderHelper, OnTriggerStartListener onTriggerStartListener) {
        boolean booleanValue = ((Boolean) stickerRenderHelper.mIsLastFrameTriggerMap.get(pair)).booleanValue();
        if (booleanValue) {
            return false;
        }
        if (!(booleanValue || !z || onTriggerStartListener == null)) {
            onTriggerStartListener.onTriggerStart(new TriggerEvent(pair, 7));
        }
        stickerRenderHelper.mIsLastFrameTriggerMap.put(pair, Boolean.valueOf(z));
        return true;
    }
}
