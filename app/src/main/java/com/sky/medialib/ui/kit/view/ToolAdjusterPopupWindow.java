package com.sky.medialib.ui.kit.view;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.sky.media.image.core.filter.Adjuster;
import com.sky.media.kit.model.FilterExt;
import com.sky.medialib.R;


public class ToolAdjusterPopupWindow extends PopupWindow {

    private TextView seek_bar_value;
    private ToolSeekBar seek_bar;

    public ToolAdjusterPopupWindow(Context context, int i, FilterExt filterExt, final IToolAdjustedListener iToolAdjustedListener) {
        final Adjuster adjuster = filterExt.getAdjuster();
        View inflate = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_tool_adjuster_popup, null);
        View findViewById = inflate.findViewById(R.id.tool_panel);
        LayoutParams layoutParams = (LayoutParams) findViewById.getLayoutParams();
        layoutParams.height = i;
        findViewById.setLayoutParams(layoutParams);
        this.seek_bar_value = (TextView) inflate.findViewById(R.id.seek_bar_value);
        this.seek_bar = (ToolSeekBar) inflate.findViewById(R.id.seek_bar);
        this.seek_bar.setMax(adjuster.getEnd() - adjuster.getStart());
        this.seek_bar.setIsMiddleZero(adjuster.getStart() < 0);
        this.seek_bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                processFilter(adjuster, i, iToolAdjustedListener);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        ((ImageView) inflate.findViewById(R.id.cancel)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                ToolAdjusterPopupWindow.this.dismiss();
                iToolAdjustedListener.cancel();
            }
        });
        ((ImageView) inflate.findViewById(R.id.ok)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                ToolAdjusterPopupWindow.this.dismiss();
                iToolAdjustedListener.ok();
            }
        });
        ((TextView) inflate.findViewById(R.id.title)).setText(filterExt.getName());
        inflate.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() != 0 || i != 4) {
                    return false;
                }
                ToolAdjusterPopupWindow.this.dismiss();
                iToolAdjustedListener.cancel();
                return true;
            }
        });
        ((ImageView) inflate.findViewById(R.id.touch_view)).setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == 0) {
                    iToolAdjustedListener.touchDown();
                    return true;
                }
                if (action == 1) {
                    iToolAdjustedListener.touchUp();
                }
                return false;
            }
        });
        setContentView(inflate);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        inflate.post(new Runnable() {
            public void run() {
                if (ToolAdjusterPopupWindow.this.seek_bar.getProgress() == adjuster.getProgress() - adjuster.getStart()) {
                    ToolAdjusterPopupWindow.this.processFilter(adjuster, adjuster.getProgress() - adjuster.getStart(), iToolAdjustedListener);
                } else {
                    ToolAdjusterPopupWindow.this.seek_bar.setProgress(adjuster.getProgress() - adjuster.getStart());
                }
            }
        });
    }

    private void processFilter(Adjuster adjuster, int i, IToolAdjustedListener iToolAdjustedListener) {
        this.seek_bar_value.setX((this.seek_bar.getX() + this.seek_bar.getThumbX()) - ((float) (this.seek_bar_value.getWidth() / 2)));
        this.seek_bar_value.invalidate();
        int start = adjuster.getStart() + i;
        adjuster.adjust(start);
        this.seek_bar_value.setText(adjuster.getProgressText());
        iToolAdjustedListener.onProgress(start);
    }
}
