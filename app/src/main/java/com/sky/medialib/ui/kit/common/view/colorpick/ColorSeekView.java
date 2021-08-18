package com.sky.medialib.ui.kit.common.view.colorpick;

import android.content.Context;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.sky.medialib.R;
import com.sky.medialib.util.WeakHandler;


public class ColorSeekView extends LinearLayout implements ColorPickerSeekBar.OnChangeListener {

    private ColorPickBubbleView colorPickBubbleView;
    private ColorPickerSeekBar pickerSeekBar;
    private IChangeTextColorListener iChangeTextColorListener;
    private WeakHandler weakHandler = new WeakHandler(new Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            colorPickBubbleView.setVisibility(INVISIBLE);
            return false;
        }
    });

    public interface IChangeTextColorListener {
        void onChangeColor(int color);
    }

    public ColorSeekView(Context context) {
        super(context);
        init(context);
    }

    public ColorSeekView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public ColorSeekView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
    }

    private void init(Context context) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.vw_color_pick, this, true);
        colorPickBubbleView = (ColorPickBubbleView) inflate.findViewById(R.id.bubble_view);
        pickerSeekBar = (ColorPickerSeekBar) inflate.findViewById(R.id.pick_seek_bar);
        pickerSeekBar.setITrackerListener(this);
        resetSeekBarPos();
    }

    private void resetSeekBarPos() {
        colorPickBubbleView.setY((float) ((pickerSeekBar.getTrackerPosition() + pickerSeekBar.getTop()) - (colorPickBubbleView.getHeight() / 2)));
    }

    @Override
    public void changeColor(int i, int i2) {
        colorPickBubbleView.setColor(i2);
        resetSeekBarPos();
        if (iChangeTextColorListener != null) {
            iChangeTextColorListener.onChangeColor(i2);
        }
    }

    @Override
    public void onStartTouch(boolean z) {
        if (z) {
           weakHandler.removeMessages(1);
            resetSeekBarPos();
           colorPickBubbleView.setVisibility(VISIBLE);
        }else{
           weakHandler.removeMessages(1);
           weakHandler.sendEmptyMessageDelayed(1, 2000);
        }

    }

    public void setIChangeTextColor(IChangeTextColorListener listener) {
       iChangeTextColorListener = listener;
    }

    public void setSeekBarPositionByColor(int i) {
        if (pickerSeekBar != null) {
           pickerSeekBar.setSeekBarPositionByColor(i);
        }
    }
}
