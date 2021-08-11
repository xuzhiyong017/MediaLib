package com.sky.medialib.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.sky.medialib.R;
import com.sky.medialib.util.PixelUtil;
import com.sky.medialib.util.UIHelper;

public class LoadingDialog extends Dialog {
    private View mContentView;
    private LottieAnimationView animationView;
    private TextView textView;

    public LoadingDialog(Context context) {
        this(context, R.style.LoadingDialog);
    }

    public LoadingDialog(Context context, int i) {
        super(context, i);
        this.mContentView = UIHelper.inflateView(context, R.layout.dialog_loading);
        this.animationView = (LottieAnimationView) this.mContentView.findViewById(R.id.loading);
        this.textView = (TextView) this.mContentView.findViewById(R.id.loading_tip);
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(this.mContentView);
        setCanceledOnTouchOutside(false);
    }

    public void setMessage(String str) {
        this.textView.setText(str);
        this.textView.setVisibility(0);
    }

    public void show() {
        super.show();
        Window window = getWindow();
        if (window != null) {
            LayoutParams attributes = window.getAttributes();
            attributes.width = PixelUtil.dip2px(120.0f);
            attributes.height = attributes.width;
            attributes.flags = 2;
            window.setAttributes(attributes);
        }
        this.animationView.playAnimation();
    }

    public void dismiss() {
        this.animationView.pauseAnimation();
        super.dismiss();
    }
}
