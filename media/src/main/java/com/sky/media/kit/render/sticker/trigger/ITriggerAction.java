package com.sky.media.kit.render.sticker.trigger;

import android.util.Pair;

import com.sky.media.kit.face.Face;
import com.sky.media.kit.render.sticker.Sticker;
import com.sky.media.kit.render.sticker.StickerRenderHelper;


public interface ITriggerAction {
    boolean check(Pair<Sticker.Component, Face> pair, boolean z, StickerRenderHelper stickerRenderHelper, OnTriggerStartListener onTriggerStartListener);
}
