package com.sky.medialib.ui.camera.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.sky.media.kit.model.FilterExt;
import com.sky.medialib.R;
import com.sky.medialib.ui.kit.manager.ToolFilterManager;

import java.util.List;


public class CameraFiltersAdapter extends RecyclerView.Adapter<CameraFiltersAdapter.FilterViewHolder> {
    private List<FilterExt> mFilters = ToolFilterManager.INSTANCE.getCacheFilterList();
    private float mItemWidth;
    private IRecycleViewItemClickListener mListener;
    private int mSelectPosition;

    class FilterViewHolder extends RecyclerView.ViewHolder {
        public ImageView mFilterIconView;
        public RelativeLayout mFilterLayout;
        public ImageView mFilterMaskView;
        public TextView mFilterNameView;
        public ImageView mFilterNewView;

        public FilterViewHolder(View view) {
            super(view);
            this.mFilterLayout = (RelativeLayout) view.findViewById(R.id.filter_item_layout);
            this.mFilterIconView = (ImageView) view.findViewById(R.id.filter_icon);
            this.mFilterNameView = (TextView) view.findViewById(R.id.filter_name);
            this.mFilterMaskView = (ImageView) view.findViewById(R.id.filter_mask);
            this.mFilterNewView = (ImageView) view.findViewById(R.id.filter_new);
        }
    }

    public CameraFiltersAdapter(IRecycleViewItemClickListener iRecycleViewItemClickListener, float f) {
        this.mListener = iRecycleViewItemClickListener;
        this.mItemWidth = f;
    }

    public void setSelectPosition(int i) {
        this.mSelectPosition = i;
        notifyDataSetChanged();
    }

    public FilterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new FilterViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.vw_camera_filter_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(final FilterViewHolder filterViewHolder, final int i) {
        FilterExt filterExt = (FilterExt) this.mFilters.get(i);
        filterViewHolder.mFilterNewView.setImageResource(0);
        filterViewHolder.mFilterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectPosition(i);
                mListener.onItemClick(filterViewHolder, i, null);
            }
        });
        filterViewHolder.mFilterLayout.setLayoutParams(new RecyclerView.LayoutParams(Math.round(this.mItemWidth), -2));
        Glide.with(filterViewHolder.itemView).load(filterExt.getIconResource())
                .placeholder(R.drawable.defaultpics_filter_200)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(filterViewHolder.mFilterIconView);
        filterViewHolder.mFilterNameView.setText(filterExt.getName());
        if (this.mSelectPosition == i) {
            filterViewHolder.mFilterLayout.setSelected(true);
            filterViewHolder.mFilterMaskView.setVisibility(View.VISIBLE);
            return;
        }
        filterViewHolder.mFilterLayout.setSelected(false);
        filterViewHolder.mFilterMaskView.setVisibility(8);
    }

    public int getItemCount() {
        return this.mFilters.size();
    }
}
