package com.sky.medialib.ui.kit.common.base.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.sky.medialib.R;
import com.sky.medialib.util.UIHelper;

public abstract class BaseRecyclerCommonAdapter<Model> extends BaseRecyclerLoadMoreAdapter<Model> implements IAdapter<Model> {
    private Object mType;
    private TypeUtil mTypeUtil = new TypeUtil();

    public BaseRecyclerCommonAdapter(RecyclerView recyclerView) {
        super(recyclerView);
    }

    public BaseRecyclerCommonAdapter(RecyclerView recyclerView, boolean z) {
        super(recyclerView, z);
    }

    public final int getItemType(int i) {
        this.mType = getItemType(getItem(i));
        return this.mTypeUtil.getTypeId(this.mType);
    }

    public Object getItemType(Model model) {
        return Integer.valueOf(-1);
    }

    public Model getConvertedData(Model model, Object obj) {
        return model;
    }

    public ViewHolder onCreateHolder(ViewGroup viewGroup, int i) {
        Item createItem = createItem(this.mType);
        View a = UIHelper.inflateView(this.mContext, createItem.getLayoutResId(), viewGroup, false);
        a.setTag(R.id.tag_item, createItem);
        createItem.bindView(a, (IAdapter) this);
        return new ViewHolder(a);
    }

    public void onBindHolder(ViewHolder viewHolder, int i) {
        Item item = (Item) viewHolder.itemView.getTag(R.id.tag_item);
        if (item != null) {
            item.setData(getConvertedData(getItem(i), this.mType), i);
        }
    }
}
