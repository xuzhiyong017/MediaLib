package com.sky.medialib.ui.kit.view.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Gallery;

public class CameraTypeGallery extends Gallery {

    private float f9307a = 1.1f;

    public CameraTypeGallery(Context context) {
        super(context);
        init();
    }

    public CameraTypeGallery(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    private void init() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return event.getAction() == MotionEvent.ACTION_MOVE;
            }
        });
    }

    protected boolean drawChild(Canvas canvas, View view, long j) {
        if (this.f9307a != 1.0f) {
            int width = view.getWidth();
            int height = view.getHeight();
            int abs = Math.abs((getWidth() / 2) - (view.getLeft() + (width / 2)));
            float f = (((float) (width - abs)) * 1.0f) / ((float) width);
            if (abs < width) {
                view.setPivotX(((float) width) / 2.0f);
                view.setPivotY((float) (height - view.getPaddingBottom()));
                float f2 = ((this.f9307a - 1.0f) * f) + 1.0f;
                view.setScaleX(f2);
                view.setScaleY(f2);
            } else {
                view.setScaleX(1.0f);
                view.setScaleY(1.0f);
            }
        }
        return super.drawChild(canvas, view, j);
    }
}
