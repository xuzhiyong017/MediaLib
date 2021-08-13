package com.sky.medialib.ui.kit.common.base.recycler;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ListItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable mDivider;
    private int mDividerSize = 0;
    private int mHalfSpace = 0;

    public ListItemDecoration(int i) {
        this.mHalfSpace = i / 2;
    }

    public ListItemDecoration(Drawable drawable, int i) {
        this.mDivider = drawable;
        this.mDividerSize = i;
    }

    @Override
    public void getItemOffsets(@NonNull Rect rect, @NonNull View view, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.State state) {
        if (recyclerView.getChildPosition(view) >= 1) {
            if (this.mDivider != null) {
                if (getOrientation(recyclerView) == 1) {
                    rect.top = this.mDividerSize > 0 ? this.mDividerSize : this.mDivider.getIntrinsicHeight();
                } else {
                    rect.left = this.mDividerSize > 0 ? this.mDividerSize : this.mDivider.getIntrinsicWidth();
                }
            } else if (getOrientation(recyclerView) == 0) {
                rect.left = this.mHalfSpace;
                rect.right = this.mHalfSpace;
            } else {
                rect.top = this.mHalfSpace;
                rect.bottom = this.mHalfSpace;
            }
        }
    }

    @Override
    public void onDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.State state) {
        int paddingLeft;
        int width;
        int childCount;
        int i;
        View childAt;
        int top;
        if (this.mDivider == null) {
            super.onDrawOver(canvas, recyclerView, state);
        } else if (getOrientation(recyclerView) == 1) {
            paddingLeft = recyclerView.getPaddingLeft();
            width = recyclerView.getWidth() - recyclerView.getPaddingRight();
            childCount = recyclerView.getChildCount();
            for (i = 1; i < childCount; i++) {
                childAt = recyclerView.getChildAt(i);
                top = childAt.getTop() - ((RecyclerView.LayoutParams) childAt.getLayoutParams()).topMargin;
                this.mDivider.setBounds(paddingLeft, top, width, (this.mDividerSize > 0 ? this.mDividerSize : this.mDivider.getIntrinsicHeight()) + top);
                this.mDivider.draw(canvas);
            }
        } else {
            paddingLeft = recyclerView.getPaddingTop();
            width = recyclerView.getHeight() - recyclerView.getPaddingBottom();
            childCount = recyclerView.getChildCount();
            for (i = 1; i < childCount; i++) {
                childAt = recyclerView.getChildAt(i);
                top = childAt.getLeft() - ((RecyclerView.LayoutParams) childAt.getLayoutParams()).leftMargin;
                this.mDivider.setBounds(top, paddingLeft, (this.mDividerSize > 0 ? this.mDividerSize : this.mDivider.getIntrinsicWidth()) + top, width);
                this.mDivider.draw(canvas);
            }
        }
    }

    private int getOrientation(RecyclerView recyclerView) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) recyclerView.getLayoutManager()).getOrientation();
        }
        return 1;
    }
}
