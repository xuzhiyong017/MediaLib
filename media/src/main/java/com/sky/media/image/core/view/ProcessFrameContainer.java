package com.sky.media.image.core.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class ProcessFrameContainer extends FrameLayout implements IContainerView {
    private ContainerViewHelper mHelper;

    public ProcessFrameContainer(final Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mHelper = new ContainerViewHelper() {
            public void requestLayout() {
                ProcessFrameContainer.this.requestLayout();
            }

            public Context getContext() {
                return context;
            }
        };
    }

    public void setScaleType(ContainerViewHelper.ScaleType scaleType) {
        this.mHelper.setScaleType(scaleType);
    }

    public boolean setAspectRatio(float f, int i, int i2) {
        return this.mHelper.setAspectRatio(f, i, i2);
    }

    public float getAspectRatio() {
        return this.mHelper.getAspectRatio();
    }

    public void rotate() {
        this.mHelper.rotate();
    }

    public void rotate(int i) {
        this.mHelper.rotate(i);
    }

    public boolean setRotate90Degrees(int i) {
        return this.mHelper.setRotate90Degrees(i);
    }

    public int getRotation90Degrees() {
        return this.mHelper.getRotation90Degrees();
    }

    public void resetRotate() {
        this.mHelper.resetRotate();
    }

    public int getPreviewWidth() {
        return this.mHelper.getPreviewWidth();
    }

    public int getPreviewHeight() {
        return this.mHelper.getPreviewHeight();
    }

    private boolean calculatePreviewSize(int i, int i2) {
        return this.mHelper.calculatePreviewSize(i, i2);
    }

    protected void onMeasure(int i, int i2) {
        calculatePreviewSize(MeasureSpec.getSize(i), MeasureSpec.getSize(i2));
        super.onMeasure(MeasureSpec.makeMeasureSpec(this.mHelper.getPreviewWidth(), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(this.mHelper.getPreviewHeight(),  MeasureSpec.EXACTLY));
    }
}
