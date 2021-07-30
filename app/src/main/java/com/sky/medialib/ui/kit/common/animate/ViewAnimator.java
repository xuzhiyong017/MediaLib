package com.sky.medialib.ui.kit.common.animate;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.Interpolator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ViewAnimator {
    private List<AnimationBuilder> mBuilderList = new ArrayList();
    private long duration = 3000;
    private long startDelay = 0;
    private Interpolator mInterpolator = null;
    private int repeatCount = 0;
    private int repeatMode = 1;
    private AnimatorSet mAnimatorSet;
    private View mView = null;
    private AnimationListener.OnStartListener mStartListener;
    private AnimationListener.OnEndListener mEndListener;
    private ViewAnimator animatorRef = null;
    private ViewAnimator animator = null;

    public static AnimationBuilder animate(View... viewArr) {
        return new ViewAnimator().createAnimator(viewArr);
    }

    public AnimationBuilder bindView(View... viewArr) {
        ViewAnimator viewAnimator = new ViewAnimator();
        this.animator = viewAnimator;
        viewAnimator.animatorRef = this;
        return viewAnimator.createAnimator(viewArr);
    }

    public AnimationBuilder createAnimator(View... viewArr) {
        AnimationBuilder animationBuilder = new AnimationBuilder(this, viewArr);
        this.mBuilderList.add(animationBuilder);
        return animationBuilder;
    }

    protected AnimatorSet createAnimatorSet() {
        Collection<Animator> arrayList = new ArrayList();
        for (AnimationBuilder animationBuilder : this.mBuilderList) {
            Collection<Animator> a = animationBuilder.getAnimatorList();
            if (animationBuilder.getInterpolator() != null) {
                for (Animator interpolator : a) {
                    interpolator.setInterpolator(animationBuilder.getInterpolator());
                }
            }
            arrayList.addAll(a);
        }
        for (AnimationBuilder animationBuilder2 : this.mBuilderList) {
            if (animationBuilder2.isReadyedDraw()) {
                this.mView = animationBuilder2.getFirstView();
                break;
            }
        }
        for (Animator animator : arrayList) {
            if (animator instanceof ValueAnimator) {
                ValueAnimator valueAnimator = (ValueAnimator) animator;
                valueAnimator.setRepeatCount(this.repeatCount);
                valueAnimator.setRepeatMode(this.repeatMode);
            }
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(arrayList);
        animatorSet.setDuration(this.duration);
        animatorSet.setStartDelay(this.startDelay);
        if (this.mInterpolator != null) {
            animatorSet.setInterpolator(this.mInterpolator);
        }
        animatorSet.addListener(new AnimatorListener() {
            public void onAnimationStart(Animator animator) {
                if (ViewAnimator.this.mStartListener != null) {
                    ViewAnimator.this.mStartListener.onStart();
                }
            }

            public void onAnimationEnd(Animator animator) {
                if (ViewAnimator.this.mEndListener != null) {
                    ViewAnimator.this.mEndListener.onEnd();
                }
                if (ViewAnimator.this.animator != null) {
                    ViewAnimator.this.animator.animatorRef = null;
                    ViewAnimator.this.animator.start();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return animatorSet;
    }

    public ViewAnimator start() {
        if (this.animatorRef != null) {
            this.animatorRef.start();
        } else {
            this.mAnimatorSet = createAnimatorSet();
            if (this.mView != null) {
                this.mView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        ViewAnimator.this.mAnimatorSet.start();
                        ViewAnimator.this.mView.getViewTreeObserver().removeOnPreDrawListener(this);
                        return false;
                    }
                });
            } else {
                this.mAnimatorSet.start();
            }
        }
        return this;
    }

    public void cancel() {
        if (this.mAnimatorSet != null) {
            this.mAnimatorSet.cancel();
        }
        if (this.animator != null) {
            this.animator.cancel();
            this.animator = null;
        }
    }

    public ViewAnimator setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    public ViewAnimator setDelayDuration(long duration) {
        this.startDelay = duration;
        return this;
    }

    public ViewAnimator setRepeatCount(int i) {
        this.repeatCount = i;
        return this;
    }

    public ViewAnimator setRepeatMode(int mode) {
        this.repeatMode = mode;
        return this;
    }

    public ViewAnimator setOnStartListener(AnimationListener.OnStartListener onStartListener) {
        this.mStartListener = onStartListener;
        return this;
    }

    public ViewAnimator setOnEndListener(AnimationListener.OnEndListener onEndListener) {
        this.mEndListener = onEndListener;
        return this;
    }

    public ViewAnimator setInterplolator(Interpolator interpolator) {
        this.mInterpolator = interpolator;
        return this;
    }
}
