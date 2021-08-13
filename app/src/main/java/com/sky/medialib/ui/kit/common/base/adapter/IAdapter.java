package com.sky.medialib.ui.kit.common.base.adapter;

public interface IAdapter<T> {
    Item<T> createItem(Object obj);

    void notifyDataSetChanged();
}
