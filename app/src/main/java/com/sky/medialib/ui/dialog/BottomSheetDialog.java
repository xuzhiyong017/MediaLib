package com.sky.medialib.ui.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;


import com.sky.media.kit.base.BaseListAdapter;
import com.sky.medialib.R;
import com.sky.medialib.util.UIHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BottomSheetDialog extends BaseBottomDialog {

    private OnItemClickListener mOnItemClickListener;

    private MyAdapter mAdapter;

    class MyAdapter extends BaseListAdapter<ItemBean> {

        private class ViewHolder {
            TextView mTextView;

            private ViewHolder(View view) {
                this.mTextView = (TextView) view.findViewById(R.id.text);
                view.setTag(this);
            }
        }

        MyAdapter(Context context) {
            super(context);
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null || view.getTag() == null) {
                view = UIHelper.inflateView(this.mContext, R.layout.dialog_bottom_sheet_item, viewGroup, false);
                viewHolder = new ViewHolder(view);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            ItemBean itemBean = (ItemBean) getItem(i);
            viewHolder.mTextView.setText(itemBean.showName);
            viewHolder.mTextView.setTextColor(itemBean.textColorId);
            return view;
        }
    }

    public static class ItemBean implements Serializable {
        public String showName;
        public int textColorId = -1;

        public ItemBean(String str) {
            this.showName = str;
        }
    }

    public BottomSheetDialog(Context context) {
        super(context);
    }


    public View createView(Context context) {
        View a = UIHelper.inflateView(context, R.layout.dialog_bottom_sheet);
        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        a.findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dimissDialog();
            }
        });
        this.mAdapter = new MyAdapter(getContext());
        ListView listView = (ListView) a.findViewById(R.id.dialog_list);
        listView.setAdapter(this.mAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                dismiss();
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(adapterView, view, i, j);
                }
            }
        });
        return a;
    }


    public BottomSheetDialog replaceList(List<ItemBean> list) {
        this.mAdapter.replaceList(list);
        return this;
    }

    public BottomSheetDialog addList(String... strArr) {
        List arrayList = new ArrayList();
        for (String str : strArr) {
            ItemBean itemBean = new ItemBean(str);
            arrayList.add(itemBean);
        }
        this.mAdapter.replaceList(arrayList);
        return this;
    }

    public BottomSheetDialog createItem(String str, int i) {
        List arrayList = new ArrayList();
        ItemBean itemBean = new ItemBean(str);
        itemBean.textColorId = i;
        arrayList.add(itemBean);
        this.mAdapter.replaceList(arrayList);
        return this;
    }

    public BottomSheetDialog setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
        return this;
    }
}
