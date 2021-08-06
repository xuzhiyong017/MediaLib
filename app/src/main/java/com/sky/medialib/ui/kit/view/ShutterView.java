package com.sky.medialib.ui.kit.view;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import androidx.appcompat.widget.AppCompatImageView;

import com.sky.medialib.R;

public class ShutterView extends AppCompatImageView {
    private final int STATE_IDLE = 0;
    private final int STATE_IS_RECORDING = 1;
    private PointF mCircleCenter = new PointF();
    private float mCurrentStrokeWidth;
    private boolean mEnableLongPress = true;
    private int mInitHeight;
    private PointF mInitPosition = new PointF();
    private int mInitWidth;
    private boolean mIsDrawSuper;
    private Runnable mLongPressRunnable;
    private int mLongPressTime = 300;
    private int mMaxSize;
    private int mMaxStrokeWidth = 20;
    private int mMinStrokeWidth = 10;
    private Paint mPaint;
    private float mRadius;
    private Animation mRecordingAnimation;
    private mOnShutterClickListener mShutterClickListener;
    private ValueAnimator mStartAnimator;
    private int mState;
    private ValueAnimator mStopAnimator;
    private PointF mTouchDownPoint = new PointF();

    public interface mOnShutterClickListener {
        void mo16764a();
        void onStart();
        void mo16766c();
    }

    class StartAnimatorListener implements AnimatorListener {
        StartAnimatorListener() {
        }

        public void onAnimationStart(Animator animator) {
        }

        public void onAnimationEnd(Animator animator) {
            if (ShutterView.this.mState == 1) {
                ShutterView.this.startAnimation(ShutterView.this.mRecordingAnimation);
            }
        }

        public void onAnimationCancel(Animator animator) {
        }

        public void onAnimationRepeat(Animator animator) {
        }
    }

    class RecordingAnimation extends Animation {
        RecordingAnimation() {
        }

        protected void applyTransformation(float f, Transformation transformation) {
            ShutterView.this.mCurrentStrokeWidth = ((float) ShutterView.this.mMinStrokeWidth) + (((float) (ShutterView.this.mMaxStrokeWidth - ShutterView.this.mMinStrokeWidth)) * f);
            ShutterView.this.mRadius = ((float) ShutterView.this.mMaxSize) - (ShutterView.this.mCurrentStrokeWidth / 2.0f);
            ShutterView.this.mPaint.setStrokeWidth(ShutterView.this.mCurrentStrokeWidth);
            ShutterView.this.invalidate();
        }
    }

    public ShutterView(Context context) {
        super(context);
        init();
    }

