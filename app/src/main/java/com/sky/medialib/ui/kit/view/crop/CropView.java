package com.sky.medialib.ui.kit.view.crop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.sky.medialib.R;


public class CropView extends RelativeLayout {
    private CropRectView mCropRectView;
    private GestureCropImageView mGestureCropImageView;
    private int ratioIndex;
    private int lastRatioIndex;
    private float rotateDegree;
    private float lastRotateDegree;
    private Bitmap sourceBitmap;
    private OnImageClippedListener mClippedListener;

    public interface OnImageClippedListener {
        void onClipSuccess(Bitmap bitmap);
    }

    public CropView(Context context) {
        super(context);
        initView(context);
    }

    public CropView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView(context);
    }

    private void initView(Context context) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.view_crop_popup, this);
        this.mCropRectView = (CropRectView) inflate.findViewById(R.id.crop_rect);
        this.mCropRectView.drawGridLine(true);
        this.mCropRectView.setOnRectChangeListener(new CropRectView.OnRectChangeListener() {
            @Override
            public void onRectChange(float f, RectF rectF, boolean z, boolean z2) {
                CropView.this.mGestureCropImageView.setTargetAspectRatio(f);
                CropView.this.mGestureCropImageView.setCropRect(rectF, z2, z, false);
            }
        });
        this.mGestureCropImageView = (GestureCropImageView) inflate.findViewById(R.id.crop_image);
        this.mGestureCropImageView.setCropBoundsChangeListener(new CropBoundsChangeListener() {
            @Override
            public void onCropAspectRatioChanged(float f) {
                mCropRectView.cropAspectRatio(f);
            }
        });
    }
    public void setBitmap(Bitmap bitmap, Bitmap bitmap2) {
        if (bitmap != null) {
            this.ratioIndex = 0;
            this.rotateDegree = 0.0f;
            this.mGestureCropImageView.setImageBitmap(bitmap);
            this.sourceBitmap = bitmap2;
        }
    }

    public void cropImageByRatio(float ratio, int ratioIndex) {
        if ((this.mCropRectView.mo17993a() && ratio != 0.0f) || !(this.mCropRectView.mo17993a() || ratio == this.mCropRectView.getRatio())) {
            this.mGestureCropImageView.cancelAllAnimations();
            this.mCropRectView.setRatio(ratio);
        }
        this.ratioIndex = ratioIndex;
    }

    public int getRatioIndex() {
        return this.ratioIndex;
    }

    public float getRotateDegrees() {
        return this.rotateDegree;
    }

    public void saveState() {
        this.mCropRectView.saveState();
        this.mGestureCropImageView.saveState();
        this.lastRatioIndex = this.ratioIndex;
        this.lastRotateDegree = this.rotateDegree;
    }

    public void clipReset() {
        this.mCropRectView.resumeState();
        this.mGestureCropImageView.resumeState();
        this.ratioIndex = this.lastRatioIndex;
        this.rotateDegree = this.lastRotateDegree;
    }

    public void setImageToWrapCropBounds(boolean z) {
        this.mGestureCropImageView.setImageToWrapCropBounds(z);
    }

    public void rotate(float f) {
        this.mGestureCropImageView.cancelAllAnimations();
        float viewRatio;
        if (f <= -71.0f || f >= 71.0f) {
            float ratio = this.mCropRectView.getRatio();
            viewRatio = this.mCropRectView.getViewRatio();
            float f2 = 1.0f / viewRatio;
            if (ratio >= viewRatio) {
                if (ratio > f2) {
                    viewRatio = f2;
                } else {
                    viewRatio = ratio;
                }
            }
            this.mGestureCropImageView.postRotate(f);
            this.mCropRectView.setRatio(1.0f / ratio, false, false);
            this.mGestureCropImageView.postScale(viewRatio, this.mGestureCropImageView.mCurrentImageCenter[0], this.mGestureCropImageView.mCurrentImageCenter[1]);
            return;
        }
        viewRatio = f - this.rotateDegree;
        this.rotateDegree += viewRatio;
        this.mGestureCropImageView.postRotate(viewRatio);
    }

    public void postMirror(boolean z) {
        this.mGestureCropImageView.postMirror(z);
    }

    public void setAreaVisible(boolean z) {
        this.mCropRectView.setAreaVisible(z);
        this.mGestureCropImageView.setAreaVisible(z);
    }

    public void setOnImageClippedListener(OnImageClippedListener onImageClippedListener) {
        this.mClippedListener = onImageClippedListener;
    }

    public boolean mo18014c() {
        return this.mCropRectView.mo17996d() || this.mGestureCropImageView.isBitmapTransformed();
    }

    public void mo18015d() {
        if (this.mCropRectView.mo17996d() || this.mGestureCropImageView.isBitmapTransformed()) {
            if (this.sourceBitmap != null) {
                this.mGestureCropImageView.changeBitmap(this.sourceBitmap);
            }
            Bitmap clipBitmap = this.mGestureCropImageView.getClipBitmap();
            setBitmap(clipBitmap, this.sourceBitmap == null ? null : clipBitmap);
            if (this.mClippedListener != null) {
                this.mClippedListener.onClipSuccess(clipBitmap);
            }
        }
    }

    public void setVisibility(int i) {
        super.setVisibility(i);
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case 0:
                this.mCropRectView.setOutAreaVisible(true);
                break;
            case 1:
            case 3:
                this.mCropRectView.setOutAreaVisible(false);
                break;
        }
        return super.dispatchTouchEvent(motionEvent);
    }
}
