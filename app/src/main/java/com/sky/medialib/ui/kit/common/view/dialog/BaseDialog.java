package com.sky.medialib.ui.kit.common.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ScreenUtils;
import com.sky.media.image.core.util.LogUtils;
import com.sky.medialib.R;
import com.sky.medialib.util.DialogUtil;
import com.sky.medialib.util.UIHelper;

public class BaseDialog extends Dialog {

    private boolean showLine = true;
    protected View mButtonBarDivider;
    protected View mButtonDivider;
    protected View mButtonLayout;
    protected FrameLayout mContainer;
    protected TextView mLeftButton;
    protected TextView mRightButton;
    protected View mTitleDivider;
    protected TextView mTitleView;

    public BaseDialog(Context context, int i) {
        super(context, i);
        View inflate = getLayoutInflater().inflate(R.layout.dialog_base, null);
        super.setContentView(inflate);
        mTitleView = findViewById(R.id.dialog_title);
        mTitleDivider = findViewById(R.id.dialog_title_divider);
        mRightButton = findViewById(R.id.dialog_right_button);
        mLeftButton = findViewById(R.id.dialog_left_button);
        mLeftButton = findViewById(R.id.dialog_left_button);
        mContainer = findViewById(R.id.dialog_container);
        mButtonLayout = findViewById(R.id.dialog_button_layout);
        mButtonDivider = findViewById(R.id.dialog_button_divider);
        mButtonBarDivider = findViewById(R.id.dialog_button_bar_divider);
    }

    public void setContentView(int i) {
        addView(UIHelper.inflateView(getContext(), i, this.mContainer, false));
    }

    public void setContentView(View view) {
        addView(view);
    }

    public void setContentView(View view, LayoutParams layoutParams) {
        addView(view, layoutParams);
    }

    private void addView(View view) {
        addView(view, new FrameLayout.LayoutParams(-1, -2));
    }

    private void addView(View view, LayoutParams layoutParams) {
        this.mContainer.removeAllViews();
        this.mContainer.addView(view, layoutParams);
    }

    public void setTitle(int i) {
        setTitle(getContext().getResources().getString(i));
    }

    public void setTitle(CharSequence charSequence) {
        this.mTitleView.setText(charSequence);
    }

    public void show() {
        Context context = getContext();
        try {
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                if (activity.isFinishing() || activity.isDestroyed()) {
                    return;
                }
            }
        } catch (Throwable th) {
            LogUtils.loge("baseDialog",th.getMessage());
        }
        initTitle();
        changUIStatus();
        super.show();
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.width = (int) (((float) ScreenUtils.getScreenWidth()) * 0.75f);
            window.setAttributes(attributes);
        }
    }

    public void dismiss() {
        try {
            super.dismiss();
        } catch (Throwable e) {
            LogUtils.loge("baseDialog",e.getMessage());
        }
    }

    public boolean showDialog() {
        return DialogUtil.showDialog(this);
    }

    private void initTitle() {
        if (TextUtils.isEmpty(this.mTitleView.getText())) {
            this.mTitleView.setVisibility(View.GONE);
            this.mTitleDivider.setVisibility(View.GONE);
            return;
        }
        this.mTitleView.setVisibility(View.VISIBLE);
        setTitleDividerShow(this.showLine);
    }

    private void changUIStatus() {
        int i;
        int i2 = 1;
        if (TextUtils.isEmpty(this.mLeftButton.getText())) {
            this.mLeftButton.setVisibility(View.GONE);
        } else {
            this.mLeftButton.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(this.mRightButton.getText())) {
            this.mRightButton.setVisibility(View.GONE);
        } else {
            this.mRightButton.setVisibility(View.VISIBLE);
        }
        if (this.mLeftButton.getVisibility() == View.VISIBLE) {
            i = 1;
        } else {
            i = 0;
        }
        if (this.mRightButton.getVisibility() != View.VISIBLE) {
            i2 = 0;
        }
        if (i == 0 && i2 == 0) {
            this.mButtonBarDivider.setVisibility(View.GONE);
            this.mButtonLayout.setVisibility(View.GONE);
        } else {
            this.mButtonBarDivider.setVisibility(View.VISIBLE);
            this.mButtonLayout.setVisibility(View.VISIBLE);
        }
        if (i == 0 || i2 == 0) {
            this.mButtonDivider.setVisibility(View.GONE);
        } else {
            this.mButtonDivider.setVisibility(View.VISIBLE);
        }
    }

    public void setLeftButton(CharSequence charSequence, final OnClickListener onClickListener) {
        this.mLeftButton.setText(charSequence);
        this.mLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onClickListener != null){
                    onClickListener.onClick(BaseDialog.this,0);
                }
            }
        });
    }

    public void setRightButton(CharSequence charSequence, final OnClickListener onClickListener) {
        this.mRightButton.setText(charSequence);
        this.mRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onClickListener != null){
                    onClickListener.onClick(BaseDialog.this,1);
                }
            }
        });
    }

    public void setTitleDividerShow(boolean show) {
        this.showLine = show;
        this.mTitleDivider.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
