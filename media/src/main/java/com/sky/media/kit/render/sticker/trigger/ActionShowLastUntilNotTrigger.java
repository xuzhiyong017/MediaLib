package com.sky.media.kit.render.sticker.trigger;

import android.util.Pair;


import com.sky.media.kit.face.Face;
import com.sky.media.kit.render.sticker.Sticker.Component;
import com.sky.media.kit.render.sticker.StickerRenderHelper;

public class ActionShowLastUntilNotTrigger implements ITriggerAction {
    public boolean check(Pair<Component, Face> pair, boolean z, StickerRenderHelper stickerRenderHelper, OnTriggerStartListener onTriggerStartListener) {
        if (((Integer) stickerRenderHelper.mTriggerCountMap.get(pair)).intValue() == 1) {
            stickerRenderHelper.mPositionMap.put(pair, Integer.valueOf(((Component) pair.first).length - 1));
            return true;
        }
        boolean booleanValue = ((Boolean) stickerRenderHelper.mIsLastFrameTriggerMap.get(pair)).booleanValue();
        stickerRenderHelper.mIsLastFrameTriggerMap.put(pair, Boolean.valueOf(z));
        if (booleanValue || !z) {
            if (!(booleanValue && z)) {
                if (!booleanValue || z) {
                    if (!((Boolean) stickerRenderHelper.mIsContinuePlayMap.get(pair)).booleanValue()) {
                        stickerRenderHelper.mPositionMap.put(pair, Integer.valueOf(0));
                        return false;
                    } else if (((Integer) stickerRenderHelper.mLastPositionMap.get(pair)).intValue() > ((Integer) stickerRenderHelper.mPositionMap.get(pair)).intValue()) {
                        stickerRenderHelper.mIsContinuePlayMap.put(pair, Boolean.valueOf(false));
                        stickerRenderHelper.mPositionMap.put(pair, Integer.valueOf(((Component) pair.first).length - 1));
                        stickerRenderHelper.mTriggerCountMap.put(pair, Integer.valueOf(((Integer) stickerRenderHelper.mTriggerCountMap.get(pair)).intValue() + 1));
                        return true;
                    }
                } else if (((Component) pair.first).length - (((Integer) stickerRenderHelper.mPositionMap.get(pair)).intValue() + 1) > 0) {
                    stickerRenderHelper.mIsContinuePlayMap.put(pair, Boolean.valueOf(true));
                }
            }
        } else if (onTriggerStartListener != null) {
            onTriggerStartListener.onTriggerStart(new TriggerEvent(pair, 8));
        }
        return true;
    }
}
