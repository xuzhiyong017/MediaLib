package com.sky.medialib.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;

import com.blankj.utilcode.util.ScreenUtils;
import com.sky.medialib.R;


public abstract class BaseBottomDialog extends Dialog {

    protected int animatorDuration = 200;
    protected boolean isAnimationStart = false;
    protected View mContentView;

    class AnimatorListenerEx implements AnimationListener {
        AnimatorListenerEx() {
        }

        public void onAnimationStart(Animation animation) {
            BaseBottomDialog.this.isAnimationStart = true;
        }

        public void onAnimationEnd(Animation animation) {
            BaseBottomDialog.this.isAnimationStart = false;
            BaseBottomDialog.this.mContentView.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        dismiss();
                    } catch (Exception e) {
                    }
                }
            });
        }
        public void onAnimationRepeat(Animation animation) {
        }
    }

    protected abstract View createView(Context context);

    public BaseBottomDialog(Context context) {
        super(context, R.style.Dialog_BottomSheet_NoAnimation);
        this.mContentView = createView(context);
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(this.mContentView);
        setCanceledOnTouchOutside(true);
    }

    public void show() {
        if (!(getContext() instanceof Activity) || !((Activity) getContext()).isFinishing()) {
            super.show();
            Window window = getWindow();
            if (window != null) {
                LayoutParams attributes = window.getAttributes();
                attributes.width = ScreenUtils.getScreenWidth();
                attributes.height = LayoutParams.WRAP_CONTENT;
                attributes.gravity = Gravity.BOTTOM |Gravity.CENTER;
                window.setAttributes(attributes);
            }
            startAnimation();
        }
    }

    public void dimissDialog() {
        if (!this.isAnimationStart) {
            hideAnimation();
        }
    }
    private void startAnimation() {
        if (this.mContentView != null) {
            Animation translateAnimation = new TranslateAnimation(1, 0.0f, 1, 0.0f, 1, 1.0f, 1, 0.0f);
            Animation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
            AnimationSet animationSet = new AnimationSet(true);
            animationSet.addAnimation(translateAnimation);
            animationSet.addAnimation(alphaAnimation);
            animationSet.setInterpolator(new DecelerateInterpolator());
            animationSet.setDuration((long) this.animatorDuration);
            animationSet.setFillAfter(true);
            this.mContentView.startAnimation(animationSet);
        }
    }

    private void hideAnimation() {
        if (this.mContentView != null) {
            Animation translateAnimation = new TranslateAnimation(1, 0.0f, 1, 0.0f, 1, 0.0f, 1, 1.0f);
            Animation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
            AnimationSet animationSet = new AnimationSet(true);
            animationSet.addAnimation(translateAnimation);
            animationSet.addAnimation(alphaAnimation);
            animationSet.setInterpolator(new DecelerateInterpolator());
            animationSet.setDuration((long) this.animatorDuration);
            animationSet.setFillAfter(true);
            animationSet.setAnimationListener(new AnimatorListenerEx());
            this.mContentView.startAnimation(animationSet);
        }
    }
}
