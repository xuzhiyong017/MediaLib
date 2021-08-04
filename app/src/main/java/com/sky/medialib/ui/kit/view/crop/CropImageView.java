package com.sky.medialib.ui.kit.view.crop;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import java.lang.ref.WeakReference;
import java.util.Arrays;

public class CropImageView extends TransformImageView {
    public static final float DEFAULT_ASPECT_RATIO = 0.0f;
    public static final int DEFAULT_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION = 300;
    public static final int DEFAULT_MAX_BITMAP_SIZE = 0;
    public static final float DEFAULT_MAX_SCALE_MULTIPLIER = 10.0f;
    public static final float SOURCE_IMAGE_ASPECT_RATIO = 0.0f;
    private CropBoundsChangeListener mCropBoundsChangeListener;
    private final RectF mCropRect;
    private final RectF mCropRectState;
    private final Matrix mCurrentImageMatrixState;
    private final Matrix mDefaultMatrixState;
    private long mImageToWrapCropBoundsAnimDuration;
    private boolean mIsMirroredState;
    private float mMaxScale;
    private float mMaxScaleMultiplier;
    private float mMinScale;
    private int mNum0f90RotateState;
    private float mTargetAspectRatio;
    private float mTargetAspectRatioState;
    private final Matrix mTempMatrix;
    private Runnable mWrapCropBoundsRunnable;
    private Runnable mZoomImageToPositionRunnable;

    private static class WrapCropBoundsRunnable implements Runnable {
        private final WeakReference<CropImageView> mCropImageView;
        private final long mDurationMs;
        private final long mStartTime = System.currentTimeMillis();
        private final float mOldX;
        private final float mOldY;
        private final float mCenterDiffX;
        private final float mCenterDiffY;
        private final float mOldScale;
        private final float mDeltaScale;
        private final boolean mWillBeImageInBoundsAfterTranslate;

        public WrapCropBoundsRunnable(CropImageView cropImageView, long j, float f, float f2, float f3, float f4, float f5, float f6, boolean z) {
            this.mCropImageView = new WeakReference(cropImageView);
            this.mDurationMs = j;
            this.mOldX = f;
            this.mOldY = f2;
            this.mCenterDiffX = f3;
            this.mCenterDiffY = f4;
            this.mOldScale = f5;
            this.mDeltaScale = f6;
            this.mWillBeImageInBoundsAfterTranslate = z;
        }

        public void run() {
            CropImageView cropImageView = (CropImageView) this.mCropImageView.get();
            if (cropImageView != null) {
                float min = (float) Math.min(this.mDurationMs, System.currentTimeMillis() - this.mStartTime);
                float easeOut = CubicEasing.easeOut(min, 0.0f, this.mCenterDiffX, (float) this.mDurationMs);
                float easeOut2 = CubicEasing.easeOut(min, 0.0f, this.mCenterDiffY, (float) this.mDurationMs);
                float easeInOut = CubicEasing.easeInOut(min, 0.0f, this.mDeltaScale, (float) this.mDurationMs);
                if (min < ((float) this.mDurationMs)) {
                    cropImageView.postTranslate(easeOut - (cropImageView.mCurrentImageCenter[0] - this.mOldX), easeOut2 - (cropImageView.mCurrentImageCenter[1] - this.mOldY));
                    if (!this.mWillBeImageInBoundsAfterTranslate) {
                        cropImageView.zoomInImage(this.mOldScale + easeInOut, cropImageView.mCropRect.centerX(), cropImageView.mCropRect.centerY());
                    }
                    if (!cropImageView.isImageWrapCropBounds()) {
                        cropImageView.post(this);
                    }
                }
            }
        }
    }

    private static class ZoomImageToPosition implements Runnable {

        private final WeakReference<CropImageView> mCropImageView;
        private final long mDurationMs;
        private final long mStartTime = System.currentTimeMillis();
        private final float mOldScale;
        private final float mDeltaScale;
        private final float mDestX;
        private final float mDestY;

        public ZoomImageToPosition(CropImageView cropImageView, long j, float f, float f2, float f3, float f4) {
            this.mCropImageView = new WeakReference(cropImageView);
            this.mDurationMs = j;
            this.mOldScale = f;
            this.mDeltaScale = f2;
            this.mDestX = f3;
            this.mDestY = f4;
        }

        public void run() {
            CropImageView cropImageView = (CropImageView) this.mCropImageView.get();
            if (cropImageView != null) {
                float min = (float) Math.min(this.mDurationMs, System.currentTimeMillis() - this.mStartTime);
                float easeInOut = CubicEasing.easeInOut(min, 0.0f, this.mDeltaScale, (float) this.mDurationMs);
                if (min < ((float) this.mDurationMs)) {
                    cropImageView.zoomInImage(this.mOldScale + easeInOut, this.mDestX, this.mDestY);
                    cropImageView.post(this);
                } else {
                    cropImageView.setImageToWrapCropBounds();
                }
            }
        }
    }

