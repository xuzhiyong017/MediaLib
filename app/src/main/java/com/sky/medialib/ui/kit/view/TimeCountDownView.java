package com.sky.medialib.ui.kit.view;

import android.content.Context;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.sky.medialib.R;
import com.sky.medialib.ui.kit.common.view.NavigationTabStrip;
import com.sky.medialib.util.UIHelper;
import com.sky.medialib.util.WeakHandler;

public class TimeCountDownView extends FrameLayout {


    private final int[] mCountDownResId = new int[]{R.drawable.count_down_1, R.drawable.count_down_2, R.drawable.count_down_3, R.drawable.count_down_4, R.drawable.count_down_5, R.drawable.count_down_6, R.drawable.count_down_7, R.drawable.count_down_8, R.drawable.count_down_9, R.drawable.count_down_10};

    private NavigationTabStrip mNavigationTabStrip;
    private TextView mTextStart;
    private ImageView mImageView;
    private int mCount;
    private CountDownListener mListener;
    private Animation mAnimation;
    private int chooseTime;
    private int time = 3;
    private WeakHandler mHandler = new WeakHandler(new CountDownCallback());

    public interface CountDownListener {
        void onFinish();
        void start();
    }

    class CountDownCallback implements Callback {
        CountDownCallback() {
        }

        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    if (TimeCountDownView.this.mCount > 0) {
                        TimeCountDownView.this.mHandler.sendEmptyMessageDelayed(1, 1000);
                        TimeCountDownView.this.mCount = TimeCountDownView.this.mCount - 1;
                        TimeCountDownView.this.mImageView.setImageResource(TimeCountDownView.this.mCountDownResId[TimeCountDownView.this.mCount]);
                    } else {
                        TimeCountDownView.this.mImageView.setImageResource(0);
                        if (TimeCountDownView.this.mListener != null) {
                            TimeCountDownView.this.mListener.onFinish();
                        }
                    }
                    TimeCountDownView.this.mImageView.startAnimation(TimeCountDownView.this.mAnimation);
                    break;
            }
            return true;
        }
    }

    public TimeCountDownView(Context context) {
        super(context);
        initView();
    }

    public TimeCountDownView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView();
    }

    public TimeCountDownView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initView();
    }

    private void initView() {
        UIHelper.inflateView(getContext(), R.layout.camera_time_count, this);
        this.mNavigationTabStrip = (NavigationTabStrip) findViewById(R.id.count_down_tab);
        this.mTextStart = (TextView) findViewById(R.id.count_down_start);
        this.mImageView = (ImageView) findViewById(R.id.count_down_animation);
        this.mAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in_then_out);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mImageView.getVisibility() != VISIBLE) {
                    mCount = 0;
                    mHandler.removeMessages(1);
                    setVisibility(GONE);
                    mNavigationTabStrip.setVisibility(VISIBLE);
                    mTextStart.setVisibility(VISIBLE);
                    mImageView.setVisibility(GONE);
                    if (mListener != null) {
                        mListener.start();
                    }
                }
            }
        });
        this.mTextStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCount =time;
                chooseTime =time;
                mNavigationTabStrip.setVisibility(GONE);
                mTextStart.setVisibility(GONE);
                mImageView.setVisibility(VISIBLE);
                startCountDown();
            }
        });
        this.mNavigationTabStrip.setTabIndex(0);
        mNavigationTabStrip.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        time = 3;
                        break;
                    case 1:
                        time = 5;
                        break;
                    case 2:
                        time = 10;
                        break;
                }
            }
        });

        mNavigationTabStrip.setTitles("3S","5S","10S");
    }

    public int getChooseTime() {
        return this.chooseTime;
    }

    public void startCountDown() {
        if (this.mCount > 0) {
            this.mHandler.sendEmptyMessage(1);
        }
    }

    public void doJob() {
        this.mImageView.setImageResource(0);
        this.mImageView.clearAnimation();
        this.mImageView.setVisibility(GONE);
        performClick();
    }

    public void setCountDownTimeListener(CountDownListener countDownListener) {
        this.mListener = countDownListener;
    }
}
