package com.sky.medialib.ui.kit.common.base.recycler;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.sky.medialib.R;
import com.sky.medialib.util.UIHelper;

public class ErrorView extends RelativeLayout implements OnClickListener {

    private boolean f10019a = true;
    private int state = 0;
    private View mErrorView;
    private CharSequence message = "";
    private View contentView;
    private OnClickListener reTryListener;

    ImageView mImageView;
    LottieAnimationView mLoadingView;
    ImageView mRetryButton;
    TextView mTextView;

    public ErrorView(Context context) {
        super(context);
        init(context);
    }

    public ErrorView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public ErrorView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
    }

    private void init(Context context) {
        if (isInEditMode()) {
            inflate(context, R.layout.vw_error_layout, this);
        }else{
            this.mErrorView = UIHelper.inflateView(context, R.layout.vw_error_layout, this, false);
            addView(this.mErrorView);
            setVisibility(VISIBLE);
            super.setOnClickListener(this);
        }
        mLoadingView = findViewById(R.id.error_progress);
        mImageView = findViewById(R.id.error_image);
        mRetryButton = findViewById(R.id.error_retry);
        mTextView = findViewById(R.id.error_text);

        if(isInEditMode()){
            mImageView.setImageResource(R.drawable.icon_error);
            mTextView.setText(R.string.network_error);
        }

    }

    public ErrorView getErrorView() {
        this.mLoadingView.setAnimation("lottie_loading_logo_small.json");
        return this;
    }

    public void setContentView(View view) {
        this.contentView = view;
    }

    public ErrorView setMessage(int i) {
        this.mTextView.setText(i);
        this.mTextView.setVisibility(VISIBLE);
        return this;
    }

    public ErrorView setMessage(CharSequence charSequence) {
        this.mTextView.setText(charSequence);
        this.mTextView.setVisibility(VISIBLE);
        return this;
    }

    public ErrorView setTips(int i) {
        this.message = getContext().getString(i);
        return this;
    }

    public int getState() {
        return this.state;
    }

    public ErrorView updateStatus(int i) {
        switch (i) {
            case 1:
                setContentVisible(GONE);
                this.state = 1;
                this.f10019a = true;
                showError();
                setVisibility(VISIBLE);
                break;
            case 2:
                setContentVisible(GONE);
                this.state = 2;
                this.f10019a = false;
                showLoading();
                setVisibility(VISIBLE);
                break;
            case 3:
                setContentVisible(GONE);
                this.state = 3;
                this.f10019a = true;
                showEmpty();
                setVisibility(VISIBLE);
                break;
            default:
                this.state = 0;
                setVisibility(GONE);
                setContentVisible(VISIBLE);
                break;
        }
        return this;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.reTryListener = onClickListener;
    }

    public void onClick(View view) {
        if (this.f10019a && this.reTryListener != null) {
            if (this.state == 3 || this.state == 1) {
                this.reTryListener.onClick(view);
            }
        }
    }

    public void setVisibility(int i) {
        if (i == 8) {
            this.state = 0;
        }
        super.setVisibility(i);
    }

    public void setContentVisible(int i) {
        if (this.contentView != null) {
            this.contentView.setVisibility(i);
        }
    }



    private void showLoading() {
        this.mLoadingView.setProgress(1.0f);
        this.mLoadingView.playAnimation();
        this.mLoadingView.setVisibility(VISIBLE);
        this.mTextView.setVisibility(GONE);
        this.mImageView.setVisibility(GONE);
        this.mErrorView.setVisibility(VISIBLE);
        this.mRetryButton.setVisibility(GONE);
    }

    private void showEmpty() {



        this.mImageView.setImageResource(R.drawable.icon_empty);
        if (TextUtils.isEmpty(this.message)) {
            this.mTextView.setVisibility(VISIBLE);
            this.mTextView.setText(R.string.empty_text);
        } else {
            this.mTextView.setText(this.message);
            this.mTextView.setVisibility(VISIBLE);
        }
        this.mLoadingView.pauseAnimation();
        this.mLoadingView.setVisibility(GONE);
        this.mImageView.setVisibility(VISIBLE);
        this.mErrorView.setVisibility(VISIBLE);
        this.mRetryButton.setVisibility(GONE);
    }

    private void showError() {
        this.mImageView.setImageResource(R.drawable.icon_error);
        this.mTextView.setVisibility(VISIBLE);
        this.mTextView.setText(R.string.load_failed_retry);
        this.mLoadingView.pauseAnimation();
        this.mLoadingView.setVisibility(GONE);
        this.mImageView.setVisibility(VISIBLE);
        this.mErrorView.setVisibility(VISIBLE);
        this.mRetryButton.setVisibility(GONE);
    }
}
