package com.sky.medialib.ui.kit.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.sky.media.image.core.process.ImageProcessExt;
import com.sky.media.kit.filter.BuffingTool;
import com.sky.media.kit.filter.WhiteningTool;
import com.sky.medialib.R;
import com.sky.medialib.ui.kit.manager.ToolFilterManager;

public class BeautyAdjusterPopupWindow extends PopupWindow {

    private Context mContext;
    private TextView btnWhite;
    private TextView btnBuffing;
    private RadioGroup mRadioGroup;
    private ImageProcessExt imageProcessExt;
    private int lastWhiteValue;
    private int lastBuffingToolValue;
    private int curWhiteValue;
    private int curBuffingToolValue;
    private BuffingTool mBuffingTool;
    private WhiteningTool mWhiteningTool;
    private int mSelectTag = 10001;
    private boolean f9303u = false;
    private String publishTag;

    public interface OnTouchUpListener {
        void touchUp();
    }

    public BeautyAdjusterPopupWindow(Context context, String str, ImageProcessExt imageProcessExt, final OnTouchUpListener onTouchUpListener) {
        mContext = context;
        publishTag = str;
        this.imageProcessExt = imageProcessExt;
        View view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_beauty_adjuster_popup, null);
        btnWhite = (TextView) view.findViewById(R.id.white_btn);
        btnWhite.setOnClickListener(v -> {
            mSelectTag = 10001;
            updateViewStatus();
        });
        btnBuffing = (TextView) view.findViewById(R.id.buffing_btn);
        btnBuffing.setOnClickListener(v -> {
            mSelectTag = 10002;
            updateViewStatus();
        });
        setContentView(view);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        view.setOnTouchListener((v, event) -> dealTouchEvent(onTouchUpListener,v,event));
        mBuffingTool = ToolFilterManager.INSTANCE.getBuffingTool();
        mWhiteningTool = ToolFilterManager.INSTANCE.getWhiteningTool();
        mRadioGroup = (RadioGroup) view.findViewById(R.id.beauty_level_group);
        mRadioGroup.setOnCheckedChangeListener((group, checkedId) -> dealOnCheckedChanged(group,checkedId));
        updateBtnStatus();
        updateViewStatus();
    }

     boolean dealTouchEvent(OnTouchUpListener onTouchUpListener, View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                f9303u = false;
                return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (f9303u) {
                    imageProcessExt.refreshAllFilters();
                    return false;
                } else {
                    lastBuffingToolValue = curBuffingToolValue;
                    lastWhiteValue = curWhiteValue;
                    adjustBuffing();
                    adjustWhite();
                    dismiss();
                    onTouchUpListener.touchUp();
                    return true;
                }
            default:
                return false;
        }
    }

    void dealOnCheckedChanged(RadioGroup radioGroup, int checkId) {
        if (checkId == R.id.level_0) {
            updateFilterValue(0);
            checkedStatus(0);
        } else if (checkId == R.id.level_1) {
            updateFilterValue(1);
            checkedStatus(1);
        } else if (checkId == R.id.level_2) {
            updateFilterValue(2);
            checkedStatus(2);
        } else if (checkId == R.id.level_3) {
            updateFilterValue(3);
            checkedStatus(3);
        } else if (checkId == R.id.level_4) {
            updateFilterValue(4);
            checkedStatus(4);
        } else if (checkId == R.id.level_5) {
            updateFilterValue(5);
            checkedStatus(5);
        }
    }

    private void adjustWhite() {
        if (curWhiteValue != 0) {
            mWhiteningTool.getAdjuster().adjust(curWhiteValue);
            imageProcessExt.addFilter(mWhiteningTool);
            return;
        }
        imageProcessExt.removeFilter(mWhiteningTool);
    }

    private void adjustBuffing() {
        if (curBuffingToolValue != 0) {
            mBuffingTool.getAdjuster().adjust(curBuffingToolValue);
            imageProcessExt.addFilter(mBuffingTool);
            return;
        }
        imageProcessExt.removeFilter(mBuffingTool);
    }



    private void updateFilterValue(int value) {
        switch (mSelectTag) {
            case 10001:
                curWhiteValue = value;
                adjustWhite();
                return;
            case 10002:
                curBuffingToolValue = value;
                adjustBuffing();
                return;
            default:
                return;
        }
    }

    private void updateBtnStatus() {
        btnBuffing.setSelected(false);
        btnWhite.setSelected(false);
        switch (mSelectTag) {
            case 10001:
                btnWhite.setSelected(true);
                return;
            case 10002:
                btnBuffing.setSelected(true);
                return;
            default:
                return;
        }
    }

    private void checkedStatus(int i) {
        switch (i) {
            case 0:
                mRadioGroup.check(R.id.level_0);
                return;
            case 1:
                mRadioGroup.check(R.id.level_1);
                return;
            case 2:
                mRadioGroup.check(R.id.level_2);
                return;
            case 3:
                mRadioGroup.check(R.id.level_3);
                return;
            case 4:
                mRadioGroup.check(R.id.level_4);
                return;
            case 5:
                mRadioGroup.check(R.id.level_5);
                return;
            default:
                return;
        }
    }

    private void updateViewStatus() {
        updateBtnStatus();
        if (mSelectTag == 10002) {
            checkedStatus(curBuffingToolValue);
        }else if (mSelectTag == 10001) {
            checkedStatus(curWhiteValue);
        }
    }

    public void showAtLocation(View view, int i, int i2, int i3) {
        super.showAtLocation(view, i, i2, i3);
        mSelectTag = 10001;
        curWhiteValue = lastWhiteValue;
        curBuffingToolValue = lastBuffingToolValue;
        updateBtnStatus();
        updateViewStatus();
    }
}
