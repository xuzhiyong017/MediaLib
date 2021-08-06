package com.sky.medialib.ui.kit.view.camera;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sky.medialib.R;


public class FocusIndicatorView extends RelativeLayout implements FocusIndicator {

    private ImageView focusView;
    private ScaleAnimation scaleAnimation = new ScaleAnimation(0.9f, 1.05f, 0.9f, 1.05f, 1, 0.5f, 1, 0.5f);

    public FocusIndicatorView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.focusView = (ImageView) LayoutInflater.from(context).inflate(R.layout.view_foucs, this).findViewById(R.id.out_radio);
        this.scaleAnimation.setDuration(600);
        this.scaleAnimation.setRepeatCount(-1);
        this.scaleAnimation.setRepeatMode(2);
        setVisibility(View.GONE);
    }

    @Override
    public void focusStart() {
        if (this.scaleAnimation != null) {
            this.scaleAnimation.cancel();
        }
        setVisibility(View.VISIBLE);
        this.focusView.startAnimation(this.scaleAnimation);
    }

    public void mo17893b() {
    }

    public void mo17894c() {
    }

    public void focusStop() {
        if (this.scaleAnimation != null) {
            this.scaleAnimation.cancel();
        }
        setVisibility(View.GONE);
    }
}
