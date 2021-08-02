package com.sky.medialib.ui.kit.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.core.content.ContextCompat;

import com.sky.medialib.R;


public class ToolSeekBar extends AppCompatSeekBar {
    private static final int DEFAULT_NO_PROGRESS_COLOR = Color.WHITE;
    private static final int DEFAULT_PROGRESS_COLOR = Color.parseColor("#FC6063");
    private static final int PADDING = 16;
    private boolean mIsMiddleZero;
    private Bitmap mLeft;
    private int mNoProgressColor;
    private Bitmap mThumb;

    public ToolSeekBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ToolSeekBar);
        if (obtainStyledAttributes != null) {
            obtainStyledAttributes.getColor(R.styleable.ToolSeekBar_progress_color, DEFAULT_PROGRESS_COLOR);
            this.mNoProgressColor = obtainStyledAttributes.getColor(R.styleable.ToolSeekBar_no_progress_color, DEFAULT_NO_PROGRESS_COLOR);
            obtainStyledAttributes.recycle();
        }
        this.mLeft = ((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.camera_control_bar_left)).getBitmap();
        this.mThumb = ((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.camera_control_bar_circle)).getBitmap();
    }

    public void setIsMiddleZero(boolean z) {
        this.mIsMiddleZero = z;
    }

    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = (((float) ((getWidth() - this.mThumb.getWidth()) * getProgress())) * 1.0f) / ((float) getMax());
        drawProgress(canvas, width);
        drawThumb(canvas, width);
    }

    public float getThumbX() {
        return ((((float) ((getWidth() - this.mThumb.getWidth()) * getProgress())) * 1.0f) / ((float) getMax())) + ((float) (this.mThumb.getWidth() / 2));
    }

    private void drawProgress(Canvas canvas, float f) {
        float height;
        float f2;
        float width;
        float width2;
        Paint paint;
        if (this.mIsMiddleZero) {
            height = (((float) getHeight()) - 6.0f) / 2.0f;
            f2 = height + 6.0f;
            width = (float) (this.mThumb.getWidth() / 2);
            width2 = (float) ((getWidth() / 2) + (this.mLeft.getWidth() / 2));
            float width3 = (float) ((getWidth() / 2) - (this.mLeft.getWidth() / 2));
            float width4 = (float) (getWidth() - (this.mThumb.getWidth() / 2));
            paint = new Paint();
            paint.setStyle(Style.FILL);
            paint.setColor(this.mNoProgressColor);
            canvas.drawRect(width, height, width3, f2, paint);
            canvas.drawRect(width2, height, width4, f2, paint);
            canvas.drawBitmap(this.mLeft, (float) ((getWidth() / 2) - (this.mLeft.getWidth() / 2)), (float) ((getHeight() / 2) - (this.mLeft.getHeight() / 2)), paint);
            paint.setARGB(255, 252, 96, 99);
            if (getProgress() > getMax() / 2) {
                canvas.drawRect((float) ((getWidth() / 2) + (this.mLeft.getWidth() / 2)), height, f + ((float) (this.mThumb.getWidth() / 2)), f2, paint);
            }else{
                canvas.drawRect(f + ((float) (this.mThumb.getWidth() / 2)), height, (float) ((getWidth() / 2) - (this.mLeft.getWidth() / 2)), f2, paint);
            }
        }else{
            height = (((float) getHeight()) - 6.0f) / 2.0f;
            f2 = height + 6.0f;
            width = (float) ((this.mThumb.getWidth() / 2) + (this.mLeft.getWidth() / 2));
            width2 = f + width;
            paint = new Paint();
            paint.setStyle(Style.FILL);
            paint.setColor(this.mNoProgressColor);
            canvas.drawRect(width, height, (float) (getWidth() - (this.mThumb.getWidth() / 2)), f2, paint);
            canvas.drawBitmap(this.mLeft, (float) ((this.mThumb.getWidth() / 2) - (this.mLeft.getWidth() / 2)), (float) ((getHeight() / 2) - (this.mLeft.getHeight() / 2)), paint);
            paint.setARGB(255, 252, 96, 99);
            canvas.drawRect(width, height, width2, f2, paint);
        }

    }

    private void drawThumb(Canvas canvas, float f) {
        float height = ((float) (getHeight() - this.mThumb.getHeight())) / 2.0f;
        canvas.drawBitmap(this.mThumb, null, new RectF(f, height, ((float) this.mThumb.getWidth()) + f, ((float) this.mThumb.getHeight()) + height), null);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        int i = 1;
        boolean onTouchEvent = super.onTouchEvent(motionEvent);
        if (onTouchEvent) {
            switch (motionEvent.getAction()) {
                case 1:
                    float x = motionEvent.getX();
                    float y = motionEvent.getY();
                    if (this.mIsMiddleZero ? x > ((float) (((getWidth() / 2) + (this.mLeft.getWidth() / 2)) + 16)) || x < ((float) (((getWidth() / 2) - (this.mLeft.getWidth() / 2)) - 16)) || y <= 0.0f || y >= ((float) getHeight()) : x > ((float) (((this.mThumb.getWidth() / 2) + (this.mLeft.getWidth() / 2)) + 16)) || y <= 0.0f || y >= ((float) getHeight())) {
                        i = 0;
                    }
                    if (i != 0) {
                        if (!this.mIsMiddleZero) {
                            setProgress(0);
                            break;
                        }
                        setProgress(getMax() / 2);
                        break;
                    }
                    break;
            }
        }
        return onTouchEvent;
    }
}
