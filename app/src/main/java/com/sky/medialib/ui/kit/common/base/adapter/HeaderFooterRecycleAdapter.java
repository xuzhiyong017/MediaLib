package com.sky.medialib.ui.kit.common.base.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import androidx.collection.SparseArrayCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.List;

public abstract class HeaderFooterRecycleAdapter<Model> extends BaseRecyclerAdapter<Model, ViewHolder> {
    private int BASE_TYPE_FOOTER = -200000;
    private int BASE_TYPE_HEADER = -100000;
    private SparseArrayCompat<View> mFootViews = new SparseArrayCompat();
    private SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat();

    public abstract void onBindHolder(ViewHolder viewHolder, int i);

    public abstract ViewHolder onCreateHolder(ViewGroup viewGroup, int i);

    public HeaderFooterRecycleAdapter(Context context) {
        super(context);
    }

    public boolean isHeader(int i) {
        return i < getHeaderCount();
    }

    public boolean isFooter(int i) {
        return i >= getItemCount() - getFooterCount();
    }

    public void addHeader(View view) {
        SparseArrayCompat sparseArrayCompat = this.mHeaderViews;
        int i = this.BASE_TYPE_HEADER - 1;
        this.BASE_TYPE_HEADER = i;
        sparseArrayCompat.setValueAt(i, view);
        notifyItemInserted(this.mHeaderViews.size() - 1);
    }

    public void addFooter(View view) {
        SparseArrayCompat sparseArrayCompat = this.mFootViews;
        int i = this.BASE_TYPE_FOOTER - 1;
        this.BASE_TYPE_FOOTER = i;
        sparseArrayCompat.setValueAt(i, view);
        notifyItemInserted(getItemCount() - 1);
    }

    public void removeHeader(View view) {
        int a = this.mHeaderViews.indexOfValue(view);
        if (a > -1) {
            this.mHeaderViews.remove(a);
            notifyItemRemoved(a);
        }
    }

    public void removeHeaders() {
        int b = this.mHeaderViews.size();
        if (b > 0) {
            this.mHeaderViews.clear();
            notifyItemRangeRemoved(0, b);
        }
    }

    public void removeFooter(View view) {
        int a = this.mFootViews.indexOfValue(view);
        if (a > -1) {
            int b = this.mFootViews.size();
            this.mFootViews.remove(a);
            notifyItemRemoved(a + ((getItemCount() - b) - 1));
        }
    }

    public void removeFooters() {
        int b = this.mFootViews.size();
        if (this.mFootViews.size() > 0) {
            this.mFootViews.clear();
            notifyItemRangeRemoved((getItemCount() - b) - 1, getItemCount() - 1);
        }
    }

    public boolean containsHeader(View view) {
        return this.mHeaderViews.indexOfValue(view) >= 0;
    }

    public boolean containsFooter(View view) {
        return this.mFootViews.indexOfValue(view) >= 0;
    }

    public int getHeaderCount() {
        return this.mHeaderViews.size();
    }

    public int getFooterCount() {
        return this.mFootViews.size();
    }

    public int getFirstDataPosition() {
        return getHeaderCount();
    }

    public int getItemCount() {
        return (getDataSize() + getHeaderCount()) + getFooterCount();
    }

    public void addList(List<? extends Model> list) {
        if (list != null) {
            this.mData.addAll(list);
            int size = list.size();
            notifyItemRangeInserted((getFirstDataPosition() + this.mData.size()) - size, size);
        }
    }

    public void addItem(Model model) {
        if (model != null) {
            this.mData.add(model);
            notifyItemInserted((getFirstDataPosition() + this.mData.size()) - 1);
        }
    }

    public void addItem(int i, Model model) {
        if (model != null && i >= 0 && i <= this.mData.size()) {
            this.mData.add(i, model);
            notifyItemInserted(getFirstDataPosition() + i);
        }
    }

    public void removeItem(Model model) {
        if (model != null) {
            int indexOf = this.mData.indexOf(model);
            if (indexOf > -1) {
                this.mData.remove(indexOf);
                notifyItemRemoved(indexOf + getFirstDataPosition());
            }
        }
    }

    public void updateItem(Model model) {
        if (model != null) {
            int indexOf = this.mData.indexOf(model);
            if (indexOf > -1) {
                this.mData.set(indexOf, model);
                notifyItemChanged(indexOf + getFirstDataPosition());
            }
        }
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = (View) this.mHeaderViews.get(i);
        if (view != null) {
            return new ViewHolder(view);
        }
        view = (View) this.mFootViews.get(i);
        if (view != null) {
            return new ViewHolder(view);
        }
        return onCreateHolder(viewGroup, i);
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        if (isHeader(i) || isFooter(i)) {
            LayoutParams layoutParams = viewHolder.itemView.getLayoutParams();
            if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
                ((StaggeredGridLayoutManager.LayoutParams) layoutParams).setFullSpan(true);
                return;
            }
            return;
        }
        onBindHolder(viewHolder, i - getFirstDataPosition());
    }

    public int getItemViewType(int i) {
        if (isHeader(i)) {
            return this.mHeaderViews.indexOfKey(i);
        }
        if (isFooter(i)) {
            return this.mFootViews.indexOfKey((i - getItemCount()) + getFooterCount());
        }
        return getItemType(i - getFirstDataPosition());
    }

    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int i) {
                    return (HeaderFooterRecycleAdapter.this.isHeader(i) || HeaderFooterRecycleAdapter.this.isFooter(i)) ? gridLayoutManager.getSpanCount() : 1;
                }
            });
        }
    }

    public int getItemType(int i) {
        return -1;
    }
}
