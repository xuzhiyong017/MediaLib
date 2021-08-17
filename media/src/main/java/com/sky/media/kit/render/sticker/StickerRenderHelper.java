package com.sky.media.kit.render.sticker;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Pair;

import com.sky.media.image.core.cache.IBitmapCache;
import com.sky.media.image.core.extra.FpsTest;
import com.sky.media.image.core.util.BitmapUtil;
import com.sky.media.kit.face.*;
import com.sky.media.kit.render.sticker.Sticker.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class StickerRenderHelper {
    private IBitmapCache mBitmapCache;
    private Context mContext;
    private int mFps;
    private FpsTest.FpsGetListener mFpsGetListener = new FpsTest.FpsGetListener() {
        @Override
        public void onFpsGet(int i) {
            mFps = i;
        }
    };
    public Map<Pair<Component, Face>, Boolean> mIsContinuePlayMap = new LinkedHashMap();
    public Map<Pair<Component, Face>, Boolean> mIsLastFrameTriggerMap = new LinkedHashMap();
    public Map<Pair<Component, Face>, Integer> mLastPositionMap = new LinkedHashMap();
    public Map<Pair<Component, Face>, Integer> mPositionMap = new LinkedHashMap();
    private Sticker mSticker;
    public Map<Pair<Component, Face>, Integer> mStickerFrameMap = new LinkedHashMap();
    public Map<Pair<Component, Face>, Integer> mTriggerCountMap = new LinkedHashMap();


    StickerRenderHelper(Context context, IBitmapCache iBitmapCache) {
        this.mContext = context.getApplicationContext();
        this.mBitmapCache = iBitmapCache;
    }

    void setSticker(Sticker sticker) {
        this.mSticker = sticker;
        reset();
    }

    void reset() {
        this.mIsLastFrameTriggerMap.clear();
        this.mLastPositionMap.clear();
        this.mIsContinuePlayMap.clear();
        this.mTriggerCountMap.clear();
        this.mPositionMap.clear();
        this.mStickerFrameMap.clear();
        FpsTest.getInstance().removeFpsListener(this.mFpsGetListener);
    }

    Sticker getSticker() {
        return this.mSticker;
    }

    private <T> void clearMapUselessFaces(Map<Pair<Component, Face>, T> map, Face[] faceArr) {
        Pair pair;
        List<Pair> arrayList = new ArrayList();
        for (Entry key : map.entrySet()) {
            pair = (Pair) key.getKey();
            Face face = (Face) pair.second;
            if (face != null) {
                Object obj;
                for (Face equals : faceArr) {
                    if (equals.equals(face)) {
                        obj = 1;
                        break;
                    }
                }
                obj = null;
                if (obj == null) {
                    arrayList.add(pair);
                }
            }
        }
        for (Pair pair2 : arrayList) {
            map.remove(pair2);
        }
    }

    private void updateAllMapPair(Pair<Component, Face> pair) {
        if (this.mPositionMap.containsKey(pair)) {
            this.mPositionMap.put(pair, Integer.valueOf(((Integer) this.mPositionMap.remove(pair)).intValue()));
        } else {
            this.mPositionMap.put(pair, Integer.valueOf(0));
        }
        if (this.mStickerFrameMap.containsKey(pair)) {
            this.mStickerFrameMap.put(pair, Integer.valueOf(((Integer) this.mStickerFrameMap.remove(pair)).intValue()));
        } else {
            this.mStickerFrameMap.put(pair, Integer.valueOf(0));
        }
        if (this.mIsContinuePlayMap.containsKey(pair)) {
            this.mIsContinuePlayMap.put(pair, Boolean.valueOf(((Boolean) this.mIsContinuePlayMap.remove(pair)).booleanValue()));
        } else {
            this.mIsContinuePlayMap.put(pair, Boolean.valueOf(false));
        }
        if (this.mIsLastFrameTriggerMap.containsKey(pair)) {
            this.mIsLastFrameTriggerMap.put(pair, Boolean.valueOf(((Boolean) this.mIsLastFrameTriggerMap.remove(pair)).booleanValue()));
        } else {
            this.mIsLastFrameTriggerMap.put(pair, Boolean.valueOf(false));
        }
        if (this.mLastPositionMap.containsKey(pair)) {
            this.mLastPositionMap.put(pair, Integer.valueOf(((Integer) this.mLastPositionMap.remove(pair)).intValue()));
        } else {
            this.mLastPositionMap.put(pair, Integer.valueOf(0));
        }
        if (this.mTriggerCountMap.containsKey(pair)) {
            this.mTriggerCountMap.put(pair, Integer.valueOf(((Integer) this.mTriggerCountMap.remove(pair)).intValue()));
            return;
        }
        this.mTriggerCountMap.put(pair, Integer.valueOf(0));
    }

    private void updateAllMapFaces(Face[] faceArr) {
        clearMapUselessFaces(this.mPositionMap, faceArr);
        clearMapUselessFaces(this.mStickerFrameMap, faceArr);
        clearMapUselessFaces(this.mIsContinuePlayMap, faceArr);
        clearMapUselessFaces(this.mIsLastFrameTriggerMap, faceArr);
        clearMapUselessFaces(this.mLastPositionMap, faceArr);
        clearMapUselessFaces(this.mTriggerCountMap, faceArr);
        for (Component component : this.mSticker.componentResourceMap.keySet()) {
            if (component.type == 0 || component.type == 2) {
                for (Object pair : faceArr) {
                    updateAllMapPair(new Pair(component, pair));
                }
            } else {
                updateAllMapPair(new Pair(component, null));
            }
        }
    }

    Map<Pair<Component, Face>, Bitmap> getStickerResource(Face[] faceArr) {
        if (this.mSticker == null || this.mSticker.componentResourceMap == null) {
            return null;
        }
        int intValue;
        FpsTest.getInstance().addFpsListener(this.mFpsGetListener);
        updateAllMapFaces(faceArr);
        Map<Pair<Component, Face>, Bitmap> linkedHashMap = new LinkedHashMap();
        for (Entry entry : this.mPositionMap.entrySet()) {
            intValue = ((Integer) entry.getValue()).intValue();
            if (intValue >= 0) {
                Pair pair = (Pair) entry.getKey();
                Component component = (Component) pair.first;
                List list = (List) this.mSticker.componentResourceMap.get(component);
                if (list != null && list.size() > intValue) {
                    Bitmap loadBitmap;
                    String str = (String) list.get(intValue);
                    Bitmap bitmap = this.mBitmapCache.get(str);
                    if (bitmap == null) {
                        loadBitmap = BitmapUtil.loadBitmap(this.mContext, str, component.width, component.height);
                        if (loadBitmap != null) {
                            this.mBitmapCache.put(str, loadBitmap);
                        }
                        bitmap = loadBitmap;
                    }
                    linkedHashMap.put(pair, bitmap);
                    this.mStickerFrameMap.put(pair, Integer.valueOf(((Integer) this.mStickerFrameMap.get(pair)).intValue() + 1));
                }
            }
        }
        for (Component component2 : this.mSticker.component) {
            if (component2.type == 0 || component2.type == 2) {
                for (Object pair2 : faceArr) {
                    calculateNextFrameComponentIndex(new Pair(component2, pair2));
                }
            } else {
                calculateNextFrameComponentIndex(new Pair(component2, null));
            }
        }
        return linkedHashMap;
    }

    private void calculateNextFrameComponentIndex(Pair<Component, Face> pair) {
        int i = 0;
        Component component = (Component) pair.first;
        int round;
        if (this.mFps > 0) {
            round = Math.round((((float) component.length) * 1000.0f) / ((float) component.duration));
            int max = Math.max(1, Math.round((((float) round) * 1.0f) / ((float) this.mFps)));
            if (Math.round((((float) this.mFps) * 1.0f) / ((float) round)) <= ((Integer) this.mStickerFrameMap.get(pair)).intValue()) {
                int i2;
                round = ((Integer) this.mPositionMap.get(pair)).intValue() + max;
                if (round > component.length - 1) {
                    i2 = 0;
                } else {
                    i2 = round;
                }
                this.mLastPositionMap.put(pair, this.mPositionMap.get(pair));
                this.mPositionMap.put(pair, Integer.valueOf(i2));
                this.mStickerFrameMap.put(pair, Integer.valueOf(0));
                return;
            }
            return;
        }
        round = ((Integer) this.mPositionMap.get(pair)).intValue() + 1;
        if (round <= component.length - 1) {
            i = round;
        }
        this.mLastPositionMap.put(pair, this.mPositionMap.get(pair));
        this.mPositionMap.put(pair, Integer.valueOf(i));
    }
}
