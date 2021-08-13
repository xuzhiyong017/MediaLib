package com.sky.medialib.ui.kit.common.base.adapter;

import androidx.recyclerview.widget.GridLayoutManager;

public class HeaderSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {
    private HeaderFooterRecycleAdapter mAdapter;
    private int mSpanSize = 1;

    public HeaderSpanSizeLookup(HeaderFooterRecycleAdapter headerFooterRecycleAdapter, int i) {
        this.mAdapter = headerFooterRecycleAdapter;
        this.mSpanSize = i;
    }

    public int getSpanSize(int i) {
        int i2 = (this.mAdapter.isHeader(i) || this.mAdapter.isFooter(i)) ? 1 : 0;
        if (i2 != 0) {
            return this.mSpanSize;
        }
        return 1;
    }
}
