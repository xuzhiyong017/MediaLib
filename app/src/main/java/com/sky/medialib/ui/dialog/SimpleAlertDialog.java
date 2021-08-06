package com.sky.medialib.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sky.media.kit.base.BaseListAdapter;
import com.sky.medialib.R;
import com.sky.medialib.ui.kit.common.view.dialog.BaseDialog;
import com.sky.medialib.util.UIHelper;

import java.util.List;

public class SimpleAlertDialog extends BaseDialog {

    public static class Builder {

        private Context mContext;
        private int styleRes;
        private SimpleAlertDialog mDialog;
        private View mContentView;
        private int padding;
        private CharSequence mTitle;
        private CharSequence leftTitle;
        private CharSequence rightTitle;
        private DialogInterface.OnClickListener mLeftClickListener;
        private DialogInterface.OnClickListener mRightClickListener;
        private boolean cancelable;
        private boolean touchOutside;
        private BaseAdapter adapter;
        private List<CharSequence> listString;
        private OnItemClickListener onItemClickListener;
        private int pos;
        private CharSequence message;
        private int gravity;
        private boolean titleDividerShow;

        public Builder(Context context) {
            this(context, R.style.DialogStyle);
        }

        public Builder(Context context, int styleRes) {
            this.cancelable = true;
            this.touchOutside = false;
            this.pos = -1;
            this.gravity = 51;
            this.titleDividerShow = true;
            this.styleRes = styleRes;
            this.mContext = context;
        }

        public Builder setTitle(int i) {
            return setTitle(this.mContext.getString(i));
        }

        public Builder setTitle(CharSequence charSequence) {
            this.mTitle = charSequence;
            return this;
        }

        public Builder setContentView(View view) {
            return setContentView(view, 0);
        }

        public Builder setContentView(View view, int i) {
            this.mContentView = view;
            this.padding = i;
            return this;
        }

        public Builder setMessage(int i, int i2) {
            return setMessage(this.mContext.getString(i), i2);
        }

        public Builder setMessage(CharSequence charSequence, int i) {
            this.message = charSequence;
            this.gravity = i;
            return this;
        }

        public Builder setLeftBtn(int i) {
            return setLeftBtn(i, null);
        }

        public Builder setLeftBtn(int i, DialogInterface.OnClickListener onClickListener) {
            return setLeftBtn(this.mContext.getString(i), onClickListener);
        }

        public Builder setLeftBtn(CharSequence charSequence, DialogInterface.OnClickListener onClickListener) {
            this.leftTitle = charSequence;
            this.mLeftClickListener = onClickListener;
            return this;
        }

        public Builder setRightBtn(int i, DialogInterface.OnClickListener onClickListener) {
            return setRightBtn(this.mContext.getString(i), onClickListener);
        }

        public Builder setRightBtn(CharSequence charSequence, DialogInterface.OnClickListener onClickListener) {
            this.rightTitle = charSequence;
            this.mRightClickListener = onClickListener;
            return this;
        }

        public Builder setCancleable(boolean z) {
            this.cancelable = z;
            return this;
        }

        private void addContentView(ViewGroup viewGroup, View view, int i) {
            viewGroup.removeAllViews();
            viewGroup.setPadding(i, i, i, i);
            viewGroup.addView(view, new LayoutParams(-1, -2));
        }

        private void initListView(ViewGroup viewGroup) {
            if (this.adapter != null || (this.listString != null && !this.listString.isEmpty())) {
                ListView b = getListVIew();
                b.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mDialog.dismiss();
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick(parent, view, position, id);
                        }
                    }
                });
                if (this.adapter != null) {
                    b.setAdapter(this.adapter);
                } else if (!(this.listString == null || this.listString.isEmpty())) {
                    b.setAdapter(new ListAdapter(this.mContext, this.listString, this.pos));
                }
                addContentView(viewGroup, b, 0);
            }
        }


        private ListView getListVIew() {
            ListView listView = (ListView) UIHelper.inflateView(this.mContext, R.layout.dialog_list);
            listView.setLayoutParams(new LayoutParams(-1, -2));
            return listView;
        }

        private void initScrolleView(ViewGroup viewGroup) {
            ScrollView scrollView = (ScrollView) UIHelper.inflateView(this.mContext, R.layout.dialog_message);
            scrollView.setLayoutParams(new LayoutParams(-1, -2));
            TextView textView = (TextView) scrollView.findViewById(R.id.dialog_text);
            textView.setText(this.message);
            textView.setGravity(this.gravity);
            addContentView(viewGroup, scrollView, 0);
        }

        private void initView(ViewGroup viewGroup) {
            if (this.mContentView != null) {
                addContentView(viewGroup, this.mContentView, this.padding);
            } else if (!TextUtils.isEmpty(this.message)) {
                initScrolleView(viewGroup);
            } else if (this.adapter != null || (this.listString != null && !this.listString.isEmpty())) {
                initListView(viewGroup);
            }
        }

        public SimpleAlertDialog build() {
            this.mDialog = new SimpleAlertDialog(this.mContext, this.styleRes);
            this.mDialog.setTitle(this.mTitle);
            initView(this.mDialog.mContainer);
            this.mDialog.setLeftButton(this.leftTitle, this.mLeftClickListener);
            this.mDialog.setRightButton(this.rightTitle, this.mRightClickListener);
            this.mDialog.setCancelable(this.cancelable);
            this.mDialog.setCanceledOnTouchOutside(this.touchOutside);
            this.mDialog.setTitleDividerShow(this.titleDividerShow);
            return this.mDialog;
        }
    }

    static class ListAdapter extends BaseListAdapter<CharSequence> {

        private int selectPos = -1;

        protected ListAdapter(Context context, List<CharSequence> list, int i) {
            super(context);
            replaceList(list);
            this.selectPos = i;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            View a;
            boolean z;
            if (view == null || !(view instanceof TextView)) {
                a = UIHelper.inflateView(this.mContext, R.layout.dailog_list_item, viewGroup, false);
            } else {
                a = view;
            }
            TextView textView = (TextView) a;
            textView.setText((CharSequence) getItem(i));
            if (this.selectPos != i) {
                z = true;
            } else {
                z = false;
            }
            textView.setEnabled(z);
            return a;
        }
    }

    public static Builder newBuilder(Context context) {
        return new Builder(context);
    }

    public SimpleAlertDialog(Context context, int i) {
        super(context, i);
    }
}
