package com.sky.medialib.ui.kit.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.sky.medialib.R;
import com.sky.medialib.ui.kit.model.VideoEffect;


public class VideoEffectIconView extends AppCompatImageView {
    private int mBlurRadius;
    private boolean mHighlight;
    private boolean mIsDrawMaskBitmap;
    private Bitmap mMaskBitmap;
    private Paint mPaint;
    private RectF mRectRing;
    private Paint mRingPaint;
    private Paint mSelectedPaint;
    private int mType;

    public VideoEffectIconView(Context context) {
        super(context);
        init();
    }

    public VideoEffectIconView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public VideoEffectIconView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void init() {
        setLayerType(1, null);
        this.mBlurRadius = getResources().getDimensionPixelSize(R.dimen.video_effect_blur_radius);
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setMaskFilter(new BlurMaskFilter((float) this.mBlurRadius, Blur.OUTER));
        this.mRingPaint = new Paint();
        this.mRingPaint.setAntiAlias(true);
        this.mRingPaint.setStyle(Style.STROKE);
        this.mRingPaint.setStrokeWidth(2.0f);
        this.mSelectedPaint = new Paint();
        this.mSelectedPaint.setAntiAlias(true);
        this.mSelectedPaint.setStyle(Style.FILL);
        this.mMaskBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.effects_time_choice_tick_mdpi);
    }

    public void setVideoEffect(VideoEffect videoEffect) {
        this.mType = videoEffect.getType();
        this.mPaint.setColor(videoEffect.getColor());
        this.mRingPaint.setColor(videoEffect.getColor());
        this.mSelectedPaint.setColor(videoEffect.getColor());
    }

    public boolean isDrawMaskBitmap() {
        return this.mIsDrawMaskBitmap;
    }

    public void setDrawMaskBitmap(boolean z) {
        this.mIsDrawMaskBitmap = z;
    }

    public void setHighlight(boolean z) {
        this.mHighlight = z;
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mType == 0) {
            if (this.mHighlight) {
                canvas.drawCircle((float) (getWidth() / 2), (float) (getHeight() / 2), (float) ((getWidth() / 2) - this.mBlurRadius), this.mPaint);
                if (this.mRectRing == null) {
                    this.mRectRing = new RectF((float) (this.mBlurRadius + 1), (float) (this.mBlurRadius + 1), (float) ((getWidth() - 1) - this.mBlurRadius), (float) ((getHeight() - 1) - this.mBlurRadius));
                }
                canvas.drawOval(this.mRectRing, this.mRingPaint);
            }
        } else if (this.mHighlight) {
            canvas.drawCircle((float) (getWidth() / 2), (float) (getHeight() / 2), (float) ((getWidth() / 2) - this.mBlurRadius), this.mSelectedPaint);
            if (this.mIsDrawMaskBitmap) {
                canvas.drawBitmap(this.mMaskBitmap, (float) ((getWidth() / 2) - (this.mMaskBitmap.getWidth() / 2)), (float) ((getHeight() / 2) - (this.mMaskBitmap.getHeight() / 2)), this.mSelectedPaint);
            }
        }
    }
}
