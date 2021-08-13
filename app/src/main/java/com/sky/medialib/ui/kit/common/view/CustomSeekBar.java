package com.sky.medialib.ui.kit.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatSeekBar;

public class CustomSeekBar extends AppCompatSeekBar {
    private boolean mEnable = true;

    public CustomSeekBar(Context context) {
        super(context);
    }

    public CustomSeekBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CustomSeekBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void setIsSeekEnable(boolean z) {
        this.mEnable = z;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!this.mEnable) {
            return false;
        }
        return super.onTouchEvent(motionEvent);
    }
}
