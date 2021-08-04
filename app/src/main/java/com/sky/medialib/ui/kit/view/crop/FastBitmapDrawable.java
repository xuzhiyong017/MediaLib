package com.sky.medialib.ui.kit.view.crop;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

public class FastBitmapDrawable extends Drawable {

    private final Paint mPaint = new Paint(2);

    private Bitmap sourceBitmap;
    private int alpha = 255;
    private int width;
    private int height;

    public FastBitmapDrawable(Bitmap bitmap) {
        initBitmap(bitmap);
    }

    public void draw(Canvas canvas) {
        if (this.sourceBitmap != null && !this.sourceBitmap.isRecycled()) {
            canvas.drawBitmap(this.sourceBitmap, null, getBounds(), this.mPaint);
        }
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
    }

    public int getOpacity() {
        return -3;
    }

    public void setFilterBitmap(boolean z) {
        this.mPaint.setFilterBitmap(z);
    }

    public int getAlpha() {
        return this.alpha;
    }

    public void setAlpha(int i) {
        this.alpha = i;
        this.mPaint.setAlpha(i);
    }

    public int getIntrinsicWidth() {
        return this.width;
    }

    public int getIntrinsicHeight() {
        return this.height;
    }

    public int getMinimumWidth() {
        return this.width;
    }

    public int getMinimumHeight() {
        return this.height;
    }


    public Bitmap getSourceBitmap() {
        return this.sourceBitmap;
    }


    public void initBitmap(Bitmap bitmap) {
        this.sourceBitmap = bitmap;
        if (bitmap != null) {
            this.width = this.sourceBitmap.getWidth();
            this.height = this.sourceBitmap.getHeight();
            return;
        }
        this.height = 0;
        this.width = 0;
    }
}
