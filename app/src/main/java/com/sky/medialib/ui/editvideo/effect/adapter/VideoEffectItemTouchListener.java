package com.sky.medialib.ui.editvideo.effect.adapter;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class VideoEffectItemTouchListener implements RecyclerView.OnItemTouchListener{
    private GestureDetector mGestureDetector;
    private int mLongPressPosition = -1;
    private OnPressListener mPressListener;
    private RecyclerView mRecyclerView;

    public interface OnPressListener {
        void onSingleUp(int pos);
        void onLongPress(int pos, int action);
    }

    public VideoEffectItemTouchListener(Context context, RecyclerView recyclerView, OnPressListener onPressListener) {
        this.mRecyclerView = recyclerView;
        this.mPressListener = onPressListener;
        this.mGestureDetector = new GestureDetector(context, new SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                View findChildViewUnder = mRecyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                if (!(findChildViewUnder == null || mPressListener == null)) {
                    mPressListener.onSingleUp(mRecyclerView.getChildLayoutPosition(findChildViewUnder));
                }
                return false;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {
                View findChildViewUnder = mRecyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                if (findChildViewUnder != null && mPressListener != null) {
                    int childLayoutPosition = mRecyclerView.getChildLayoutPosition(findChildViewUnder);
                    if (mLongPressPosition != childLayoutPosition) {
                        mPressListener.onLongPress(childLayoutPosition, motionEvent.getAction());
                    }
                    mLongPressPosition = childLayoutPosition;
                }
            }
        });
    }

    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
        if (!((motionEvent.getAction() != 1 && motionEvent.getAction() != 3) || this.mLongPressPosition == -1 || this.mPressListener == null)) {
            this.mPressListener.onLongPress(this.mLongPressPosition, motionEvent.getAction());
            this.mLongPressPosition = -1;
        }
        return this.mGestureDetector.onTouchEvent(motionEvent);
    }

    public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
    }

    public void onRequestDisallowInterceptTouchEvent(boolean z) {
    }
}
