package com.sky.medialib.ui.kit.common.base.recycler;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sky.medialib.R;

public class RecyclerViewClickSupport {

    private final RecyclerView mRecycleView;
    private OnItemClickListener mOnItemClickListener;
    private onItemLongClickListener mOnItemLongClickListener;
    private OnClickListener mClickListener = new OnClickListenerEx();
    private OnLongClickListener onLongClickListener = new OnLongClickListenerEx();

    private RecyclerView.OnChildAttachStateChangeListener mOnChildAttachStateListener = new RecyclerView.OnChildAttachStateChangeListener(){
        @Override
        public void onChildViewAttachedToWindow(@NonNull View view) {
            if (mOnItemClickListener != null) {
                view.setOnClickListener(mClickListener);
            }
            if (mOnItemLongClickListener != null) {
                view.setOnLongClickListener(onLongClickListener);
            }
        }

        @Override
        public void onChildViewDetachedFromWindow(@NonNull View view) {

        }
    };

    public interface OnItemClickListener {
        void onItemClick(RecyclerView recyclerView, int i, View view);
    }

    class OnClickListenerEx implements OnClickListener {
        OnClickListenerEx() {
        }

        public void onClick(View view) {
            if (RecyclerViewClickSupport.this.mOnItemClickListener != null) {
                RecyclerViewClickSupport.this.mOnItemClickListener.onItemClick(RecyclerViewClickSupport.this.mRecycleView, RecyclerViewClickSupport.this.mRecycleView.getChildViewHolder(view).getAdapterPosition(), view);
            }
        }
    }

    class OnLongClickListenerEx implements OnLongClickListener {
        OnLongClickListenerEx() {
        }

        public boolean onLongClick(View view) {
            if (RecyclerViewClickSupport.this.mOnItemLongClickListener == null) {
                return false;
            }
            return RecyclerViewClickSupport.this.mOnItemLongClickListener.onItemLongClick(RecyclerViewClickSupport.this.mRecycleView, RecyclerViewClickSupport.this.mRecycleView.getChildViewHolder(view).getAdapterPosition(), view);
        }
    }

    public interface onItemLongClickListener {
        boolean onItemLongClick(RecyclerView recyclerView, int i, View view);
    }

    private RecyclerViewClickSupport(RecyclerView recyclerView) {
        this.mRecycleView = recyclerView;
        this.mRecycleView.setTag(R.id.item_click_support, this);
        this.mRecycleView.addOnChildAttachStateChangeListener(this.mOnChildAttachStateListener);
    }

    public static RecyclerViewClickSupport getRecycleViewClickSupport(RecyclerView recyclerView) {
        RecyclerViewClickSupport recyclerViewClickSupport = (RecyclerViewClickSupport) recyclerView.getTag(R.id.item_click_support);
        if (recyclerViewClickSupport == null) {
            return new RecyclerViewClickSupport(recyclerView);
        }
        return recyclerViewClickSupport;
    }

    public RecyclerViewClickSupport setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
        return this;
    }

    public RecyclerViewClickSupport setOnItemLongClickListener(onItemLongClickListener onItemLongClickListener) {
        this.mOnItemLongClickListener = onItemLongClickListener;
        return this;
    }
}
