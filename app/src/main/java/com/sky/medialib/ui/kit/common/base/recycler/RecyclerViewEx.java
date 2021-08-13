package com.sky.medialib.ui.kit.common.base.recycler;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.sky.medialib.ui.kit.common.base.recycler.RecyclerViewClickSupport.*;


public class RecyclerViewEx extends RecyclerView {
    public RecyclerViewEx(Context context) {
        super(context);
    }

    public RecyclerViewEx(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public RecyclerViewEx(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        if (onItemClickListener != null) {
            RecyclerViewClickSupport.getRecycleViewClickSupport((RecyclerView) this).setOnItemClickListener(onItemClickListener);
        }
    }

    public void setOnItemLongClickListener(onItemLongClickListener onItemLongClickListener) {
        if (onItemLongClickListener != null) {
            RecyclerViewClickSupport.getRecycleViewClickSupport((RecyclerView) this).setOnItemLongClickListener(onItemLongClickListener);
        }
    }

    public int getScrollYDistance() {
        LayoutManager layoutManager = getLayoutManager();
        int findFirstVisibleItemPosition;
        View findViewByPosition;
        if (layoutManager instanceof LinearLayoutManager) {
            findFirstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
            findViewByPosition = layoutManager.findViewByPosition(findFirstVisibleItemPosition);
            return (findFirstVisibleItemPosition * findViewByPosition.getHeight()) - findViewByPosition.getTop();
        } else if (!(layoutManager instanceof StaggeredGridLayoutManager)) {
            return 0;
        } else {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            if (layoutManager.getChildCount() > 0) {
                findFirstVisibleItemPosition = staggeredGridLayoutManager.findFirstVisibleItemPositions(null)[0];
            } else {
                findFirstVisibleItemPosition = 0;
            }
            findViewByPosition = layoutManager.findViewByPosition(findFirstVisibleItemPosition);
            return (findFirstVisibleItemPosition * findViewByPosition.getHeight()) - findViewByPosition.getTop();
        }
    }
}
