package com.sky.medialib.ui.kit.common.base.recycler;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GridItemDecoration extends RecyclerView.ItemDecoration {
    private int halfSpace;

    public GridItemDecoration(int i) {
        this.halfSpace = i / 2;
    }

    @Override
    public void getItemOffsets(@NonNull Rect rect, @NonNull View view, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.State state) {
        if (recyclerView.getPaddingLeft() != this.halfSpace) {
            recyclerView.setPadding(this.halfSpace, this.halfSpace, this.halfSpace, this.halfSpace);
            recyclerView.setClipToPadding(false);
        }
        rect.top = this.halfSpace;
        rect.bottom = this.halfSpace;
        rect.left = this.halfSpace;
        rect.right = this.halfSpace;
    }

}
