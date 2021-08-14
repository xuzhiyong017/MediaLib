package com.sky.medialib.ui.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ScreenUtils;
import com.bumptech.glide.Glide;
import com.sky.media.image.core.util.BitmapUtil;
import com.sky.medialib.R;
import com.sky.medialib.ui.kit.model.GalleryFolder;

import java.util.ArrayList;
import java.util.List;

public class GalleryFolderAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;

    private ImageSize imageSize;
    private int itemWidth;
    private List<GalleryFolder> mData = new ArrayList();

    public class ViewHolder {
        RelativeLayout relativeLayout;
        ImageView squareImageView;
        TextView textName;
        TextView count;

        public ViewHolder(View view) {
            this.relativeLayout = (RelativeLayout) view.findViewById(R.id.folder_layout);
            this.squareImageView = (ImageView) view.findViewById(R.id.cover);
            this.textName = (TextView) view.findViewById(R.id.name);
            this.count = (TextView) view.findViewById(R.id.count);
            LayoutParams layoutParams = this.squareImageView.getLayoutParams();
            layoutParams.height = GalleryFolderAdapter.this.itemWidth;
            this.squareImageView.setLayoutParams(layoutParams);
        }
    }

    public GalleryFolderAdapter(Context context) {
        this.layoutInflater = LayoutInflater.from(context);
        this.itemWidth = (ScreenUtils.getScreenWidth() - 60) / 3;
        int i = (int) (((double) this.itemWidth) / 1.3d);
        this.imageSize = new ImageSize(i, i);
    }

    public void setFolderList(List<GalleryFolder> list) {
        this.mData.clear();
        this.mData.addAll(list);
        notifyDataSetChanged();
    }

    public void setFolder(GalleryFolder galleryFolder) {
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.mData.size();
    }

    public GalleryFolder getItem(int i) {
        return (GalleryFolder) this.mData.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = this.layoutInflater.inflate(R.layout.vw_image_folder_item, viewGroup, false);
            ViewHolder viewHolder2 = new ViewHolder(view);
            view.setTag(viewHolder2);
            viewHolder = viewHolder2;
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        GalleryFolder galleryFolder = (GalleryFolder) this.mData.get(i);
        String wrap = BitmapUtil.Scheme.FILE.wrap(galleryFolder.getFilteredFirstImageUrl());
        Glide.with(viewHolder.squareImageView).load(wrap).placeholder(R.drawable.icon_default).override(imageSize.getWidth(),imageSize.getHeight()).into(viewHolder.squareImageView);
        viewHolder.textName.setText(galleryFolder.getName());
        viewHolder.count.setText(String.valueOf(galleryFolder.getFilterdCount()));
        return view;
    }
}
