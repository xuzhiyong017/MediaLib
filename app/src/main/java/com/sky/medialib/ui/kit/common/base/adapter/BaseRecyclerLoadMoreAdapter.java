package com.sky.medialib.ui.kit.common.base.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.sky.medialib.R;
import com.sky.medialib.util.UIHelper;


public abstract class BaseRecyclerLoadMoreAdapter<Model> extends HeaderFooterRecycleAdapter<Model> {
    private static final int TYPE_LOAD_MORE = -300000;
    private boolean mAutoLoadEnable = true;
    private boolean mIsLoadingMore = false;
    private boolean mLoadMoreEnable = false;
    private LoadViewHolder mLoadViewHolder;
    private OnLoadMoreListener mOnLoadMoreListener;
    protected RecyclerView mRecycleView;
    private boolean mSupportLoadMore = true;

    private class ScrollListener extends RecyclerView.OnScrollListener {
        private ScrollListener() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
            int i2 = 0;
            if (BaseRecyclerLoadMoreAdapter.this.mAutoLoadEnable && BaseRecyclerLoadMoreAdapter.this.mLoadMoreEnable && !BaseRecyclerLoadMoreAdapter.this.mIsLoadingMore && i == 0) {
                int findFirstVisibleItemPosition;
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int childCount = recyclerView.getChildCount();
                int itemCount = layoutManager.getItemCount();
                if (layoutManager instanceof LinearLayoutManager) {
                    findFirstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                    findFirstVisibleItemPosition = layoutManager.getChildCount() > 0 ? ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(null)[0] : 0;
                } else {
                    throw new IllegalStateException("LayoutManager needs to subclass LinearLayoutManager or StaggeredGridLayoutManager");
                }
                if (findFirstVisibleItemPosition + childCount >= itemCount) {
                    i2 = 1;
                }
                if (i2 != 0) {
                    BaseRecyclerLoadMoreAdapter.this.setLoading(true);
                    BaseRecyclerLoadMoreAdapter.this.mOnLoadMoreListener.onLoadMore();
                }
            }
        }
    }

    public BaseRecyclerLoadMoreAdapter(RecyclerView recyclerView) {
        super(recyclerView.getContext());
        this.mRecycleView = recyclerView;
        this.mRecycleView.addOnScrollListener(new ScrollListener());
    }

    public BaseRecyclerLoadMoreAdapter(RecyclerView recyclerView, boolean z) {
        super(recyclerView.getContext());
        this.mRecycleView = recyclerView;
        this.mSupportLoadMore = z;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.mOnLoadMoreListener = onLoadMoreListener;
    }

    public void setAutoLoadEnable(boolean z) {
        this.mAutoLoadEnable = z;
    }

    public void setLoadMoreEnable(boolean z) {
        this.mLoadMoreEnable = z;
        int itemCount = getItemCount() - 1;
        if (-300000 == getItemViewType(itemCount)) {
            notifyItemChanged(itemCount);
        }
    }

    public void setLoadMoreVisibility(int i) {
        if (this.mLoadViewHolder == null) {
            return;
        }
        if (i == 0) {
            this.mLoadViewHolder.show();
        } else {
            this.mLoadViewHolder.hide();
        }
    }

    public void setLoadMoreComplete() {
        setLoading(false);
    }

    private void setLoading(boolean z) {
        if (this.mLoadMoreEnable && this.mLoadViewHolder != null && this.mIsLoadingMore != z) {
            this.mIsLoadingMore = z;
            if (this.mIsLoadingMore) {
                this.mLoadViewHolder.startLoadMore();
            } else {
                this.mLoadViewHolder.stopLoadMore();
            }
        }
    }

    public int getItemCount() {
        if (this.mSupportLoadMore) {
            return super.getItemCount() + 1;
        }
        return super.getItemCount();
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (!mSupportLoadMore || i != -300000) {
            return super.onCreateViewHolder(viewGroup, i);
        }
        if (mLoadViewHolder == null) {
            mLoadViewHolder = new LoadViewHolder(UIHelper.inflateView(mContext, R.layout.vw_load_more));
            mLoadViewHolder.mLoadLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mIsLoadingMore && mLoadMoreEnable && mOnLoadMoreListener != null) {
                        setLoading(true);
                        mOnLoadMoreListener.onLoadMore();
                    }
                }
            });
        }
        return mLoadViewHolder;
    }


    private boolean isLoadMoreItem(int i) {
        return mSupportLoadMore && i == getHeaderCount() + getDataSize();
    }

    public boolean isFooter(int i) {
        return isLoadMoreItem(i) || super.isFooter(i);
    }

    public int getItemViewType(int i) {
        if (isLoadMoreItem(i)) {
            return -300000;
        }
        return super.getItemViewType(i);
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        if (!isLoadMoreItem(i)) {
            super.onBindViewHolder(viewHolder, i);
        } else if (isEmpty()) {
            mLoadViewHolder.hide();
        } else if (findVisibleItemPositions()[0] == 0) {
            mLoadViewHolder.hide();
        } else if (mLoadMoreEnable) {
            mLoadViewHolder.showLoadMore();
        } else {
            mLoadViewHolder.showLoadEnd();
        }
    }

    public void onViewAttachedToWindow(ViewHolder viewHolder) {
        super.onViewAttachedToWindow(viewHolder);
        if (mRecycleView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            LayoutParams layoutParams = viewHolder.itemView.getLayoutParams();
            if (layoutParams != null && (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) && isLoadMoreItem(viewHolder.getLayoutPosition())) {
                ((StaggeredGridLayoutManager.LayoutParams) layoutParams).setFullSpan(true);
            }
        }
    }

    private int[] findVisibleItemPositions() {
        int[] iArr = new int[2];
        RecyclerView.LayoutManager layoutManager = mRecycleView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            iArr[0] = linearLayoutManager.findFirstVisibleItemPosition();
            iArr[1] = linearLayoutManager.findLastVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            if (layoutManager.getChildCount() > 0) {
                iArr[0] = staggeredGridLayoutManager.findFirstVisibleItemPositions(null)[0];
                iArr[1] = staggeredGridLayoutManager.findLastVisibleItemPositions(null)[0];
            } else {
                iArr[0] = 0;
                iArr[1] = 0;
            }
        } else {
            throw new IllegalStateException("LayoutManager needs to subclass LinearLayoutManager or StaggeredGridLayoutManager");
        }
        return iArr;
    }
}
