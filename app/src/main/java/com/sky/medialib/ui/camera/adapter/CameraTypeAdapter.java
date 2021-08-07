package com.sky.medialib.ui.camera.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sky.medialib.R;

import java.util.ArrayList;
import java.util.List;

public class CameraTypeAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> arrayList = new ArrayList();

    class ViewHolder {
        private TextView textView;

        public ViewHolder(View view) {
            this.textView = (TextView) view.findViewById(R.id.name);
        }
    }

    public CameraTypeAdapter(Context context) {
        this.mContext = context;
        this.arrayList.add("拍照");
        this.arrayList.add("视频");
    }

    public int getCount() {
        return this.arrayList.size();
    }

    public String getItem(int i) {
        return (String) this.arrayList.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(this.mContext).inflate(R.layout.vw_camera_type_item, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.textView.setText(getItem(i));
        return view;
    }
}
