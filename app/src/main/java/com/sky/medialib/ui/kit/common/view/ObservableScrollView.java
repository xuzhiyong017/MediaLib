package com.sky.medialib.ui.kit.common.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class ObservableScrollView extends HorizontalScrollView {

    private int scrollX;
    private boolean isMove;
    private OnScrollChangedListener mOnScrollChangedListener;
    private boolean isScrollEnable = true;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!ObservableScrollView.this.isMove) {
                if (ObservableScrollView.this.scrollX == ObservableScrollView.this.getScrollX()) {
                    ObservableScrollView.this.mOnScrollChangedListener.onStopScroll();
                }else{
                    ObservableScrollView.this.handler.removeCallbacks(ObservableScrollView.this.runnable);
                    ObservableScrollView.this.handler.postDelayed(ObservableScrollView.this.runnable, 100);
                    ObservableScrollView.this.scrollX = ObservableScrollView.this.getScrollX();
                }
            }
        }
    };

    public interface OnScrollChangedListener {
        void onStopScroll();
        void onScrollChanged(HorizontalScrollView horizontalScrollView, int i, int i2, int i3, int i4);
    }

    public ObservableScrollView(Context context) {
        super(context);
    }

    public ObservableScrollView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public ObservableScrollView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setScrollEnable(boolean z) {
        this.isScrollEnable = z;
    }

    public void setOnScrollChangedListener(OnScrollChangedListener onScrollChangedListener) {
        this.mOnScrollChangedListener = onScrollChangedListener;
    }

    protected void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
        if (this.mOnScrollChangedListener != null) {
            this.mOnScrollChangedListener.onScrollChanged(this, i, i2, i3, i4);
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (this.isMove) {
                    this.isMove = false;
                    this.handler.removeCallbacks(this.runnable);
                    this.handler.postDelayed(this.runnable, 100);
                    break;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                boolean z;
                if (Math.abs(getScrollX() - this.scrollX) > 1) {
                    z = true;
                } else {
                    z = false;
                }
                this.isMove = z;
                this.handler.removeCallbacks(this.runnable);
                break;
        }
        if (!this.isScrollEnable || super.onTouchEvent(motionEvent)) {
            return true;
        }
        return false;
    }
}
