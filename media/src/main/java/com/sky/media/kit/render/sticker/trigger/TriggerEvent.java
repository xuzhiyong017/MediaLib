package com.sky.media.kit.render.sticker.trigger;

import android.util.Pair;

import com.sky.media.kit.face.Face;
import com.sky.media.kit.render.sticker.Sticker.Component;


public class TriggerEvent {
    public int mAction;
    public Pair<Component, Face> mPair;

    public TriggerEvent(Pair<Component, Face> pair, int i) {
        this.mPair = pair;
        this.mAction = i;
    }
}
