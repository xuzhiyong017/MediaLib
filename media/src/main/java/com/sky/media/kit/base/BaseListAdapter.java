package com.sky.media.kit.base;

import android.content.Context;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseListAdapter<T> extends BaseAdapter {

    protected Context mContext;

    protected List<T> mData = newList();

    public BaseListAdapter(Context context) {
        this.mContext = context;
    }

    public void replaceList(List<T> list) {
        if (list != null) {
            if (this.mData.size() > 0) {
                this.mData.clear();
            }
            this.mData.addAll(list);
            notifyDataSetChanged();
        }
    }

    public T getItem(int i) {
        if (this.mData == null || i >= this.mData.size()) {
            return null;
        }
        return this.mData.get(i);
    }

    public long getItemId(int i) {
        if (this.mData == null || i == this.mData.size()) {
            return 0;
        }
        return (long) i;
    }

    public int getCount() {
        if (this.mData != null) {
            return this.mData.size();
        }
        return 0;
    }

    protected List<T> newList() {
        return new ArrayList();
    }
}
