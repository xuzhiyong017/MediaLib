package com.sky.medialib.ui.kit.common.base.recycler;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

public class NestedRecyclerView extends RecyclerViewEx {
    private static final int INVALID_POINTER = -1;
    private int mInitialTouchX;
    private int mInitialTouchY;
    private int mScrollPointerId = -1;
    private int mTouchSlop;

    public NestedRecyclerView(Context context) {
        super(context);
        init();
    }

    public NestedRecyclerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public NestedRecyclerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void init() {
        this.mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    public void setScrollingTouchSlop(int i) {
        super.setScrollingTouchSlop(i);
        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        switch (i) {
            case 0:
                this.mTouchSlop = viewConfiguration.getScaledTouchSlop();
                return;
            case 1:
                this.mTouchSlop = viewConfiguration.getScaledPagingTouchSlop();
                return;
            default:
                return;
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        boolean z = true;
        int actionMasked = motionEvent.getActionMasked();
        int actionIndex = motionEvent.getActionIndex();
        switch (actionMasked) {
            case 0:
                this.mScrollPointerId = motionEvent.getPointerId(0);
                this.mInitialTouchX = (int) (motionEvent.getX() + 0.5f);
                this.mInitialTouchY = (int) (motionEvent.getY() + 0.5f);
                return super.onInterceptTouchEvent(motionEvent);
            case 2:
                actionMasked = motionEvent.findPointerIndex(this.mScrollPointerId);
                if (actionMasked < 0) {
                    return false;
                }
                actionIndex = (int) (motionEvent.getX(actionMasked) + 0.5f);
                actionMasked = (int) (motionEvent.getY(actionMasked) + 0.5f);
                if (getScrollState() == 1) {
                    return super.onInterceptTouchEvent(motionEvent);
                }
                boolean z2;
                actionIndex -= this.mInitialTouchX;
                int i = actionMasked - this.mInitialTouchY;
                boolean canScrollHorizontally = getLayoutManager().canScrollHorizontally();
                boolean canScrollVertically = getLayoutManager().canScrollVertically();
                if (!canScrollHorizontally || Math.abs(actionIndex) <= this.mTouchSlop || (Math.abs(actionIndex) < Math.abs(i) && !canScrollVertically)) {
                    z2 = false;
                } else {
                    z2 = true;
                }
                if (canScrollVertically && Math.abs(i) > this.mTouchSlop && (Math.abs(i) >= Math.abs(actionIndex) || canScrollHorizontally)) {
                    z2 = true;
                }
                if (!(z2 && super.onInterceptTouchEvent(motionEvent))) {
                    z = false;
                }
                return z;
            case 5:
                this.mScrollPointerId = motionEvent.getPointerId(actionIndex);
                this.mInitialTouchX = (int) (motionEvent.getX(actionIndex) + 0.5f);
                this.mInitialTouchY = (int) (motionEvent.getY(actionIndex) + 0.5f);
                return super.onInterceptTouchEvent(motionEvent);
            default:
                return super.onInterceptTouchEvent(motionEvent);
        }
    }
}
