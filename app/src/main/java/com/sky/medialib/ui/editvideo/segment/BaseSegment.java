package com.sky.medialib.ui.editvideo.segment;

import android.content.Intent;
import android.os.Bundle;

import com.sky.media.kit.base.BaseActivity;
import com.sky.medialib.ui.editvideo.segment.listener.IData;
import com.sky.medialib.ui.kit.common.base.AppActivity;


public abstract class BaseSegment<T extends IData> {
    protected AppActivity activity;
    protected T mData;

    public BaseSegment(AppActivity baseActivity, T t) {
        this.activity = baseActivity;
        this.mData = t;
    }

    public void onCreate(Bundle bundle) {
    }

    public void onResume() {
    }

    public void onPause() {
    }

    public void onDestroy() {
    }

    public void onActivityResult(int i, int i2, Intent intent) {
    }
}
