package com.sky.media.kit.render.sticker.trigger;

import android.util.Pair;

import com.sky.media.kit.face.Face;
import com.sky.media.kit.render.sticker.Sticker.Component;
import com.sky.media.kit.render.sticker.StickerRenderHelper;

public class ActionHideUntilNotTrigger implements ITriggerAction {
    public boolean check(Pair<Component, Face> pair, boolean z, StickerRenderHelper stickerRenderHelper, OnTriggerStartListener onTriggerStartListener) {
        if (((Integer) stickerRenderHelper.mTriggerCountMap.get(pair)).intValue() == 1 && ((Component) pair.first).again == 1) {
            return true;
        }
        boolean booleanValue = ((Boolean) stickerRenderHelper.mIsLastFrameTriggerMap.get(pair)).booleanValue();
        stickerRenderHelper.mIsLastFrameTriggerMap.put(pair, Boolean.valueOf(z));
        if (!(booleanValue || !z || onTriggerStartListener == null)) {
            onTriggerStartListener.onTriggerStart(new TriggerEvent(pair, 1));
        }
        if (booleanValue && !z) {
            stickerRenderHelper.mTriggerCountMap.put(pair, Integer.valueOf(((Integer) stickerRenderHelper.mTriggerCountMap.get(pair)).intValue() + 1));
        }
        return !z;
    }
}