    public ShutterView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public ShutterView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void init() {
        this.mIsDrawSuper = true;
        this.mPaint = new Paint();
        this.mPaint.setStyle(Style.STROKE);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setColor(getResources().getColor(R.color.common_red));
        this.mLongPressRunnable = new Runnable() {
            @Override
            public void run() {
                mStartAnimator.start();
                mState = 1;
                if (mShutterClickListener != null) {
                    mShutterClickListener.onStart();
                }
            }
        };
        this.mStartAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.2f});
        this.mStartAnimator.setDuration(500);
        this.mStartAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                if (((double) floatValue) < 0.2d) {
                    mRadius = (floatValue * ((float) (mMaxSize / 2))) * 5.0f;
                    mPaint.setStrokeWidth(mRadius * 2.0f);
                } else {
                    mCurrentStrokeWidth = (float) (((int) (((float) (mMaxSize - mMinStrokeWidth)) * (1.2f - floatValue))) + mMinStrokeWidth);
                    mRadius = ((float) mMaxSize) - (mCurrentStrokeWidth / 2.0f);
                    mPaint.setStrokeWidth(mCurrentStrokeWidth);
                    mIsDrawSuper = false;
                    setScaleX(Math.min((floatValue * 2.0f) + 1.0f, 2.0f));
                    setScaleY(Math.min((floatValue * 2.0f) + 1.0f, 2.0f));
                }
                invalidate();
            }
        });
        this.mStartAnimator.addListener(new StartAnimatorListener());
        this.mRecordingAnimation = new RecordingAnimation();
        this.mRecordingAnimation.setDuration(700);
        this.mRecordingAnimation.setRepeatMode(ValueAnimator.REVERSE);
        this.mRecordingAnimation.setRepeatCount(ValueAnimator.INFINITE);
        this.mRecordingAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        this.mStopAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.5f});
        this.mStopAnimator.setDuration(500);
        this.mStopAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                if (floatValue <= 1.0f) {
                    mCurrentStrokeWidth += (((float) mMaxSize) - mCurrentStrokeWidth) * floatValue;
                    mRadius = ((float) mMaxSize) - (mCurrentStrokeWidth / 2.0f);
                    mPaint.setStrokeWidth(mCurrentStrokeWidth);
                    setScaleX(Math.max(getScaleX() - (floatValue * 2.0f), 1.0f));
                    setScaleY(Math.max(getScaleY() - (floatValue * 2.0f), 1.0f));
                    setX(((mInitPosition.x - getX()) * floatValue) + getX());
                    setY((floatValue * (mInitPosition.y - getY())) + getY());
                } else {
                    if (!(getX() == mInitPosition.x && getY() == mInitPosition.y)) {
                        setX(mInitPosition.x);
                        setY(mInitPosition.y);
                    }
                    mRadius = ((1.5f - floatValue) * ((float) (mMaxSize / 2))) * 2.0f;
                    mPaint.setStrokeWidth(mRadius * 2.0f);
                    mIsDrawSuper = true;
                }
                invalidate();
            }
        });
    }


    public void setRecordingIdle() {
        mState = 0;
    }

    public void setShutterClickListener(mOnShutterClickListener mOnShutterClickListener) {
        mShutterClickListener = mOnShutterClickListener;
    }

    public void onDraw(Canvas canvas) {
        if (mIsDrawSuper) {
            super.onDraw(canvas);
        }
        if (mState != 0) {
            canvas.drawCircle(mCircleCenter.x, mCircleCenter.y, mRadius, mPaint);
        }
    }

    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (z) {
            if (mInitWidth == 0 || mInitHeight == 0) {
                mInitWidth = i3 - i;
                mInitHeight = i4 - i2;
            }
            mCircleCenter.x = (float) ((i3 - i) / 2);
            mCircleCenter.y = (float) ((i4 - i2) / 2);
            mMaxSize = (int) Math.min(mCircleCenter.x, mCircleCenter.y);
            mCurrentStrokeWidth = (float) mMaxSize;
            mRadius = mCurrentStrokeWidth / 2.0f;
            mPaint.setStrokeWidth(mCurrentStrokeWidth);
            mInitPosition.set(getX(), getY());
        }
    }

    public void setEnableLongPress(boolean videoMode) {
        mEnableLongPress = videoMode;
        if (mEnableLongPress) {
            setImageResource(R.drawable.selector_camera_video_shutter);
        } else {
            setImageResource(R.drawable.selector_camera_image_shutter);
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case 0:
                if (mEnableLongPress) {
                    postDelayed(mLongPressRunnable, (long) mLongPressTime);
                }
                mTouchDownPoint.set(motionEvent.getRawX(), motionEvent.getRawY());
                setPressed(true);
                break;
            case 1:
            case 3:
                removeCallbacks(mLongPressRunnable);
                if (mState == 1) {
                    clearAnimation();
                    mStartAnimator.cancel();
                    mRecordingAnimation.cancel();
                    mStopAnimator.start();
                }
                setPressed(false);
                if (mShutterClickListener != null) {
                    if (mState != 1) {
                        mShutterClickListener.mo16764a();
                        break;
                    }
                    mShutterClickListener.mo16766c();
                    mState = 0;
                    break;
                }
                break;
            case 2:
                if (mState != 0) {
                    int rawY = (int) (motionEvent.getRawY() - mTouchDownPoint.y);
                    setX(((float) ((int) (motionEvent.getRawX() - mTouchDownPoint.x))) + getX());
                    setY(getY() + ((float) rawY));
                    mTouchDownPoint.set(motionEvent.getRawX(), motionEvent.getRawY());
                    invalidate();
                    break;
                }
                break;
        }
        return true;
    }
}
