package com.sky.medialib.ui.kit.view.crop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import androidx.appcompat.widget.AppCompatImageView;

import com.sky.media.image.core.util.LogUtils;

public class TransformImageView extends AppCompatImageView {
    private static final int MATRIX_VALUES_COUNT = 9;
    private static final int RECT_CENTER_POINT_COORDS = 2;
    private static final int RECT_CORNER_POINTS_COORDS = 8;
    private static final String TAG = "TransformImageView";
    protected boolean mBitmapDecoded;
    protected boolean mBitmapLaidOut;
    protected final float[] mCurrentImageCenter;
    protected final float[] mCurrentImageCorners;
    protected Matrix mCurrentImageMatrix;
    protected Matrix mDefaultMatrix;
    private float[] mInitialImageCenter;
    private float[] mInitialImageCorners;
    protected boolean mIsMirrored;
    private final float[] mMatrixValues;
    private int mMaxBitmapSize;
    protected int mNum0f90Rotate;
    protected int mThisHeight;
    protected int mThisWidth;
    protected TransformImageListener mTransformImageListener;

    public interface TransformImageListener {

        void onLoadComplete();

        void onRotate(float f);

        void onScale(float f);
    }

    public TransformImageView(Context context) {
        this(context, null);
    }

    public TransformImageView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public TransformImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mCurrentImageCorners = new float[8];
        this.mCurrentImageCenter = new float[2];
        this.mMatrixValues = new float[9];
        this.mCurrentImageMatrix = new Matrix();
        this.mDefaultMatrix = new Matrix();
        this.mBitmapDecoded = false;
        this.mBitmapLaidOut = false;
        this.mNum0f90Rotate = 0;
        this.mMaxBitmapSize = 0;
        init();
    }

    public void setTransformImageListener(TransformImageListener transformImageListener) {
        this.mTransformImageListener = transformImageListener;
    }

    public void setScaleType(ScaleType scaleType) {
        if (scaleType == ScaleType.MATRIX) {
            super.setScaleType(scaleType);
        }
    }

    public void setMaxBitmapSize(int i) {
        this.mMaxBitmapSize = i;
    }

    public int getMaxBitmapSize() {
        if (this.mMaxBitmapSize <= 0) {
            this.mMaxBitmapSize = calculateMaxBitmapSize();
        }
        return this.mMaxBitmapSize;
    }

    public void setImageBitmap(Bitmap bitmap) {
        this.mBitmapDecoded = true;
        setImageDrawable(new FastBitmapDrawable(bitmap));
        onImageLaidOut();
        invalidate();
    }

    public void changeBitmap(Bitmap bitmap) {
        setImageDrawable(new FastBitmapDrawable(bitmap));
        setImageMatrix(this.mCurrentImageMatrix);
    }

    public float getCurrentScale() {
        return getMatrixScale(this.mCurrentImageMatrix);
    }

    public float getMatrixScale(Matrix matrix) {
        return (float) Math.sqrt(Math.pow((double) getMatrixValue(matrix, 0), 2.0d) + Math.pow((double) getMatrixValue(matrix, 3), 2.0d));
    }

    public float getCurrentAngle() {
        return getMatrixAngle(this.mCurrentImageMatrix);
    }

    public float getMatrixAngle(Matrix matrix) {
        if (this.mIsMirrored) {
            return (float) (Math.atan2((double) getMatrixValue(matrix, 1), (double) getMatrixValue(matrix, 0)) * 57.29577951308232d);
        }
        return (float) (-(Math.atan2((double) getMatrixValue(matrix, 1), (double) getMatrixValue(matrix, 0)) * 57.29577951308232d));
    }

    public void setImageMatrix(Matrix matrix) {
        super.setImageMatrix(matrix);
        updateCurrentImagePoints();
    }

    public Bitmap getViewBitmap() {
        if (getDrawable() == null || !(getDrawable() instanceof FastBitmapDrawable)) {
            return null;
        }
        return ((FastBitmapDrawable) getDrawable()).getSourceBitmap();
    }

    public void postTranslate(float f, float f2) {
        if (f != 0.0f || f2 != 0.0f) {
            this.mCurrentImageMatrix.postTranslate(f, f2);
            setImageMatrix(this.mCurrentImageMatrix);
        }
    }

    public void postScale(float f, float f2, float f3) {
        if (f != 0.0f) {
            this.mCurrentImageMatrix.postScale(f, f, f2, f3);
            setImageMatrix(this.mCurrentImageMatrix);
            if (this.mTransformImageListener != null) {
                this.mTransformImageListener.onScale(getMatrixScale(this.mCurrentImageMatrix));
            }
        }
    }

    public void postMirror(float f, float f2, boolean z) {
        if (z) {
            this.mCurrentImageMatrix.postScale(-1.0f, 1.0f, f, f2);
        } else {
            this.mCurrentImageMatrix.postScale(1.0f, -1.0f, f, f2);
        }
        this.mIsMirrored = !this.mIsMirrored;
        setImageMatrix(this.mCurrentImageMatrix);
    }

    public void postRotate(float f, float f2, float f3) {
        if (f != 0.0f) {
            this.mCurrentImageMatrix.postRotate(f, f2, f3);
            setImageMatrix(this.mCurrentImageMatrix);
            if (this.mTransformImageListener != null) {
                this.mTransformImageListener.onRotate(getMatrixAngle(this.mCurrentImageMatrix));
            }
        }
    }

    public boolean isBitmapTransformed() {
        return !this.mCurrentImageMatrix.equals(this.mDefaultMatrix);
    }

    protected void init() {
        setScaleType(ScaleType.MATRIX);
    }

    protected int calculateMaxBitmapSize() {
        int width;
        int height;
        Display defaultDisplay = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point point = new Point();
        if (VERSION.SDK_INT >= 13) {
            defaultDisplay.getSize(point);
            width = point.x;
            height = point.y;
        } else {
            width = defaultDisplay.getWidth();
            height = defaultDisplay.getHeight();
        }
        return ((int) Math.sqrt(Math.pow((double) height, 2.0d) + Math.pow((double) width, 2.0d))) * 2;
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (z || (this.mBitmapDecoded && !this.mBitmapLaidOut)) {
            int paddingLeft = getPaddingLeft();
            int paddingTop = getPaddingTop();
            int height = getHeight() - getPaddingBottom();
            this.mThisWidth = (getWidth() - getPaddingRight()) - paddingLeft;
            this.mThisHeight = height - paddingTop;
            onImageLaidOut();
        }
    }

    protected void onImageLaidOut() {
        Drawable drawable = getDrawable();
        if (drawable != null) {
            float intrinsicWidth = (float) drawable.getIntrinsicWidth();
            float intrinsicHeight = (float) drawable.getIntrinsicHeight();
            Log.d(TAG, String.format("Image size: [%d:%d]", new Object[]{Integer.valueOf((int) intrinsicWidth), Integer.valueOf((int) intrinsicHeight)}));
            RectF rectF = new RectF(0.0f, 0.0f, intrinsicWidth, intrinsicHeight);
            this.mInitialImageCorners = RectUtils.getCornersFromRect(rectF);
            this.mInitialImageCenter = RectUtils.getCenterFromRect(rectF);
            this.mIsMirrored = false;
            this.mBitmapLaidOut = true;
            this.mNum0f90Rotate = 0;
            if (this.mTransformImageListener != null) {
                this.mTransformImageListener.onLoadComplete();
            }
        }
    }

    protected float getMatrixValue(Matrix matrix, int i) {
        matrix.getValues(this.mMatrixValues);
        return this.mMatrixValues[i];
    }

    protected void printMatrix(String str, Matrix matrix) {
        float matrixValue = getMatrixValue(matrix, 2);
        float matrixValue2 = getMatrixValue(matrix, 5);
        float matrixScale = getMatrixScale(matrix);
        LogUtils.logd("TransformImageView", str + ": matrix: { x: " + matrixValue + ", y: " + matrixValue2 + ", scale: " + matrixScale + ", angle: " + getMatrixAngle(matrix) + " }");
    }

    protected void printMatrix() {
        printMatrix("mCurrentImageMatrix", this.mCurrentImageMatrix);
    }

    protected void updateCurrentImagePoints() {
        this.mCurrentImageMatrix.mapPoints(this.mCurrentImageCorners, this.mInitialImageCorners);
        this.mCurrentImageMatrix.mapPoints(this.mCurrentImageCenter, this.mInitialImageCenter);
    }
}