    public CropImageView(Context context) {
        this(context, null);
    }

    public CropImageView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CropImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mCropRect = new RectF();
        this.mCropRectState = new RectF();
        this.mCurrentImageMatrixState = new Matrix();
        this.mDefaultMatrixState = new Matrix();
        this.mTempMatrix = new Matrix();
        this.mMaxScaleMultiplier = 10.0f;
        this.mZoomImageToPositionRunnable = null;
        this.mImageToWrapCropBoundsAnimDuration = 300;
    }

    public float getMaxScale() {
        return this.mMaxScale;
    }

    public float getMinScale() {
        return this.mMinScale;
    }

    public float getTargetAspectRatio() {
        return this.mTargetAspectRatio;
    }

    public void setCropRect(RectF rectF, boolean z, boolean isCrop, boolean isWrapCropBounds) {
        this.mCropRect.set(rectF.left - ((float) getPaddingLeft()), rectF.top - ((float) getPaddingTop()), rectF.right - ((float) getPaddingRight()), rectF.bottom - ((float) getPaddingBottom()));
        calculateImageScaleBounds();
        if (z && getDrawable() != null) {
            setupInitialImagePosition((float) getDrawable().getIntrinsicWidth(), (float) getDrawable().getIntrinsicHeight());
        }
        if (isCrop) {
            setImageToWrapCropBounds(isWrapCropBounds);
        }
    }

    public void setTargetAspectRatio(float f) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            this.mTargetAspectRatio = f;
        } else if (f == 0.0f) {
            this.mTargetAspectRatio = ((float) drawable.getIntrinsicWidth()) / ((float) drawable.getIntrinsicHeight());
        } else {
            this.mTargetAspectRatio = f;
        }

        if (this.mCropBoundsChangeListener != null) {
            this.mCropBoundsChangeListener.onCropAspectRatioChanged(this.mTargetAspectRatio);
        }
    }

    public CropBoundsChangeListener getCropBoundsChangeListener() {
        return this.mCropBoundsChangeListener;
    }

    public void setCropBoundsChangeListener(CropBoundsChangeListener cropBoundsChangeListener) {
        this.mCropBoundsChangeListener = cropBoundsChangeListener;
    }

    public void setImageToWrapCropBoundsAnimDuration(long j) {
        if (j > 0) {
            this.mImageToWrapCropBoundsAnimDuration = j;
            return;
        }
        throw new IllegalArgumentException("Animation duration cannot be negative value.");
    }

    public void setMaxScaleMultiplier(float f) {
        this.mMaxScaleMultiplier = f;
    }

    public void zoomOutImage(float f) {
        zoomOutImage(f, this.mCropRect.centerX(), this.mCropRect.centerY());
    }

    public void zoomOutImage(float f, float f2, float f3) {
        if (f >= getMinScale()) {
            postScale(f / getCurrentScale(), f2, f3);
        }
    }

    public void zoomInImage(float f) {
        zoomInImage(f, this.mCropRect.centerX(), this.mCropRect.centerY());
    }

    public void zoomInImage(float f, float f2, float f3) {
        if (f <= getMaxScale()) {
            postScale(f / getCurrentScale(), f2, f3);
        }
    }

    public void postMirror(boolean z) {
        postMirror(this.mCropRect.centerX(), this.mCropRect.centerY(), z);
    }

    public void postScale(float f, float f2, float f3) {
        if (f > 1.0f) {
            if (getCurrentScale() * f > getMaxScale()) {
                f = getMaxScale() / getCurrentScale();
            }
            super.postScale(f, f2, f3);
        } else if (f < 1.0f) {
            if (getCurrentScale() * f < getMinScale()) {
                f = getMinScale() / getCurrentScale();
            }
            super.postScale(f, f2, f3);
        }
    }

    public void postRotate(float f) {
        if (f == 90.0f || f == -90.0f) {
            this.mNum0f90Rotate++;
        }
        postRotate(f, this.mCropRect.centerX(), this.mCropRect.centerY());
    }

    public Bitmap getClipBitmap() {
        Bitmap createBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
        draw(new Canvas(createBitmap));
        float width = (((float) Resources.getSystem().getDisplayMetrics().widthPixels) * 1.0f) / this.mCropRect.width();
        float height = (((float) Resources.getSystem().getDisplayMetrics().heightPixels) * 1.0f) / this.mCropRect.height();
        if (width > height) {
            width = height;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(width, width);
        return Bitmap.createBitmap(createBitmap, (int) this.mCropRect.left, (int) this.mCropRect.top, (int) (this.mCropRect.right - this.mCropRect.left), (int) (this.mCropRect.bottom - this.mCropRect.top), matrix, true);
    }

    public void saveState() {
        this.mDefaultMatrixState.set(this.mDefaultMatrix);
        this.mCurrentImageMatrixState.set(this.mCurrentImageMatrix);
        this.mCropRectState.set(this.mCropRect);
        this.mTargetAspectRatioState = this.mTargetAspectRatio;
        this.mNum0f90RotateState = this.mNum0f90Rotate;
        this.mIsMirroredState = this.mIsMirrored;
    }

    public void resumeState() {
        this.mDefaultMatrix.set(this.mDefaultMatrixState);
        this.mCurrentImageMatrix.set(this.mCurrentImageMatrixState);
        this.mCropRect.set(this.mCropRectState);
        this.mTargetAspectRatio = this.mTargetAspectRatioState;
        cancelAllAnimations();
        setImageMatrix(this.mCurrentImageMatrix);
        this.mNum0f90Rotate = this.mNum0f90RotateState;
        this.mIsMirrored = this.mIsMirroredState;
    }

    public void cancelAllAnimations() {
        removeCallbacks(this.mWrapCropBoundsRunnable);
        removeCallbacks(this.mZoomImageToPositionRunnable);
    }

    public void setImageToWrapCropBounds() {
        setImageToWrapCropBounds(true);
    }

    public void setImageToWrapCropBounds(boolean z) {
        if (this.mBitmapLaidOut && !isImageWrapCropBounds()) {
            float f = this.mCurrentImageCenter[0];
            float f2 = this.mCurrentImageCenter[1];
            float currentScale = getCurrentScale();
            float centerX = this.mCropRect.centerX() - f;
            float centerY = this.mCropRect.centerY() - f2;
            float f3 = 0.0f;
            this.mTempMatrix.reset();
            this.mTempMatrix.setTranslate(centerX, centerY);
            float[] copyOf = Arrays.copyOf(this.mCurrentImageCorners, this.mCurrentImageCorners.length);
            this.mTempMatrix.mapPoints(copyOf);
            boolean isImageWrapCropBounds = isImageWrapCropBounds(copyOf);
            copyOf = calculateImageIndents();
            float f4 = -(copyOf[0] + copyOf[2]);
            float f5 = -(copyOf[3] + copyOf[1]);
            if (!isImageWrapCropBounds) {
                RectF rectF = new RectF(this.mCropRect);
                this.mTempMatrix.reset();
                this.mTempMatrix.setRotate(getCurrentAngle());
                this.mTempMatrix.mapRect(rectF);
                float[] a = RectUtils.getRectSidesFromCorners(this.mCurrentImageCorners);
                f3 = (Math.max(rectF.width() / a[0], rectF.height() / a[1]) * currentScale) - currentScale;
            }
            if (z) {
                Runnable c2666a = new WrapCropBoundsRunnable(this, this.mImageToWrapCropBoundsAnimDuration, f, f2, f4, f5, currentScale, f3, isImageWrapCropBounds);
                this.mWrapCropBoundsRunnable = c2666a;
                post(c2666a);
                return;
            }
            postTranslate(f4, f5);
            if (!isImageWrapCropBounds) {
                zoomInImage(currentScale + f3, this.mCropRect.centerX(), this.mCropRect.centerY());
            }
        }
    }

    private float[] calculateImageIndents() {
        this.mTempMatrix.reset();
        this.mTempMatrix.setRotate(-getCurrentAngle());
        float[] copyOf = Arrays.copyOf(this.mCurrentImageCorners, this.mCurrentImageCorners.length);
        float[] cornersFromRect = RectUtils.getCornersFromRect(this.mCropRect);
        this.mTempMatrix.mapPoints(copyOf);
        this.mTempMatrix.mapPoints(cornersFromRect);
        RectF trapToRect = RectUtils.trapToRect(copyOf);
        RectF trapToRect2 = RectUtils.trapToRect(cornersFromRect);
        float f = trapToRect.left - trapToRect2.left;
        float f2 = trapToRect.top - trapToRect2.top;
        float f3 = trapToRect.right - trapToRect2.right;
        float f4 = trapToRect.bottom - trapToRect2.bottom;
        cornersFromRect = new float[4];
        if (f <= 0.0f) {
            f = 0.0f;
        }
        cornersFromRect[0] = f;
        if (f2 <= 0.0f) {
            f2 = 0.0f;
        }
        cornersFromRect[1] = f2;
        if (f3 >= 0.0f) {
            f3 = 0.0f;
        }
        cornersFromRect[2] = f3;
        if (f4 >= 0.0f) {
            f4 = 0.0f;
        }
        cornersFromRect[3] = f4;
        this.mTempMatrix.reset();
        this.mTempMatrix.setRotate(getCurrentAngle());
        this.mTempMatrix.mapPoints(cornersFromRect);
        return cornersFromRect;
    }

    protected void onImageLaidOut() {
        super.onImageLaidOut();
        Drawable drawable = getDrawable();
        if (drawable != null) {
            float intrinsicWidth = (float) drawable.getIntrinsicWidth();
            float intrinsicHeight = (float) drawable.getIntrinsicHeight();
            this.mTargetAspectRatio = intrinsicWidth / intrinsicHeight;
            if (this.mCropBoundsChangeListener != null) {
                this.mCropBoundsChangeListener.onCropAspectRatioChanged(this.mTargetAspectRatio);
            }
            if (this.mCropRect.width() <= 0.0f || this.mCropRect.height() <= 0.0f) {
                calculateCropRect();
            }
            calculateImageScaleBounds(intrinsicWidth, intrinsicHeight);
            setupInitialImagePosition(intrinsicWidth, intrinsicHeight);
            if (this.mTransformImageListener != null) {
                this.mTransformImageListener.onScale(getCurrentScale());
                this.mTransformImageListener.onRotate(getCurrentAngle());
            }
        }
    }

    protected boolean isImageWrapCropBounds() {
        return isImageWrapCropBounds(this.mCurrentImageCorners);
    }

    protected boolean isImageWrapCropBounds(float[] fArr) {
        this.mTempMatrix.reset();
        this.mTempMatrix.setRotate(-getCurrentAngle());
        float[] copyOf = Arrays.copyOf(fArr, fArr.length);
        this.mTempMatrix.mapPoints(copyOf);
        float[] a = RectUtils.getCornersFromRect(this.mCropRect);
        this.mTempMatrix.mapPoints(a);
        return RectUtils.trapToRect(copyOf).contains(RectUtils.trapToRect(a));
    }

    protected void zoomImageToPosition(float f, float f2, float f3, long j) {
        if (f > getMaxScale()) {
            f = getMaxScale();
        }
        float currentScale = getCurrentScale();
        Runnable zoomImageToPosition = new ZoomImageToPosition(this, j, currentScale, f - currentScale, f2, f3);
        this.mZoomImageToPositionRunnable = zoomImageToPosition;
        post(zoomImageToPosition);
    }

    private void calculateCropRect() {
        if (this.mTargetAspectRatio > 0.0f) {
            int i = (int) (((float) this.mThisWidth) / this.mTargetAspectRatio);
            int i2;
            if (i > this.mThisHeight) {
                i = (int) (((float) this.mThisHeight) * this.mTargetAspectRatio);
                i2 = (this.mThisWidth - i) / 2;
                this.mCropRect.set((float) i2, 0.0f, (float) (i + i2), (float) this.mThisHeight);
                return;
            }
            i2 = (this.mThisHeight - i) / 2;
            this.mCropRect.set(0.0f, (float) i2, (float) this.mThisWidth, (float) (i + i2));
        }
    }

    private void calculateImageScaleBounds() {
        Drawable drawable = getDrawable();
        if (drawable != null) {
            if (this.mNum0f90Rotate % 2 == 0) {
                calculateImageScaleBounds((float) drawable.getIntrinsicWidth(), (float) drawable.getIntrinsicHeight());
            } else {
                calculateImageScaleBounds((float) drawable.getIntrinsicHeight(), (float) drawable.getIntrinsicWidth());
            }
        }
    }

    private void calculateImageScaleBounds(float f, float f2) {
        this.mMinScale = Math.max(this.mCropRect.width() / f, this.mCropRect.height() / f2);
        this.mMaxScale = this.mMinScale * this.mMaxScaleMultiplier;
    }

    private void setupInitialImagePosition(float f, float f2) {
        float width = this.mCropRect.width();
        width = ((width - (this.mMinScale * f)) / 2.0f) + this.mCropRect.left;
        float height = ((this.mCropRect.height() - (this.mMinScale * f2)) / 2.0f) + this.mCropRect.top;
        this.mCurrentImageMatrix.reset();
        this.mCurrentImageMatrix.postScale(this.mMinScale, this.mMinScale);
        this.mCurrentImageMatrix.postTranslate(width, height);
        setImageMatrix(this.mCurrentImageMatrix);
        this.mDefaultMatrix.set(this.mCurrentImageMatrix);
    }
}
