package com.sky.medialib.ui.kit.view.crop;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;


public class GestureCropImageView extends CropImageView {
    private static final int DOUBLE_TAP_ZOOM_DURATION = 200;
    private boolean mAreaVisible;
    private int mDoubleTapScaleSteps;
    private GestureDetector mGestureDetector;
    private boolean mIsRotateEnabled;
    private boolean mIsScaleEnabled;
    private float mMidPntX;
    private float mMidPntY;
    private RotationGestureDetector mRotateDetector;
    private ScaleGestureDetector mScaleDetector;

    public GestureCropImageView(Context context) {
        super(context);
        this.mIsRotateEnabled = false;
        this.mIsScaleEnabled = true;
        this.mDoubleTapScaleSteps = 5;
        this.mAreaVisible = true;
    }

    public GestureCropImageView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public GestureCropImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mIsRotateEnabled = false;
        this.mIsScaleEnabled = true;
        this.mDoubleTapScaleSteps = 5;
        this.mAreaVisible = true;
    }

    public void setScaleEnabled(boolean z) {
        this.mIsScaleEnabled = z;
    }

    public boolean isScaleEnabled() {
        return this.mIsScaleEnabled;
    }

    public void setRotateEnabled(boolean z) {
        this.mIsRotateEnabled = z;
    }

    public boolean isRotateEnabled() {
        return this.mIsRotateEnabled;
    }

    public void setDoubleTapScaleSteps(int i) {
        this.mDoubleTapScaleSteps = i;
    }

    public int getDoubleTapScaleSteps() {
        return this.mDoubleTapScaleSteps;
    }

    public void setAreaVisible(boolean z) {
        this.mAreaVisible = z;
    }

    public boolean getAreaVisible() {
        return this.mAreaVisible;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!this.mAreaVisible) {
            return false;
        }
        if ((motionEvent.getAction() & 255) == 0) {
            cancelAllAnimations();
        }
        if (motionEvent.getPointerCount() > 1) {
            this.mMidPntX = (motionEvent.getX(0) + motionEvent.getX(1)) / 2.0f;
            this.mMidPntY = (motionEvent.getY(0) + motionEvent.getY(1)) / 2.0f;
        }
        this.mGestureDetector.onTouchEvent(motionEvent);
        if (this.mIsScaleEnabled) {
            this.mScaleDetector.onTouchEvent(motionEvent);
        }
        if (this.mIsRotateEnabled) {
            this.mRotateDetector.onTouchEvent(motionEvent);
        }
        if ((motionEvent.getAction() & 255) == 1) {
            setImageToWrapCropBounds();
        }
        return true;
    }

    protected void init() {
        super.init();
        setupGestureListeners();
    }

    protected float getDoubleTapTargetScale() {
        return getCurrentScale() * ((float) Math.pow((double) (getMaxScale() / getMinScale()), (double) (1.0f / ((float) this.mDoubleTapScaleSteps))));
    }

    private void setupGestureListeners() {
        this.mGestureDetector = new GestureDetector(getContext(), new SimpleOnGestureListener(){

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                zoomImageToPosition(getDoubleTapTargetScale(), e.getX(), e.getY(), DOUBLE_TAP_ZOOM_DURATION);
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                postTranslate(-distanceX, -distanceY);
                return true;
            }
        }, null, true);
        this.mScaleDetector = new ScaleGestureDetector(getContext(), new SimpleOnScaleGestureListener(){
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                postScale(detector.getScaleFactor(), mMidPntX,mMidPntY);
                return true;
            }
        });
        this.mRotateDetector = new RotationGestureDetector(new RotationGestureDetector.SimpleOnRotationGestureListener(){
            @Override
            public boolean onRotation(RotationGestureDetector rotationGestureDetector) {
                postRotate(rotationGestureDetector.getAngle(), mMidPntX, mMidPntY);
                return true;
            }
        });
    }
}
