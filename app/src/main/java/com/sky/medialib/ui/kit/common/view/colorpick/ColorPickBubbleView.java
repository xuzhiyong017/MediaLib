package com.sky.medialib.ui.kit.common.view.colorpick;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.sky.medialib.util.PixelUtil;

public class ColorPickBubbleView extends View {

    private Paint paint;
    private Path path;
    private RectF rectF;
    private int width;
    private int color;

    public ColorPickBubbleView(Context context) {
        this(context, null);
    }

    public ColorPickBubbleView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ColorPickBubbleView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.color = -1;
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setTextAlign(Align.CENTER);
        this.path = new Path();
        this.rectF = new RectF();
        this.width = (int) PixelUtil.dip2px(17.0f, getContext());
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        setMeasuredDimension(this.width * 3, this.width * 3);
        this.rectF.set(0.0f, (float) (this.width / 2), (float) (this.width * 2), (float) ((this.width * 2) + (this.width / 2)));
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.path.reset();
        this.path.arcTo(this.rectF, 60.0f, 240.0f);
        this.path.quadTo((float) ((this.width * 3) / 2), (float) (((double) (getWidth() / 2)) - ((Math.sqrt(3.0d) / 2.0d) * ((double) this.width))), ((float) getWidth()) - (((float) this.width) / 3.0f), (float) (getHeight() / 2));
        this.path.close();
        this.paint.setColor(this.color);
        canvas.drawPath(this.path, this.paint);
    }

    public void setColor(int i) {
        if (this.color != i) {
            this.color = i;
            invalidate();
        }
    }
}
