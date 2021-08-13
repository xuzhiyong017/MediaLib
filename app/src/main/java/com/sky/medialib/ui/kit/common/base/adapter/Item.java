package com.sky.medialib.ui.kit.common.base.adapter;

import android.view.View;

public interface Item<Model> {

    void bindView(View view, IAdapter<Model> iAdapter);

    void setData(Model model, int i);

    int getLayoutResId();
}
