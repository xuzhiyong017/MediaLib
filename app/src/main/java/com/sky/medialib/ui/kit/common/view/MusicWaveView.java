package com.sky.medialib.ui.kit.common.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Random;

public class MusicWaveView extends View {
    private int duration;
    private int progress;
    private int voiceWidth;
    private int voiceHeight;
    private Paint paint;
    private PorterDuffXfermode xfermode = new PorterDuffXfermode(Mode.SRC_IN);
    private RectF rect = new RectF();
    private int[] randowList;
    private int screenWidth;
    private int offsetx;
    private float playDuration = 15000.0f;

    public MusicWaveView(Context context) {
        super(context);
        init();
    }

    public MusicWaveView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    private void init() {
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.screenWidth = getResources().getDisplayMetrics().widthPixels;
    }

    public void setPlayDuration(float f) {
        this.playDuration = f;
    }

    public long getDuration() {
        return (long) this.duration;
    }

    public void setDuration(int i) {
        this.duration = i;
        this.voiceWidth = Math.max(Math.round((this.duration * 1080 * 1.0f) / this.playDuration), this.screenWidth);
        this.voiceHeight = 120;
        int i2 = this.voiceWidth / 20;
        this.randowList = new int[i2];
        Random random = new Random();
        for (int i3 = 0; i3 < i2; i3++) {
            this.randowList[i3] = random.nextInt(95) + 16;
        }
        requestLayout();
    }

    public void setProgress(int i) {
        this.progress = i;
        invalidate();
    }

    public void setOffsetX(int i) {
        this.offsetx = i;
        invalidate();
    }

    public int getOffsetX() {
        return this.offsetx;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.duration != 0) {
            int saveLayer = canvas.saveLayer((float) this.offsetx, 0.0f, (float) (this.offsetx + this.screenWidth), (float) this.voiceHeight, this.paint, 31);
            this.paint.setColor(-4408132);
            for (int i = 0; i < this.randowList.length; i++) {
                this.rect.set((float) (i * 20), (float) ((this.voiceHeight - this.randowList[i]) / 2), (float) ((i * 20) + 10), (float) ((this.voiceHeight + this.randowList[i]) / 2));
                canvas.drawRoundRect(this.rect, 5.0f, 5.0f, this.paint);
            }
            this.paint.setXfermode(this.xfermode);
            this.paint.setColor(-26552);
            canvas.drawRect(0.0f, 0.0f, (float) Math.round((((float) (this.progress * 1080)) * 1.0f) / this.playDuration), (float) this.voiceHeight, this.paint);
            this.paint.setXfermode(null);
            canvas.restoreToCount(saveLayer);
        }
    }

    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(this.voiceWidth, this.voiceHeight);
    }
}
