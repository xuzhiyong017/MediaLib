package com.sky.medialib.ui.kit.view.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

@SuppressLint({"NewApi"})
public class RotateLayout extends ViewGroup {
    private int rotateType;
    private View firstView;

    public RotateLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setBackgroundResource(17170445);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.firstView = getChildAt(0);
        this.firstView.setPivotX(0.0f);
        this.firstView.setPivotY(0.0f);
    }


    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = right - left;
        int height = bottom - top;
        switch (this.rotateType) {
            case 0:
            case 180:
                this.firstView.layout(0, 0, width, height);
                return;
            case 90:
            case 270:
                this.firstView.layout(0, 0, height, width);
                return;
            default:
                return;
        }
    }

    protected void onMeasure(int i, int i2) {
        int measuredWidth;
        int i3 = 0;
        switch (this.rotateType) {
            case 0:
            case 180:
                measureChild(this.firstView, i, i2);
                measuredWidth = this.firstView.getMeasuredWidth();
                i3 = this.firstView.getMeasuredHeight();
                break;
            case 90:
            case 270:
                measureChild(this.firstView, i2, i);
                measuredWidth = this.firstView.getMeasuredHeight();
                i3 = this.firstView.getMeasuredWidth();
                break;
            default:
                measuredWidth = 0;
                break;
        }
        setMeasuredDimension(measuredWidth, i3);
        switch (this.rotateType) {
            case 0:
                this.firstView.setTranslationX(0.0f);
                this.firstView.setTranslationY(0.0f);
                break;
            case 90:
                this.firstView.setTranslationX(0.0f);
                this.firstView.setTranslationY((float) i3);
                break;
            case 180:
                this.firstView.setTranslationX((float) measuredWidth);
                this.firstView.setTranslationY((float) i3);
                break;
            case 270:
                this.firstView.setTranslationX((float) measuredWidth);
                this.firstView.setTranslationY(0.0f);
                break;
        }
        this.firstView.setRotation((float) (-this.rotateType));
    }

    public void setOrientation(int i) {
        int i2 = i % 360;
        if (this.rotateType != i2) {
            this.rotateType = i2;
            requestLayout();
        }
    }
}
