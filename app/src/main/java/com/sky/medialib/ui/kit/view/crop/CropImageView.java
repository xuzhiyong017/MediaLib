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

        public WrapCropBoundsRunnable(CropImageView cropImageView, long duration, float oldX, float oldY, float centerDiffX, float centerDiffY, float oldScale, float deltaScale, boolean translateAble) {
            this.mCropImageView = new WeakReference(cropImageView);
            this.mDurationMs = duration;
            this.mOldX = oldX;
            this.mOldY = oldY;
            this.mCenterDiffX = centerDiffX;
            this.mCenterDiffY = centerDiffY;
            this.mOldScale = oldScale;
            this.mDeltaScale = deltaScale;
            this.mWillBeImageInBoundsAfterTranslate = translateAble;
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

        public ZoomImageToPosition(CropImageView cropImageView, long duration, float oldScale, float deltaScale, float destX, float destY) {
            this.mCropImageView = new WeakReference(cropImageView);
            this.mDurationMs = duration;
            this.mOldScale = oldScale;
            this.mDeltaScale = deltaScale;
            this.mDestX = destX;
            this.mDestY = destY;
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

    public void zoomOutImage(float f, float x, float y) {
        if (f >= getMinScale()) {
            postScale(f / getCurrentScale(), x, y);
        }
    }

    public void zoomInImage(float f) {
        zoomInImage(f, this.mCropRect.centerX(), this.mCropRect.centerY());
    }

    public void zoomInImage(float f, float px, float py) {
        if (f <= getMaxScale()) {
            postScale(f / getCurrentScale(), px, py);
        }
    }

    public void postMirror(boolean z) {
        postMirror(this.mCropRect.centerX(), this.mCropRect.centerY(), z);
    }

    public void postScale(float scale, float px, float py) {
        if (scale > 1.0f) {
            if (getCurrentScale() * scale > getMaxScale()) {
                scale = getMaxScale() / getCurrentScale();
            }
            super.postScale(scale, px, py);
        } else if (scale < 1.0f) {
            if (getCurrentScale() * scale < getMinScale()) {
                scale = getMinScale() / getCurrentScale();
            }
            super.postScale(scale, px, py);
        }
    }

    public void postRotate(float degrees) {
        if (degrees == 90.0f || degrees == -90.0f) {
            this.mNum0f90Rotate++;
        }
        postRotate(degrees, this.mCropRect.centerX(), this.mCropRect.centerY());
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
        return Bitmap.createBitmap(createBitmap, (int) this.mCropRect.left, (int) this.mCropRect.top, (int) (this.mCropRect.right - this.mCropRect.left), (int) (this.mCropRect.bottom - this.mCropRect.top), mCurrentImageMatrix, true);
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

    public void setImageToWrapCropBounds(boolean animate) {
        if (this.mBitmapLaidOut && !isImageWrapCropBounds()) {
            float oldX = this.mCurrentImageCenter[0];
            float oldY = this.mCurrentImageCenter[1];
            float currentScale = getCurrentScale();
            float centerX = this.mCropRect.centerX() - oldX;
            float centerY = this.mCropRect.centerY() - oldY;
            float deltaScale = 0.0f;
            this.mTempMatrix.reset();
            this.mTempMatrix.setTranslate(centerX, centerY);
            float[] copyOf = Arrays.copyOf(this.mCurrentImageCorners, this.mCurrentImageCorners.length);
            this.mTempMatrix.mapPoints(copyOf);
            boolean isImageWrapCropBounds = isImageWrapCropBounds(copyOf);
            copyOf = calculateImageIndents();
            float centerDiffX = -(copyOf[0] + copyOf[2]);
            float centerDiffY = -(copyOf[3] + copyOf[1]);
            if (!isImageWrapCropBounds) {
                RectF rectF = new RectF(this.mCropRect);
                this.mTempMatrix.reset();
                this.mTempMatrix.setRotate(getCurrentAngle());
                this.mTempMatrix.mapRect(rectF);
                float[] a = RectUtils.getRectSidesFromCorners(this.mCurrentImageCorners);
                deltaScale = (Math.max(rectF.width() / a[0], rectF.height() / a[1]) * currentScale) - currentScale;
            }
            if (animate) {
                Runnable boundsRunnable = new WrapCropBoundsRunnable(this,
                        this.mImageToWrapCropBoundsAnimDuration,
                        oldX, oldY, centerDiffX, centerDiffY, currentScale, deltaScale, isImageWrapCropBounds);
                this.mWrapCropBoundsRunnable = boundsRunnable;
                post(boundsRunnable);
                return;
            }
            postTranslate(centerDiffX, centerDiffY);
            if (!isImageWrapCropBounds) {
                zoomInImage(currentScale + deltaScale, this.mCropRect.centerX(), this.mCropRect.centerY());
            }
        }
    }

    private float[] calculateImageIndents() {
        mTempMatrix.reset();
        mTempMatrix.setRotate(-getCurrentAngle());

        float[] unrotatedImageCorners = Arrays.copyOf(mCurrentImageCorners, mCurrentImageCorners.length);
        float[] unrotatedCropBoundsCorners = RectUtils.getCornersFromRect(mCropRect);

        mTempMatrix.mapPoints(unrotatedImageCorners);
        mTempMatrix.mapPoints(unrotatedCropBoundsCorners);

        RectF unrotatedImageRect = RectUtils.trapToRect(unrotatedImageCorners);
        RectF unrotatedCropRect = RectUtils.trapToRect(unrotatedCropBoundsCorners);

        float deltaLeft = unrotatedImageRect.left - unrotatedCropRect.left;
        float deltaTop = unrotatedImageRect.top - unrotatedCropRect.top;
        float deltaRight = unrotatedImageRect.right - unrotatedCropRect.right;
        float deltaBottom = unrotatedImageRect.bottom - unrotatedCropRect.bottom;

        float indents[] = new float[4];
        indents[0] = (deltaLeft > 0) ? deltaLeft : 0;
        indents[1] = (deltaTop > 0) ? deltaTop : 0;
        indents[2] = (deltaRight < 0) ? deltaRight : 0;
        indents[3] = (deltaBottom < 0) ? deltaBottom : 0;

        mTempMatrix.reset();
        mTempMatrix.setRotate(getCurrentAngle());
        mTempMatrix.mapPoints(indents);

        return indents;
    }

    @Override
    protected void onImageLaidOut() {
        super.onImageLaidOut();
        final Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }

        float drawableWidth = drawable.getIntrinsicWidth();
        float drawableHeight = drawable.getIntrinsicHeight();

        if (mTargetAspectRatio == SOURCE_IMAGE_ASPECT_RATIO) {
            mTargetAspectRatio = drawableWidth / drawableHeight;
        }

        int height = (int) (mThisWidth / mTargetAspectRatio);
        if (height > mThisHeight) {
            int width = (int) (mThisHeight * mTargetAspectRatio);
            int halfDiff = (mThisWidth - width) / 2;
            mCropRect.set(halfDiff, 0, width + halfDiff, mThisHeight);
        } else {
            int halfDiff = (mThisHeight - height) / 2;
            mCropRect.set(0, halfDiff, mThisWidth, height + halfDiff);
        }

        calculateImageScaleBounds(drawableWidth, drawableHeight);
        setupInitialImagePosition(drawableWidth, drawableHeight);

        if (mCropBoundsChangeListener != null) {
            mCropBoundsChangeListener.onCropAspectRatioChanged(mTargetAspectRatio);
        }
        if (mTransformImageListener != null) {
            mTransformImageListener.onScale(getCurrentScale());
            mTransformImageListener.onRotate(getCurrentAngle());
        }
    }

    protected boolean isImageWrapCropBounds() {
        return isImageWrapCropBounds(mCurrentImageCorners);
    }

    protected boolean isImageWrapCropBounds(float[] imageCorners) {
        mTempMatrix.reset();
        mTempMatrix.setRotate(-getCurrentAngle());

        float[] unrotatedImageCorners = Arrays.copyOf(imageCorners, imageCorners.length);
        mTempMatrix.mapPoints(unrotatedImageCorners);

        float[] unrotatedCropBoundsCorners = RectUtils.getCornersFromRect(mCropRect);
        mTempMatrix.mapPoints(unrotatedCropBoundsCorners);

        return RectUtils.trapToRect(unrotatedImageCorners).contains(RectUtils.trapToRect(unrotatedCropBoundsCorners));
    }

    protected void zoomImageToPosition(float f, float destX, float destY, long duration) {
        if (f > getMaxScale()) {
            f = getMaxScale();
        }
        float currentScale = getCurrentScale();
        Runnable zoomImageToPosition = new ZoomImageToPosition(this, duration, currentScale, f - currentScale, destX, destY);
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

    private void calculateImageScaleBounds(float drawableWidth, float drawableHeight) {
        this.mMinScale = Math.max(this.mCropRect.width() / drawableWidth, this.mCropRect.height() / drawableHeight);
        this.mMaxScale = this.mMinScale * this.mMaxScaleMultiplier;
    }

    private void setupInitialImagePosition(float drawableWidth, float drawableHeight) {
        float width = this.mCropRect.width();
        width = ((width - (this.mMinScale * drawableWidth)) / 2.0f) + this.mCropRect.left;
        float height = ((this.mCropRect.height() - (this.mMinScale * drawableHeight)) / 2.0f) + this.mCropRect.top;
        this.mCurrentImageMatrix.reset();
        this.mCurrentImageMatrix.postScale(this.mMinScale, this.mMinScale);
        this.mCurrentImageMatrix.postTranslate(width, height);
        setImageMatrix(this.mCurrentImageMatrix);
        this.mDefaultMatrix.set(this.mCurrentImageMatrix);
    }
}
