package com.sky.medialib.ui.kit.common.animate;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import java.util.ArrayList;
import java.util.List;

public class AnimationBuilder {

    private final ViewAnimator viewAnimator;
    private final View[] mViews;
    private final List<Animator> mAnimatorList = new ArrayList();
    private boolean needStartOnPreDraw;
    private boolean needAdjust = false;
    private Interpolator mInterpolator = null;

    public AnimationBuilder(ViewAnimator viewAnimator, View... viewArr) {
        this.viewAnimator = viewAnimator;
        this.mViews = viewArr;
    }

    protected float scaleValue(float f) {
        return this.mViews[0].getContext().getResources().getDisplayMetrics().density * f;
    }

    protected float[] getFloatList(float... fArr) {
        if (!this.needAdjust) {
            return fArr;
        }
        float[] fArr2 = new float[fArr.length];
        for (int i = 0; i < fArr.length; i++) {
            fArr2[i] = scaleValue(fArr[i]);
        }
        return fArr2;
    }

    public AnimationBuilder bulidAnimator(String str, float... fArr) {
        for (Object ofFloat : this.mViews) {
            this.mAnimatorList.add(ObjectAnimator.ofFloat(ofFloat, str, getFloatList(fArr)));
        }
        return this;
    }

    public AnimationBuilder translationY(float... fArr) {
        return bulidAnimator("translationY", fArr);
    }

    public AnimationBuilder translationX(float... fArr) {
        return bulidAnimator("translationX", fArr);
    }

    public AnimationBuilder alpha(float... fArr) {
        return bulidAnimator("alpha", fArr);
    }

    public AnimationBuilder scaleX(float... fArr) {
        return bulidAnimator("scaleX", fArr);
    }

    public AnimationBuilder scaleY(float... fArr) {
        return bulidAnimator("scaleY", fArr);
    }

    public AnimationBuilder scaleView(float... fArr) {
        scaleX(fArr);
        scaleY(fArr);
        return this;
    }

    public AnimationBuilder rotation(float... fArr) {
        return bulidAnimator("rotation", fArr);
    }

    protected List<Animator> getAnimatorList() {
        return this.mAnimatorList;
    }

    public AnimationBuilder bindView(View... viewArr) {
        return this.viewAnimator.bindView(viewArr);
    }

    public AnimationBuilder setDuration(long duration) {
        this.viewAnimator.setDuration(duration);
        return this;
    }

    public AnimationBuilder setDelayDuration(long j) {
        this.viewAnimator.setDelayDuration(j);
        return this;
    }

    public AnimationBuilder setRepeatCount(int i) {
        this.viewAnimator.setRepeatCount(i);
        return this;
    }

    public AnimationBuilder setRepeatMode(int i) {
        this.viewAnimator.setRepeatMode(i);
        return this;
    }

    public AnimationBuilder setOnStartListener(AnimationListener.OnStartListener onStartListener) {
        this.viewAnimator.setOnStartListener(onStartListener);
        return this;
    }

    public AnimationBuilder setOnEndListener(AnimationListener.OnEndListener onEndListener) {
        this.viewAnimator.setOnEndListener(onEndListener);
        return this;
    }

    public AnimationBuilder setInterplolator(Interpolator interpolator) {
        this.viewAnimator.setInterplolator(interpolator);
        return this;
    }

    public Interpolator getInterpolator() {
        return this.mInterpolator;
    }

    public ViewAnimator interplolatorDecelerate() {
        return this.viewAnimator.setInterplolator(new DecelerateInterpolator());
    }

    public ViewAnimator start() {
        this.viewAnimator.start();
        return this.viewAnimator;
    }

    public View getFirstView() {
        return this.mViews[0];
    }

    public boolean isReadyedDraw() {
        return this.needStartOnPreDraw;
    }
}
