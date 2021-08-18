package com.sky.medialib.ui.kit.common.view.colorpick;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.sky.medialib.R;
import com.sky.medialib.util.PixelUtil;


public class ColorPickerSeekBar extends View {

    private int[] colorList;
    private Paint itemPaint;
    private Paint lineInnerPaint;
    private Paint horiLinePaint;
    private int width;
    private int left;
    private int itemHeight;
    private int offsetY;
    private int[] itemListCenterY;
    private OnChangeListener onChangeListener;

    public interface OnChangeListener {
        void changeColor(int i, int i2);
        void onStartTouch(boolean z);
    }

    public ColorPickerSeekBar(Context context) {
        super(context);
        init(context, null, 0);
    }

    public ColorPickerSeekBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context, attributeSet, 0);
    }

    public ColorPickerSeekBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context, attributeSet, i);
    }

    private void init(Context context, AttributeSet attributeSet, int i) {
        this.colorList = context.getResources().getIntArray(R.array.edit_text_colors);
        this.left = (int) PixelUtil.dip2px(10.0f, getContext());
        int a = ((int) PixelUtil.dip2px(17.0f, getContext())) / 2;
        int a2 = ((int) PixelUtil.dip2px(20.0f, getContext())) / 2;
        this.itemPaint = new Paint();
        this.lineInnerPaint = new Paint();
        this.lineInnerPaint.setColor(this.colorList[2]);
        this.lineInnerPaint.setAntiAlias(true);
        this.lineInnerPaint.setStrokeCap(Cap.ROUND);
        this.lineInnerPaint.setStrokeWidth((float) a);
        this.horiLinePaint = new Paint();
        this.horiLinePaint.setColor(-1);
        this.horiLinePaint.setAntiAlias(true);
        this.horiLinePaint.setStrokeWidth((float) a2);
        this.horiLinePaint.setStyle(Style.STROKE);
        this.horiLinePaint.setStrokeCap(Cap.ROUND);
    }


        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.itemHeight = h / this.colorList.length;
        this.itemListCenterY = new int[this.colorList.length];
        for (int i = 0; i < this.itemListCenterY.length; i++) {
            this.itemListCenterY[i] = (this.itemHeight / 2) + (this.itemHeight * i);
        }
        this.offsetY = this.itemListCenterY[0];
    }

    protected void onDraw(Canvas canvas) {
        int i = 0;
        int left = this.left;
        int right = this.width - this.left;
        int top = (this.width - (this.left * 2)) / 2;
        int index = 0;
        while (index < this.colorList.length) {
            int bottom;
            if (index == 0 || index == this.colorList.length - 1) {
                bottom = (this.itemHeight + top) - ((this.width - (this.left * 2)) / 2);
            } else {
                bottom = top + this.itemHeight;
            }
            this.itemPaint.setColor(this.colorList[index]);
            canvas.drawRect((float) left, (float) top, (float) right, (float) bottom, this.itemPaint);
            index++;
            top = bottom;
        }
        this.itemPaint.setColor(this.colorList[0]);
        canvas.drawCircle((float) (this.width / 2), (float) ((this.width - (this.left * 2)) / 2), (float) ((this.width - (this.left * 2)) / 2), this.itemPaint);
        this.itemPaint.setColor(this.colorList[this.colorList.length - 1]);
        canvas.drawCircle((float) (this.width / 2), (float) top, (float) ((this.width - (this.left * 2)) / 2), this.itemPaint);
        canvas.drawLine((float) ((this.left * 4) / 5), (float) this.offsetY, (float) (this.width - ((this.left * 4) / 5)), (float) this.offsetY, this.horiLinePaint);
        while (i < this.itemListCenterY.length) {
            if (this.offsetY >= this.itemListCenterY[i] - (this.itemHeight / 2) && this.offsetY <= this.itemListCenterY[i] + (this.itemHeight / 2)) {
                this.lineInnerPaint.setColor(this.colorList[i]);
                break;
            }
            i++;
        }
        canvas.drawLine((float) ((this.left * 4) / 5), (float) this.offsetY, (float) (this.width - ((this.left * 4) / 5)), (float) this.offsetY, this.lineInnerPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (motionEvent.getY() < itemListCenterY[0] - (itemHeight / 4)) {
                    offsetY = itemListCenterY[0] - (itemHeight / 4);
                } else if (motionEvent.getY() > ((float) (itemListCenterY[itemListCenterY.length - 1] + (itemHeight / 4)))) {
                    offsetY = itemListCenterY[itemListCenterY.length - 1] + (itemHeight / 4);
                } else {
                    offsetY = (int) motionEvent.getY();
                }
                if (onChangeListener != null) {
                    onChangeListener.onStartTouch(true);
                }
                for (int j = 0; j < itemListCenterY.length; j++) {
                    if (motionEvent.getY() >= ((float) (itemListCenterY[j] - (itemHeight / 2))) && motionEvent.getY() <= ((float) (itemListCenterY[j] + (itemHeight / 2)))) {
                        if (onChangeListener != null) {
                            onChangeListener.changeColor(offsetY, colorList[j]);
                            break;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (motionEvent.getY() < ((itemListCenterY[0] - (itemHeight / 4)))) {
                    offsetY = itemListCenterY[0] - (itemHeight / 4);
                } else if (motionEvent.getY() > ((float) (itemListCenterY[itemListCenterY.length - 1] + (itemHeight / 4)))) {
                    offsetY = itemListCenterY[itemListCenterY.length - 1] + (itemHeight / 4);
                } else {
                    offsetY = (int) motionEvent.getY();
                }
                if (motionEvent.getY() < ((float) (itemListCenterY[0] - (itemHeight / 2)))) {
                    if (onChangeListener != null) {
                        onChangeListener.changeColor(offsetY, colorList[0]);
                    }
                } else if (motionEvent.getY() <= ((float) (itemListCenterY[itemListCenterY.length - 1] - (itemHeight / 2)))) {
                    int length = itemListCenterY.length - 1;
                    while (length >= 0) {
                        if (motionEvent.getY() < ((float) (itemListCenterY[length] - (itemHeight / 2)))) {
                            length--;
                        } else if (onChangeListener != null) {
                            onChangeListener.changeColor(offsetY, colorList[length]);
                            break;
                        }
                    }
                } else if (onChangeListener != null) {
                    onChangeListener.changeColor(offsetY, colorList[colorList.length - 1]);
                }
                if (onChangeListener != null) {
                    onChangeListener.onStartTouch(false);
                    break;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (motionEvent.getY() < ((float) (itemListCenterY[0] - (itemHeight / 4)))) {
                    offsetY = itemListCenterY[0] - (itemHeight / 4);
                } else if (motionEvent.getY() > ((float) (itemListCenterY[itemListCenterY.length - 1] + (itemHeight / 4)))) {
                    offsetY = itemListCenterY[itemListCenterY.length - 1] + (itemHeight / 4);
                } else {
                    offsetY = (int) motionEvent.getY();
                }
                for (int i = itemListCenterY.length - 1; i >= 0; i--) {
                    if (motionEvent.getY() >= ((float) (itemListCenterY[i] - (itemHeight / 2)))) {
                        if (onChangeListener != null) {
                            onChangeListener.changeColor(offsetY, colorList[i]);
                            break;
                        }
                    }
                }
                break;
        }
        invalidate();
        return true;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent motionEvent) {
        return onTouchEvent(motionEvent);
    }

    public int getTrackerPosition() {
        return offsetY;
    }

    public void setSeekBarPositionByColor(int i) {
        int i2 = 0;
        while (i2 < colorList.length - 1) {
            if (i == colorList[i2]) {
                break;
            }
            i2++;
        }
        i2 = 0;
        offsetY = itemListCenterY[i2];
        invalidate();
        if (onChangeListener != null) {
            onChangeListener.onStartTouch(false);
        }
    }

    public void setITrackerListener(OnChangeListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }
}
