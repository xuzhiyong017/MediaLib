package com.sky.medialib.ui.kit.view.crop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.blankj.utilcode.util.AdaptScreenUtils;


public class CropRectView extends View {
    /* renamed from: a */
    private int mTouchAreaType = 0;
    /* renamed from: b */
    private int f9390b;
    /* renamed from: c */
    private Point f9391c = new Point();
    private float mGuideStrokeWeight = ((float) AdaptScreenUtils.pt2Px(2.0f));
    private float mGuideLineWidth = ((float) AdaptScreenUtils.pt2Px(1.0f));
    private int mHandleSize = AdaptScreenUtils.pt2Px(6.0f);
    private final Paint mCropGridPaint = new Paint();
    private Rect mAspectRatioRect = new Rect();
    private float viewRatio;
    private float ratio;
    private boolean hasCropRatio = true;
    private boolean enableGridLine;
    private int height;
    private int width;
    private Point mStartPoint = new Point();
    private Rect showRect = new Rect();
    private OnRectChangeListener mCallback;
    private Rect mStoreStartRect = new Rect();
    private boolean areaVisible;
    private boolean outAreaVisible;
    private float f9409u;
    private boolean f9410v;

    public interface OnRectChangeListener {
        void onRectChange(float f, RectF rectF, boolean z, boolean z2);
    }

    public CropRectView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    private void init() {
        this.mCropGridPaint.setAntiAlias(true);
        this.mCropGridPaint.setDither(true);
    }

    public void cropAspectRatio(float f) {
        setRatio(f);
        this.hasCropRatio = true;
        this.mAspectRatioRect.set(this.mStartPoint.x, this.mStartPoint.y, this.width, this.height);
    }

    public void setRatio(float f) {
        setRatio(f, true, getWidth() > 0);
    }

    public void setRatio(float f, boolean z, boolean z2) {
        if (f == 0.0f) {
            this.hasCropRatio = true;
        } else {
            this.ratio = f;
            this.hasCropRatio = false;
        }
        setTargetAspectRatio(this.ratio);
        resetStartPoint();
        if (this.mCallback != null) {
            this.mCallback.onRectChange(this.ratio, new RectF((float) this.mStartPoint.x, (float) this.mStartPoint.y, (float) (this.width + this.mStartPoint.x), (float) (this.height + this.mStartPoint.y)), z, z2);
        }
        invalidate();
    }

    public float getRatio() {
        return this.ratio;
    }

    /* renamed from: a */
    public boolean mo17993a() {
        return this.hasCropRatio;
    }

    /* renamed from: a */
    public void drawGridLine(boolean z) {
        this.enableGridLine = z;
        postInvalidate();
    }

    public void saveState() {
        this.mStoreStartRect.set(this.mStartPoint.x, this.mStartPoint.y, this.width, this.height);
        this.f9409u = this.ratio;
        this.f9410v = this.hasCropRatio;
    }

    public void resumeState() {
        this.mStartPoint.set(this.mStoreStartRect.left, this.mStoreStartRect.top);
        this.width = this.mStoreStartRect.right;
        this.height = this.mStoreStartRect.bottom;
        this.ratio = this.f9409u;
        this.hasCropRatio = this.f9410v;
        invalidate();
    }

    public boolean mo17996d() {
        return (this.mAspectRatioRect.left == this.mStartPoint.x && this.mAspectRatioRect.top == this.mStartPoint.y && this.mAspectRatioRect.right == this.width && this.mAspectRatioRect.bottom == this.height) ? false : true;
    }

    public void setAreaVisible(boolean z) {
        this.areaVisible = z;
        invalidate();
    }

    public void setOutAreaVisible(boolean z) {
        this.outAreaVisible = z;
    }

    public float getViewRatio() {
        return this.viewRatio;
    }

    public void setOnRectChangeListener(OnRectChangeListener onRectChangeListener) {
        this.mCallback = onRectChangeListener;
    }

    private void setTargetAspectRatio(float f) {
        int width = (getWidth() - getPaddingLeft()) - getPaddingRight();
        int height = (getHeight() - getPaddingTop()) - getPaddingBottom();
        this.viewRatio = (((float) width) * 1.0f) / ((float) height);
        if (f < this.viewRatio) {
            this.height = height;
            this.width = (int) (((float) height) * f);
            return;
        }
        this.height = (int) (((float) width) / f);
        this.width = width;
    }

