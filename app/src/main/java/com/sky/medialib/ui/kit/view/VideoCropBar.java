package com.sky.medialib.ui.kit.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.sky.medialib.R;
import com.sky.medialib.util.PixelUtil;


public class VideoCropBar extends View {

    private final int f9262a = 1;
    private final int f9263b = 2;
    private Bitmap bitmapLeft;
    private Bitmap bitmapRight;
    private float perWidth = -1.0f;
    private Paint mBitmapPaint = new Paint();
    private Paint f9268g = new Paint();
    private Paint mBoundPaint = new Paint();
    private float f9270i = 0.0f;
    private float minLimit = 0.0f;
    private float offsetLeft = 0.0f;
    private float offsetRight = 0.0f;
    private int iconWidth;
    private int iconHeight;
    private int rectHeight = 5;
    private int screenWidth;
    private int mFrameSize;
    private int halfFrameSize = 0;
    private int touchIconType = 0;
    private OnCropChangeListener onCropChangeListener;
    private boolean isSlideEnable = true;

    public interface OnCropChangeListener {
        void onStopScroll();
        void onSelectChange(float f);
    }

    public VideoCropBar(Context context) {
        super(context);
        init();
    }

    public VideoCropBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public VideoCropBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        mBitmapPaint = new Paint(1);
        f9268g = new Paint(1);
        f9268g.setColor(Color.parseColor("#661a1c21"));
        mBoundPaint = new Paint(1);
        mBoundPaint.setColor(Color.parseColor("#ffffffff"));
        rectHeight = (int) PixelUtil.dip2px(3.0f, getContext());
    }

    private void initBitmap() {
        bitmapLeft = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.edut_cut_left);
        bitmapRight = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.edut_cut_right);
        createLeftAndRightIcon();
    }

    private void createLeftAndRightIcon() {
        if (iconHeight != mFrameSize && bitmapLeft != null && bitmapRight != null) {
            iconHeight = mFrameSize;
            int width = bitmapLeft.getWidth();
            float f = (float) width;
            iconWidth = (int) (f * ((((float) mFrameSize) * 1.0f) / ((float) bitmapLeft.getHeight())));
            bitmapLeft = Bitmap.createScaledBitmap(bitmapLeft, iconWidth, iconHeight, true);
            bitmapRight = Bitmap.createScaledBitmap(bitmapRight, iconWidth, iconHeight, true);
        }
    }

    public void setOnCropChangeListener(OnCropChangeListener onCropChangeListener) {
        this.onCropChangeListener = onCropChangeListener;
    }

    private void initSizeInfo() {
        if (screenWidth <= 0 || mFrameSize <= 0) {
            screenWidth = getMeasuredWidth();
            mFrameSize = getMeasuredHeight();
        }
    }

    public void setSlideEnable(boolean z) {
        isSlideEnable = z;
    }

    public void setCropImageSize(int i, int i2) {
        screenWidth = i;
        mFrameSize = i2;
    }

    public int setup(int videoRealLength, float perWidth, int halfFrameSize) {
        initSizeInfo();
        this.perWidth = perWidth;
        this.halfFrameSize = halfFrameSize;
        initBitmap();
        int startIconOffset = (mFrameSize / 2) - iconWidth;
        if (startIconOffset <= 0) {
            startIconOffset = 0;
        }
        minLimit = (float) startIconOffset;
        f9270i = (float) startIconOffset;
        startIconOffset = (screenWidth - mFrameSize) - videoRealLength;
        if (startIconOffset > 0) {
            f9270i = ((float) startIconOffset) + f9270i;
        }
        offsetLeft = minLimit;
        offsetRight = ((float) (screenWidth - iconWidth)) - f9270i;
        postInvalidate();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                notifyTimeChange();
            }
        });
        return (int) ((offsetRight - offsetLeft) - ((float) iconWidth));
    }

    public float getStartTime(int i) {
        return (((offsetLeft + ((float) iconWidth)) - minLimit) + ((float) i)) * perWidth;
    }

    public float mo17871b(int i) {
        return ((offsetRight - minLimit) + ((float) i)) * perWidth;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initSizeInfo();
        if (screenWidth > 0 && mFrameSize > 0) {
            createLeftAndRightIcon();
            if (bitmapLeft != null) {
                if (offsetLeft < minLimit) {
                    offsetLeft = minLimit;
                }
                canvas.drawBitmap(bitmapLeft, offsetLeft, 0.0f, mBitmapPaint);
            }
            if (bitmapRight != null) {
                float i = screenWidth - f9270i - iconWidth;
                if (offsetRight > i) {
                    offsetRight = i;
                }
                canvas.drawBitmap(bitmapRight, offsetRight, 0.0f, mBitmapPaint);
            }
            if (offsetLeft >= minLimit) {
                canvas.drawRect(0.0f, 0.0f, offsetLeft, (float) mFrameSize, f9268g);
            }
            if (offsetRight + iconWidth <=  screenWidth - f9270i) {
                canvas.drawRect(offsetRight + iconWidth, 0.0f, screenWidth, mFrameSize, f9268g);
            }
            float f = offsetLeft +  iconWidth - 3.0f;
            float f2 = offsetRight + 3.0f;
            canvas.drawRect(f, 0.0f, f2,  rectHeight, mBoundPaint);
            canvas.drawRect(f, mFrameSize - rectHeight, f2, mFrameSize, mBoundPaint);
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        super.onTouchEvent(motionEvent);
        if (!isSlideEnable) {
            return false;
        }
        float x = motionEvent.getX();
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (x < offsetLeft - (iconWidth / 2.0f ) ||  x > offsetLeft + iconWidth * 1.5d) {
                    if (x >= offsetRight - (iconWidth / 2) &&  x <= offsetRight + iconWidth * 1.5d) {
                        touchIconType = 2;
                        break;
                    } else if (x < offsetLeft + iconWidth || x > offsetRight + iconWidth) {
                        touchIconType = 0;
                        return false;
                    } else {
                        touchIconType = 0;
                        return false;
                    }
                }else{
                    touchIconType = 1;
                }
                break;
            case MotionEvent.ACTION_UP:
                touchIconType = 0;
                if (onCropChangeListener != null) {
                    onCropChangeListener.onStopScroll();
                    break;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                updateIconLation(x);
                notifyTimeChange();
                break;
        }
        return true;
    }

    private void notifyTimeChange() {
        if (onCropChangeListener != null) {
            onCropChangeListener.onSelectChange(mo17871b(0) - getStartTime(0));
        }
    }

    private void updateIconLation(float downX) {
        if (touchIconType == 1 || touchIconType == 2) {
            float f2 = (float) (iconWidth + halfFrameSize);
            int width;
            if (touchIconType != 1) {
                width = (int) ((((float) getWidth()) - f9270i) - ((float) iconWidth));
                if (downX >= ((float) width)) {
                    offsetRight = (float) width;
                } else if (downX - offsetLeft > f2) {
                    offsetRight = downX;
             } else if (offsetLeft <= minLimit) {
                    offsetLeft = minLimit;
                    offsetRight = f2 + offsetLeft;
                } else {
                    offsetRight = downX;
                    offsetLeft = offsetRight - f2;
                }
            } else if (downX + f2 > offsetRight) {
                width = (int) ((((float) getWidth()) - f9270i) - ((float) iconWidth));
                if (offsetRight >= ((float) width)) {
                    offsetRight = (float) width;
                    offsetLeft = offsetRight - f2;
                } else {
                    offsetLeft = downX;
                    offsetRight = f2 + downX;
                }
            } else {
                offsetLeft = downX;
            }
            invalidate();
        }
    }
}
