package com.sky.medialib.ui.kit.common.base.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecyclerAdapter<Model, Holder extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<Holder> {
    protected Context mContext;
    protected List<Model> mData = createList();

    public BaseRecyclerAdapter(Context context) {
        this.mContext = context;
    }

    public void setList(List<? extends Model> list) {
        if (list != null) {
            if (this.mData.size() > 0) {
                this.mData.clear();
                notifyDataSetChanged();
            }
            this.mData.addAll(list);
            notifyDataSetChanged();
        }
    }

    public List<Model> getList() {
        return this.mData;
    }

    public void addList(List<? extends Model> list) {
        if (list != null) {
            int size = this.mData.size();
            this.mData.addAll(list);
            notifyItemRangeInserted(size, list.size());
        }
    }

    public void addItem(Model model) {
        if (model != null) {
            this.mData.add(model);
            notifyItemInserted(this.mData.size() - 1);
        }
    }

    public void addItem(int i, Model model) {
        if (model != null && i >= 0 && i <= this.mData.size()) {
            this.mData.add(i, model);
            notifyItemInserted(i);
        }
    }

    public void removeItem(Model model) {
        if (model != null) {
            int indexOf = this.mData.indexOf(model);
            if (indexOf > -1) {
                this.mData.remove(indexOf);
                notifyItemRemoved(indexOf);
            }
        }
    }

    public void removeItem(int i) {
        if (i > -1) {
            this.mData.remove(i);
            notifyDataSetChanged();
        }
    }

    public int getDataSize() {
        return this.mData.size();
    }

    public void clear() {
        this.mData.clear();
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return this.mData == null || this.mData.isEmpty();
    }

    public Model getItem(int i) {
        if (i < 0 || i >= this.mData.size()) {
            return null;
        }
        return this.mData.get(i);
    }

    public void removeList(List<Model> list) {
        if (list != null && !list.isEmpty() && this.mData != null && this.mData.removeAll(list)) {
            notifyDataSetChanged();
        }
    }

    public long getItemId(int i) {
        if (this.mData == null || i == this.mData.size()) {
            return 0;
        }
        return (long) i;
    }

    public int getItemCount() {
        return getDataSize();
    }

    protected List<Model> createList() {
        return new ArrayList();
    }
}