    private void resetStartPoint() {
        this.mStartPoint.set(((((getWidth() - getPaddingLeft()) - getPaddingRight()) - this.width) / 2) + getPaddingLeft(), ((((getHeight() - getPaddingTop()) - getPaddingBottom()) - this.height) / 2) + getPaddingTop());
    }

    protected void onDraw(Canvas canvas) {
        if (this.ratio != 0.0f) {
            int width = getWidth();
            int height = getHeight();
            int left = this.mStartPoint.x;
            int top = this.mStartPoint.y;
            int right = left + this.width;
            int bottom = top + this.height;
            this.showRect.set(left, top, right, bottom);
            if (this.outAreaVisible) {
                this.mCropGridPaint.setColor(-869915098);
            } else {
                this.mCropGridPaint.setColor(-14277082);
            }
            canvas.drawRect(0.0f, 0.0f, (float) width, (float) top, this.mCropGridPaint);
            canvas.drawRect(0.0f, (float) bottom, (float) width, (float) height, this.mCropGridPaint);
            canvas.drawRect(0.0f, (float) top, (float) left, (float) bottom, this.mCropGridPaint);
            canvas.drawRect((float) right, (float) top, (float) width, (float) bottom, this.mCropGridPaint);
            if (this.areaVisible) {
                if (this.enableGridLine) {
                    this.mCropGridPaint.setColor(-855638017);
                    this.mCropGridPaint.setStrokeWidth(this.mGuideLineWidth);
                    height = this.width / 3;
                    int i5 = this.height / 3;
                    int i6 = 1;
                    while (true) {
                        width = i6;
                        if (width >= 3) {
                            break;
                        }
                        canvas.drawLine((float) ((width * height) + left), (float) top, (float) ((width * height) + left), (float) bottom, this.mCropGridPaint);
                        canvas.drawLine((float) left, (float) ((width * i5) + top), (float) right, (float) ((width * i5) + top), this.mCropGridPaint);
                        i6 = width + 1;
                    }
                }
                this.mCropGridPaint.setColor(-1);
                this.mCropGridPaint.setStrokeWidth(this.mGuideStrokeWeight);
                canvas.drawLine((float) left, (float) top, (float) left, (float) bottom, this.mCropGridPaint);
                canvas.drawLine((float) right, (float) top, (float) right, (float) bottom, this.mCropGridPaint);
                canvas.drawLine((float) left, (float) top, (float) right, (float) top, this.mCropGridPaint);
                canvas.drawLine((float) left, (float) bottom, (float) right, (float) bottom, this.mCropGridPaint);
                canvas.drawCircle((float) left, (float) top, (float) this.mHandleSize, this.mCropGridPaint);
                canvas.drawCircle((float) right, (float) top, (float) this.mHandleSize, this.mCropGridPaint);
                canvas.drawCircle((float) left, (float) bottom, (float) this.mHandleSize, this.mCropGridPaint);
                canvas.drawCircle((float) right, (float) bottom, (float) this.mHandleSize, this.mCropGridPaint);
            }
        }
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (z && this.ratio > 0.0f) {
            cropAspectRatio(this.ratio);
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!this.areaVisible) {
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        int sqrt = (int) Math.sqrt((double) (((x - (getWidth() / 2)) * (x - (getWidth() / 2))) + ((y - (getHeight() / 2)) * (y - (getHeight() / 2)))));
        int abs = Math.abs((getWidth() / 2) - x) * 2;
        int abs2 = Math.abs((getHeight() / 2) - y) * 2;
        switch (motionEvent.getAction()) {
            case 0:
                this.f9390b = sqrt;
                this.f9391c.set(abs, abs2);
                Rect rect = new Rect();
                rect.set((this.showRect.left - this.mHandleSize) - 16, (this.showRect.top - this.mHandleSize) - 16, (this.showRect.left + this.mHandleSize) + 16, (this.showRect.top + this.mHandleSize) + 16);
                if (rect.contains(x, y)) {
                    this.mTouchAreaType = 1;
                    return true;
                }
                rect.set(rect.left + this.width, rect.top, rect.right + this.width, rect.bottom);
                if (rect.contains(x, y)) {
                    this.mTouchAreaType = 2;
                    return true;
                }
                rect.set(rect.left, rect.top + this.height, rect.right, rect.bottom + this.height);
                if (rect.contains(x, y)) {
                    this.mTouchAreaType = 3;
                    return true;
                }
                rect.set(rect.left - this.width, rect.top, rect.right - this.width, rect.bottom);
                if (rect.contains(x, y)) {
                    this.mTouchAreaType = 4;
                    return true;
                }
                rect.set(this.showRect.left - 16, this.showRect.top - 16, this.showRect.right + 16, this.showRect.top + 16);
                if (rect.contains(x, y)) {
                    this.mTouchAreaType = 5;
                    return this.hasCropRatio;
                }
                rect.set(rect.left, rect.top + this.height, rect.right, rect.bottom + this.height);
                if (rect.contains(x, y)) {
                    this.mTouchAreaType = 6;
                    return this.hasCropRatio;
                }
                rect.set(this.showRect.left - 16, this.showRect.top - 16, this.showRect.left + 16, 16 + this.showRect.bottom);
                if (rect.contains(x, y)) {
                    this.mTouchAreaType = 7;
                    return this.hasCropRatio;
                }
                rect.set(rect.left + this.width, rect.top, rect.right + this.width, rect.bottom);
                if (!rect.contains(x, y)) {
                    return false;
                }
                this.mTouchAreaType = 8;
                return this.hasCropRatio;
            case 1:
            case MotionEvent.ACTION_CANCEL:
                reset();
                return true;
            case 2:
                switch (this.mTouchAreaType) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                        if (!this.hasCropRatio) {
                            this.height += (sqrt - this.f9390b) * 2;
                            this.width = (int) (((float) this.width) + ((((float) (sqrt - this.f9390b)) * this.ratio) * 2.0f));
                            this.f9390b = sqrt;
                            if (this.ratio > this.viewRatio && this.width > (getWidth() - getPaddingLeft()) - getPaddingRight()) {
                                this.width = (getWidth() - getPaddingLeft()) - getPaddingRight();
                                this.height = (int) (((float) this.width) / this.ratio);
                            } else if (this.height > (getHeight() - getPaddingTop()) - getPaddingBottom()) {
                                this.width = (int) (((float) this.height) * this.ratio);
                                this.height = (getHeight() - getPaddingTop()) - getPaddingBottom();
                            }
                            if (this.ratio >= this.viewRatio || this.width >= getWidth() / 5) {
                                if (this.height < getHeight() / 5) {
                                    this.height = getHeight() / 5;
                                    this.width = (int) (((float) this.height) * this.ratio);
                                    break;
                                }
                            }
                            this.width = getWidth() / 5;
                            this.height = (int) (((float) this.width) / this.ratio);
                            break;
                        }
                        this.width += abs - this.f9391c.x;
                        this.height += abs2 - this.f9391c.y;
                        break;
                    case 5:
                    case 6:
                        this.height += abs2 - this.f9391c.y;
                        break;
                    case 7:
                    case 8:
                        this.width += abs - this.f9391c.x;
                        break;
                }
                this.f9391c.set(abs, abs2);
                if (this.hasCropRatio) {
                    if (this.width > (getWidth() - getPaddingLeft()) - getPaddingRight()) {
                        this.width = (getWidth() - getPaddingLeft()) - getPaddingRight();
                    } else if (this.width < getWidth() / 5) {
                        this.width = getWidth() / 5;
                    }
                    if (this.height > (getHeight() - getPaddingTop()) - getPaddingBottom()) {
                        this.height = (getHeight() - getPaddingTop()) - getPaddingBottom();
                    } else if (this.height < getHeight() / 5) {
                        this.height = getHeight() / 5;
                    }
                }
                resetStartPoint();
                invalidate();
                return true;
            default:
                return false;
        }
    }

    private void reset() {
        setTargetAspectRatio((((float) this.width) * 1.0f) / ((float) this.height));
        resetStartPoint();
        if (this.mCallback != null) {
            this.mCallback.onRectChange((((float) this.width) * 1.0f) / ((float) this.height), new RectF((float) this.mStartPoint.x, (float) this.mStartPoint.y, (float) (this.width + this.mStartPoint.x), (float) (this.height + this.mStartPoint.y)), true, false);
        }
        invalidate();
    }
}
