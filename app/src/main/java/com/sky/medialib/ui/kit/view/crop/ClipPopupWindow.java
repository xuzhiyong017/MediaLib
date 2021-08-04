package com.sky.medialib.ui.kit.view.crop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;


import com.sky.medialib.R;
import com.sky.medialib.ui.kit.view.ToolSeekBar;

import java.util.HashMap;
import java.util.Map;

public class ClipPopupWindow extends PopupWindow implements OnClickListener {

    private CropView cropView;
    private View[] mMenuViews;
    private View[] mMenuLayoutView;
    private int mMenuSelectIndex;
    private View[] mRatioViews;
    private int mRatioSelectIndex;
    private TextView mSeekBarValue;
    private ToolSeekBar mToolSeekBar;
    private onClipListener mCallback;

    public interface onClipListener {
        void onComplete();
        void onCancel();
    }

    public ClipPopupWindow(Context context, CropView cropView, onClipListener onClipListener) {
        this.cropView = cropView;
        this.mCallback = onClipListener;
        View inflate = LayoutInflater.from(context).inflate(R.layout.vw_clip_menu, null);
        this.mRatioViews = new View[]{inflate.findViewById(R.id.clip_ratio_free), inflate.findViewById(R.id.clip_ratio_11), inflate.findViewById(R.id.clip_ratio_34), inflate.findViewById(R.id.clip_ratio_43), inflate.findViewById(R.id.clip_ratio_916), inflate.findViewById(R.id.clip_ratio_169), inflate.findViewById(R.id.clip_ratio_reset)};
        View[] viewArr = new View[]{inflate.findViewById(R.id.clip_rotate_l), inflate.findViewById(R.id.clip_rotate_r), inflate.findViewById(R.id.clip_rotate_reset)};
        View[] viewArr2 = new View[]{inflate.findViewById(R.id.clip_mirror_horizontal), inflate.findViewById(R.id.clip_mirror_vertical), inflate.findViewById(R.id.clip_mirror_reset)};
        this.mMenuViews = new View[]{inflate.findViewById(R.id.clip_ratio), inflate.findViewById(R.id.clip_rotate), inflate.findViewById(R.id.clip_mirror), inflate.findViewById(R.id.clip_cancel), inflate.findViewById(R.id.clip_ok)};
        this.mMenuLayoutView = new View[]{inflate.findViewById(R.id.ratio), inflate.findViewById(R.id.rotate), inflate.findViewById(R.id.mirror)};
        for (View onClickListener : this.mRatioViews) {
            onClickListener.setOnClickListener(this);
        }
        for (View onClickListener2 : viewArr) {
            onClickListener2.setOnClickListener(this);
        }
        for (View onClickListener3 : viewArr2) {
            onClickListener3.setOnClickListener(this);
        }
        for (View onClickListener32 : this.mMenuViews) {
            onClickListener32.setOnClickListener(this);
        }
        for (View onClickListener322 : this.mMenuLayoutView) {
            onClickListener322.setVisibility(View.INVISIBLE);
        }
        this.mSeekBarValue = (TextView) inflate.findViewById(R.id.seek_bar_value);
        this.mToolSeekBar = (ToolSeekBar) inflate.findViewById(R.id.clip_rotate_seek);
        this.mToolSeekBar.setIsMiddleZero(true);
        this.mToolSeekBar.setProgress(((int) this.cropView.getRotateDegrees()) + 70);
        this.mToolSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ClipPopupWindow.this.onProgressChanged(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ClipPopupWindow.this.cropView.setImageToWrapCropBounds(true);
            }
        });
        this.mMenuViews[this.mMenuSelectIndex].setSelected(true);
        this.mMenuLayoutView[this.mMenuSelectIndex].setVisibility(View.VISIBLE);
        this.mRatioViews[0].setSelected(true);
        this.cropView.saveState();
        setContentView(inflate);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void onClick(View view) {
        int id = view.getId();
        this.mRatioViews[this.mRatioSelectIndex].setSelected(false);
        this.mMenuViews[this.mMenuSelectIndex].setSelected(false);
        this.mMenuLayoutView[this.mMenuSelectIndex].setVisibility(View.INVISIBLE);
        Map hashMap = new HashMap();
        if (id == R.id.clip_ratio_free) {
            this.mRatioSelectIndex = 0;
            this.cropView.cropImageByRatio(0.0f, this.mRatioSelectIndex);
            hashMap.put("edit", "free");
        } else if (id == R.id.clip_ratio_11) {
            this.mRatioSelectIndex = 1;
            this.cropView.cropImageByRatio(1.0f, this.mRatioSelectIndex);
            hashMap.put("edit", "1");
        } else if (id == R.id.clip_ratio_34) {
            this.mRatioSelectIndex = 2;
            this.cropView.cropImageByRatio(0.75f, this.mRatioSelectIndex);
            hashMap.put("edit", "0.75");
        } else if (id == R.id.clip_ratio_43) {
            this.mRatioSelectIndex = 3;
            this.cropView.cropImageByRatio(1.3333334f, this.mRatioSelectIndex);
            hashMap.put("edit", "1.3");
        } else if (id == R.id.clip_ratio_916) {
            this.mRatioSelectIndex = 4;
            this.cropView.cropImageByRatio(0.5625f, this.mRatioSelectIndex);
            hashMap.put("edit", "0.56");
        } else if (id == R.id.clip_ratio_169) {
            this.mRatioSelectIndex = 5;
            this.cropView.cropImageByRatio(1.7777778f, this.mRatioSelectIndex);
            hashMap.put("edit", "1.78");
        } else if (id == R.id.clip_ratio_reset) {
            this.cropView.clipReset();
            this.mRatioSelectIndex = this.cropView.getRatioIndex();
            hashMap.put("edit", "recovery");
        } else if (id == R.id.clip_rotate_l) {
            this.cropView.rotate(-90.0f);
            this.cropView.setImageToWrapCropBounds(false);
            hashMap.put("edit", "rotate_l");
        } else if (id == R.id.clip_rotate_r) {
            this.cropView.rotate(90.0f);
            this.cropView.setImageToWrapCropBounds(false);
            hashMap.put("edit", "rotate_r");
        } else if (id == R.id.clip_rotate_reset) {
            this.cropView.clipReset();
            this.mToolSeekBar.setProgress(((int) this.cropView.getRotateDegrees()) + 70);
            hashMap.put("edit", "recovery");
        } else if (id == R.id.clip_mirror_horizontal) {
            this.cropView.postMirror(true);
        } else if (id == R.id.clip_mirror_vertical) {
            this.cropView.postMirror(false);
        } else if (id == R.id.clip_mirror_reset) {
            this.cropView.clipReset();
        } else if (id == R.id.clip_ratio) {
            this.cropView.saveState();
            this.mMenuSelectIndex = 0;
        } else if (id == R.id.clip_rotate) {
            this.cropView.saveState();
            this.mMenuSelectIndex = 1;
        } else if (id == R.id.clip_mirror) {
            this.cropView.saveState();
            this.mMenuSelectIndex = 2;
        } else if (id == R.id.clip_cancel) {
            this.cropView.clipReset();
            this.cropView.setVisibility(View.GONE);
            dismiss();
            if (this.mCallback != null) {
                this.mCallback.onCancel();
                return;
            }
            return;
        } else if (id == R.id.clip_ok) {
            if (this.cropView.mo18014c()) {
                this.cropView.mo18015d();
            } else {
                this.cropView.setVisibility(8);
            }
            if (this.mCallback != null) {
                this.mCallback.onComplete();
            }
            dismiss();
            return;
        }
        this.mRatioViews[this.mRatioSelectIndex].setSelected(true);
        this.mMenuViews[this.mMenuSelectIndex].setSelected(true);
        this.mMenuLayoutView[this.mMenuSelectIndex].setVisibility(View.VISIBLE);
    }

    private void onProgressChanged(int i) {
        this.mSeekBarValue.setX((this.mToolSeekBar.getX() + this.mToolSeekBar.getThumbX()) - ((float) (this.mSeekBarValue.getWidth() / 2)));
        this.mSeekBarValue.invalidate();
        int i2 = i - 70;
        if (i2 > 0) {
            this.mSeekBarValue.setText("+" + i2);
        } else if (i2 == 0) {
            this.mSeekBarValue.setText("");
        } else {
            this.mSeekBarValue.setText("" + i2);
        }
        this.cropView.rotate((float) i2);
    }
}
