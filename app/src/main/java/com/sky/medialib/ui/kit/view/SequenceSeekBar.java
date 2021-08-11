package com.sky.medialib.ui.kit.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatSeekBar;


import com.sky.media.kit.video.VideoSequenceHelper.*;

import java.util.List;
import java.util.Stack;

public class SequenceSeekBar extends AppCompatSeekBar {
    private boolean mInit;
    private boolean mIsReversePlay;
    private boolean mIsShowTimeEffectColor;
    private Paint mProgressPaint = new Paint();
    private Rect mProgressRect = new Rect();
    private int mReverseColor = -855679137;
    private Stack<SequenceExt> mSequenceExts = new Stack();

    public static class SequenceExt extends BaseSequence {
        public long total;
        public boolean isReverse;
        public int color;

        public String toString() {
            return "SequenceExt{total=" + this.total + ", start=" + this.start + ", isReverse=" + this.isReverse + ", end=" + this.end + '}';
        }
    }

    public SequenceSeekBar(Context context) {
        super(context);
    }

    public SequenceSeekBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public SequenceSeekBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void setDuration(int i) {
        setMax(i);
        this.mInit = true;
    }

    public void setShowTimeEffectColor(boolean z) {
        this.mIsShowTimeEffectColor = z;
        invalidate();
    }

    public void setReversePlay(boolean z, int i) {
        int i2 = 0;
        if (this.mIsReversePlay != z) {
            this.mIsReversePlay = z;
            this.mReverseColor = i;
            SequenceExt sequenceExt;
            if (this.mIsReversePlay) {
                setProgress(getMax());
                while (i2 < this.mSequenceExts.size()) {
                    sequenceExt = (SequenceExt) this.mSequenceExts.get(i2);
                    if (!sequenceExt.isReverse) {
                        long j = sequenceExt.start;
                        sequenceExt.start = sequenceExt.total - sequenceExt.end;
                        sequenceExt.end = sequenceExt.total - j;
                        sequenceExt.isReverse = true;
                    }
                    i2++;
                }
            } else {
                setProgress(0);
                for (int i3 = 0; i3 < this.mSequenceExts.size(); i3++) {
                    sequenceExt = (SequenceExt) this.mSequenceExts.get(i3);
                    if (sequenceExt.isReverse) {
                        long j2 = sequenceExt.start;
                        sequenceExt.start = sequenceExt.total - sequenceExt.end;
                        sequenceExt.end = sequenceExt.total - j2;
                        sequenceExt.isReverse = false;
                    }
                }
            }
            invalidate();
        }
    }

    public void setSequences(List<SequenceExt> list) {
        this.mSequenceExts.clear();
        this.mSequenceExts.addAll(list);
        invalidate();
    }

    public Stack<SequenceExt> getSequences() {
        return this.mSequenceExts;
    }

    public void clearSequences() {
        this.mSequenceExts.clear();
        invalidate();
    }

    public void push(SequenceExt sequenceExt) {
        if (sequenceExt != null) {
            this.mSequenceExts.push(sequenceExt);
            invalidate();
        }
    }

    public SequenceExt pop() {
        if (this.mSequenceExts.isEmpty()) {
            return null;
        }
        SequenceExt sequenceExt = (SequenceExt) this.mSequenceExts.pop();
        invalidate();
        return sequenceExt;
    }

    public SequenceExt peek() {
        if (this.mSequenceExts.isEmpty()) {
            return null;
        }
        return (SequenceExt) this.mSequenceExts.peek();
    }

    private float dp2px(float f, Context context) {
        return ((((float) context.getResources().getDisplayMetrics().densityDpi) * f) / 160.0f) + 0.5f;
    }

    protected void onDraw(Canvas canvas) {
        int i = 0;
        int thumbOffset = getThumbOffset();
        int width = getThumb().getBounds().width();
        int width2 = getWidth() - width;
        int height = getHeight();
        int round = Math.round(dp2px(3.0f, getContext()));
        this.mProgressPaint.setColor(-2130706433);
        this.mProgressRect.set(width / 2, (height - (thumbOffset / 2)) - round, (width / 2) + width2, height - (thumbOffset / 2));
        canvas.drawRect(this.mProgressRect, this.mProgressPaint);
        if (this.mInit) {
            if (!this.mIsShowTimeEffectColor) {
                int i2;
                SequenceExt sequenceExt;
                if (!this.mIsReversePlay) {
                    this.mProgressPaint.setColor(-1);
                    this.mProgressRect.set(width / 2, (height - (thumbOffset / 2)) - round, Math.round(((((float) getProgress()) * 1.0f) / ((float) getMax())) * ((float) width2)) + (width / 2), height - (thumbOffset / 2));
                    canvas.drawRect(this.mProgressRect, this.mProgressPaint);
                    while (true) {
                        i2 = i;
                        if (i2 >= this.mSequenceExts.size()) {
                            break;
                        }
                        sequenceExt = (SequenceExt) this.mSequenceExts.get(i2);
                        this.mProgressPaint.setColor(sequenceExt.color);
                        this.mProgressRect.set(Math.round(((((float) sequenceExt.start) * 1.0f) / ((float) sequenceExt.total)) * ((float) width2)) + (width / 2), (height - (thumbOffset / 2)) - round, Math.round(((((float) sequenceExt.end) * 1.0f) / ((float) sequenceExt.total)) * ((float) width2)) + (width / 2), height - (thumbOffset / 2));
                        canvas.drawRect(this.mProgressRect, this.mProgressPaint);
                        i = i2 + 1;
                    }
                } else {
                    this.mProgressPaint.setColor(-1);
                    this.mProgressRect.set(Math.round(((((float) getProgress()) * 1.0f) / ((float) getMax())) * ((float) width2)) + (width / 2), (height - (thumbOffset / 2)) - round, (width / 2) + width2, height - (thumbOffset / 2));
                    canvas.drawRect(this.mProgressRect, this.mProgressPaint);
                    while (true) {
                        i2 = i;
                        if (i2 >= this.mSequenceExts.size()) {
                            break;
                        }
                        sequenceExt = (SequenceExt) this.mSequenceExts.get(i2);
                        this.mProgressPaint.setColor(sequenceExt.color);
                        this.mProgressRect.set((width2 - Math.round(((((float) sequenceExt.end) * 1.0f) / ((float) sequenceExt.total)) * ((float) width2))) + (width / 2), (height - (thumbOffset / 2)) - round, (width2 - Math.round(((((float) sequenceExt.start) * 1.0f) / ((float) sequenceExt.total)) * ((float) width2))) + (width / 2), height - (thumbOffset / 2));
                        canvas.drawRect(this.mProgressRect, this.mProgressPaint);
                        i = i2 + 1;
                    }
                }
            } else if (this.mIsReversePlay) {
                this.mProgressPaint.setColor(this.mReverseColor);
                this.mProgressRect.set(Math.round(((((float) getProgress()) * 1.0f) / ((float) getMax())) * ((float) width2)) + (width / 2), (height - (thumbOffset / 2)) - round, (width / 2) + width2, height - (thumbOffset / 2));
                canvas.drawRect(this.mProgressRect, this.mProgressPaint);
            } else {
                this.mProgressPaint.setColor(this.mReverseColor);
                width /= 2;
                this.mProgressRect.set(width / 2, (height - (thumbOffset / 2)) - round, width + Math.round(((float) width2) * ((((float) getProgress()) * 1.0f) / ((float) getMax()))), height - (thumbOffset / 2));
                canvas.drawRect(this.mProgressRect, this.mProgressPaint);
            }
        }
        super.onDraw(canvas);
    }
}
