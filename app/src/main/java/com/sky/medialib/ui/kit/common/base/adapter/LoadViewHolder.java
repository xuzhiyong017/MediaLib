package com.sky.medialib.ui.kit.common.base.adapter;

import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Transformation;
import android.widget.ImageView;

import com.sky.medialib.R;


public class LoadViewHolder extends ViewHolder {
    private Animation mAnimation;
    private AnimationDrawable mAnimationDrawable;
    View mEndLayout;
    View mLayout;
    View mLoadLayout;
    ImageView mLoadProgress;

    public LoadViewHolder(View view) {
        super(view);
        mEndLayout = view.findViewById(R.id.load_end_layout);
        mLayout = view.findViewById(R.id.load_layout);
        mLoadLayout = view.findViewById(R.id.load_more_layout);
        mLoadProgress = view.findViewById(R.id.load_more_progress);
        this.mLoadProgress.setImageResource(R.drawable.refresh_animation);
        mAnimationDrawable = (AnimationDrawable) mLoadProgress.getDrawable();
        this.mAnimationDrawable.setOneShot(false);
        createAnimation();
    }

    private void createAnimation() {
        this.mAnimation = new Animation() {
            @Override
            protected void applyTransformation(float f, Transformation t) {
                LoadViewHolder.this.mLoadProgress.setScaleX(f);
                LoadViewHolder.this.mLoadProgress.setScaleY(Math.min(1.0f, 1.5f * f));
                LoadViewHolder.this.mLoadProgress.setAlpha(f);
                LoadViewHolder.this.mLoadProgress.setY(((float) LoadViewHolder.this.mLayout.getHeight()) * (1.0f - f));
            }
        };
        this.mAnimation.setDuration(200);
        this.mAnimation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                LoadViewHolder.this.mLoadProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                LoadViewHolder.this.mAnimationDrawable.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void show() {
        this.mLayout.setVisibility(View.VISIBLE);
    }

    public void hide() {
        stopLoadMore();
        this.mLoadLayout.setVisibility(View.GONE);
        this.mEndLayout.setVisibility(View.GONE);
        this.mLayout.setVisibility(View.GONE);
    }

    public void showLoadMore() {
        this.mLoadLayout.setVisibility(View.VISIBLE);
        this.mEndLayout.setVisibility(View.GONE);
        show();
    }

    public void showLoadEnd() {
        this.mEndLayout.setVisibility(View.VISIBLE);
        this.mLoadLayout.setVisibility(View.GONE);
        stopLoadMore();
        show();
    }

    public void startLoadMore() {
        showLoadMore();
        this.mAnimation.reset();
        this.mLoadProgress.clearAnimation();
        this.mLoadProgress.startAnimation(this.mAnimation);
    }

    public void stopLoadMore() {
        this.mLoadProgress.setVisibility(4);
        this.mLoadProgress.clearAnimation();
        this.mAnimationDrawable.stop();
    }
}
