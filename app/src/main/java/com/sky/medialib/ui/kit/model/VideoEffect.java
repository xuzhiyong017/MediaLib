package com.sky.medialib.ui.kit.model;

import com.sky.media.kit.model.FilterExt;

public class VideoEffect {
    public static final int FILTER_EFFECT = 0;
    public static final int REVERSE_FAILED = 2;
    public static final int REVERSE_ING = 1;
    public static final int REVERSE_SUCCESS = 0;
    public static final int TIME_EFFECT = 1;
    private String mAlias;
    private int mColor;
    private FilterExt mFilter;
    private boolean mIsReverse;
    private int mReverseState = 1;
    private int mType = 0;

    public VideoEffect(int i) {
        this.mType = i;
    }

    public String getAlias() {
        return this.mAlias;
    }

    public void setAlias(String str) {
        this.mAlias = str;
    }

    public int getReverseState() {
        return this.mReverseState;
    }

    public void setReverseState(int i) {
        this.mReverseState = i;
    }

    public void setReverse(boolean z) {
        this.mIsReverse = z;
    }

    public void setFilter(FilterExt filterExt) {
        this.mFilter = filterExt;
    }

    public void setColor(int i) {
        this.mColor = i;
    }

    public int getType() {
        return this.mType;
    }

    public boolean isReverse() {
        return this.mIsReverse;
    }

    public FilterExt getFilter() {
        return this.mFilter;
    }

    public int getColor() {
        return this.mColor;
    }
}
