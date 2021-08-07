package com.sky.medialib.ui.kit.manager;

import android.annotation.SuppressLint;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera.Area;
import android.hardware.Camera.Parameters;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout.LayoutParams;


import com.sky.media.image.core.util.LogUtils;
import com.sky.medialib.R;
import com.sky.medialib.ui.kit.view.camera.FocusIndicator;
import com.sky.medialib.ui.kit.view.camera.FocusIndicatorView;
import com.sky.medialib.util.Util;
import com.sky.medialib.util.WeakHandler;

import java.util.ArrayList;
import java.util.List;

@SuppressLint({"NewApi"})
public class FocusManager {

    private int mState = 0;
    private boolean parametersIsInit;
    private boolean canContinuousFocus;
    private boolean useContinuousMode;
    private Matrix mMatrix;
    private View mFocusIndicator;
    private FocusIndicatorView indicatorView;
    private View processSurfaceView;
    private List<Area> focusAreaList;
    private List<Area> meteringAreaList;
    private List<Area> areaList;
    private List<Area> areaList1;
    private String mCurFocusMode;
    private String initFocusMode;
    private String forceFocusMode;
    private Parameters parameters;
    private WeakHandler mHandler;
    private OnFocusListener onFocusListener;
    private boolean isFaceFront;
    private float downX;
    private float downY;

    public interface OnFocusListener {
        void autoFocus();

        void cancelAutoFocus();

        boolean capture();

        void setFocusParameters();
    }

    public FocusManager(String str) {
        this.initFocusMode = str;
        this.mHandler = new WeakHandler(new Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        FocusManager.this.cancelAutoFocus();
                        break;
                }
                return true;
            }
        });
        this.mMatrix = new Matrix();
    }

    public void setParameters(Parameters parameters, boolean z) {
        this.parameters = parameters;
        this.isFaceFront = z;
        boolean z2 = this.parameters.getMaxNumFocusAreas() > 0 && FocusManager.hasContains("continuous-picture", this.parameters.getSupportedFocusModes());
        this.canContinuousFocus = z2;
    }

    public void init(View view, View view2, OnFocusListener onFocusListener, boolean z, int displayOrientation) {
        this.mFocusIndicator = view;
        this.indicatorView = (FocusIndicatorView) view.findViewById(R.id.focus_indicator);
        this.processSurfaceView = view2;
        this.onFocusListener = onFocusListener;
        Matrix matrix = new Matrix();
        Util.scaleMatrix(matrix, z, displayOrientation, view2.getWidth(), view2.getHeight());
        matrix.invert(this.mMatrix);
        if (this.parameters != null) {
            this.parametersIsInit = true;
        } else {
            LogUtils.logd("FocusManager", "mParameters is not initialized.");
        }
        if (this.areaList == null) {
            this.areaList = new ArrayList();
            this.areaList.add(new Area(new Rect(), 1));
            this.areaList1 = new ArrayList();
            this.areaList1.add(new Area(new Rect(), 1));
            int width = this.mFocusIndicator.getWidth();
            int height = this.mFocusIndicator.getHeight();
            int width2 = this.processSurfaceView.getWidth();
            int height2 = this.processSurfaceView.getHeight();
            calculateRect(width, height, 2.0f, width2 / 2, width2 / 2, width2, height2, ((Area) this.areaList.get(0)).rect);
            calculateRect(width, height, 3.0f, width2 / 2, width2 / 2, width2, height2, ((Area) this.areaList1.get(0)).rect);
        }
    }

    public void takePhoto() {
        if (!this.parametersIsInit) {
            return;
        }
        if (!isContinuousMode() || this.mState == 3 || this.mState == 4) {
            capture();
        } else if (this.mState == 1) {
            this.mState = 2;
        } else if (this.mState == 0) {
            capture();
        }
    }

    public void onAutoFocus(boolean success) {
        if (this.mState == 2) {
            if (success) {
                this.mState = 3;
            } else {
                this.mState = 4;
            }
            updateLayoutStatus();
            capture();
        } else if (this.mState == 1) {
            if (success) {
                this.mState = 3;
            } else {
                this.mState = 4;
            }
            updateLayoutStatus();
            if (this.focusAreaList != null) {
                this.mHandler.sendEmptyMessageDelayed(0, 3000);
            }
        } else {
            if (this.mState == 0) {
            }
        }
    }

    public boolean onTouch(MotionEvent motionEvent) {
        if (!this.parametersIsInit || this.mState == 2) {
            return false;
        }
        int action = motionEvent.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            this.downX = motionEvent.getX();
            this.downY = motionEvent.getY();
        } else if (action == MotionEvent.ACTION_UP && Math.abs(motionEvent.getX() - this.downX) < 10.0f && Math.abs(motionEvent.getY() - this.downY) < 10.0f) {
            if (this.focusAreaList != null && (this.mState == 1 || this.mState == 3 || this.mState == 4)) {
                cancelAutoFocus();
            }
            int x = Math.round(motionEvent.getX());
            int y = Math.round(motionEvent.getY());
            int width = this.mFocusIndicator.getWidth();
            int height = this.mFocusIndicator.getHeight();
            int width2 = this.processSurfaceView.getWidth();
            int height2 = this.processSurfaceView.getHeight();
            if (this.focusAreaList == null) {
                this.focusAreaList = new ArrayList();
                this.focusAreaList.add(new Area(new Rect(), 1));
                this.meteringAreaList = new ArrayList();
                this.meteringAreaList.add(new Area(new Rect(), 1));
            }
            calculateRect(width, height, 1.0f, x, y, width2, height2, ((Area) this.focusAreaList.get(0)).rect);
            calculateRect(width, height, 1.5f, x, y, width2, height2, ((Area) this.meteringAreaList.get(0)).rect);
            ((Area) this.areaList.get(0)).rect.set(((Area) this.focusAreaList.get(0)).rect);
            ((Area) this.areaList1.get(0)).rect.set(((Area) this.meteringAreaList.get(0)).rect);
            LayoutParams layoutParams = (LayoutParams) this.mFocusIndicator.getLayoutParams();
            layoutParams.setMargins(Util.getMinNum(x - (width / 2), 0, width2 - width), Util.getMinNum(y - (height / 2), 0, height2 - height), 0, 0);
            layoutParams.getRules()[13] = 0;
            this.mFocusIndicator.requestLayout();
            this.onFocusListener.setFocusParameters();
            if (this.canContinuousFocus) {
                canAutoFocus();
            } else {
                updateLayoutStatus();
            }
            this.mHandler.removeMessages(0);
            this.mHandler.sendEmptyMessageDelayed(0, 3000);
        }
        return true;
    }

    public void initStatus() {
        this.mState = 0;
    }

    public void release() {
        this.mState = 0;
        resetLayout();
        updateLayoutStatus();
    }

    public void clear() {
        release();
    }

    private void canAutoFocus() {
        this.onFocusListener.autoFocus();
        this.mState = 1;
        updateLayoutStatus();
        this.mHandler.removeMessages(0);
    }

    private void cancelAutoFocus() {
        resetLayout();
        this.onFocusListener.cancelAutoFocus();
        this.mState = 0;
        updateLayoutStatus();
        this.mHandler.removeMessages(0);
    }

    private void capture() {
        if (this.onFocusListener.capture()) {
            this.mState = 0;
            this.mHandler.removeMessages(0);
        }
    }

    public String getSupportFocusMode() {
        if (this.forceFocusMode != null) {
            return this.forceFocusMode;
        }
        if (this.useContinuousMode) {
            this.mCurFocusMode = "continuous-picture";
        } else if (!this.canContinuousFocus || this.focusAreaList == null) {
            this.mCurFocusMode = this.initFocusMode;
        } else {
            this.mCurFocusMode = "continuous-picture";
        }
        if (!FocusManager.hasContains(this.mCurFocusMode, this.parameters.getSupportedFocusModes())) {
            if (FocusManager.hasContains("continuous-picture", this.parameters.getSupportedFocusModes())) {
                this.mCurFocusMode = "continuous-picture";
            } else {
                this.mCurFocusMode = this.parameters.getFocusMode();
            }
        }
        return this.mCurFocusMode;
    }

    public List<Area> getFocusArea() {
        return this.focusAreaList;
    }

    public List<Area> getMeteringArea() {
        return this.meteringAreaList;
    }

    public void updateLayoutStatus() {
        if (this.parametersIsInit) {
            int min = Math.min(this.processSurfaceView.getWidth(), this.processSurfaceView.getHeight()) / 4;
            ViewGroup.LayoutParams layoutParams = this.indicatorView.getLayoutParams();
            layoutParams.width = min;
            layoutParams.height = min;
            FocusIndicator focusIndicator = this.indicatorView;
            if (this.isFaceFront) {
                this.mFocusIndicator.setVisibility(View.INVISIBLE);
                return;
            }
            this.mFocusIndicator.setVisibility(View.VISIBLE);
            if (this.mState == 0) {
                if (this.focusAreaList == null) {
                    focusIndicator.focusStop();
                } else {
                    focusIndicator.focusStart();
                }
            } else if (this.mState == 1 || this.mState == 2) {
                focusIndicator.focusStart();
            } else if ("continuous-picture".equals(this.mCurFocusMode)) {
                focusIndicator.focusStart();
            } else if (this.mState == 3) {
                focusIndicator.mo17893b();
            } else if (this.mState == 4) {
                focusIndicator.mo17894c();
            }
        }
    }

    public void resetLayout() {
        if (this.parametersIsInit) {
            LayoutParams layoutParams = (LayoutParams) this.mFocusIndicator.getLayoutParams();
            layoutParams.getRules()[13] = -1;
            layoutParams.setMargins(0, 0, 0, 0);
            this.focusAreaList = null;
            this.meteringAreaList = null;
        }
    }

    public void calculateRect(int i, int i2, float f, int i3, int i4, int i5, int i6, Rect rect) {
        int i7 = (int) (((float) i) * f);
        int i8 = (int) (((float) i2) * f);
        int a = Util.getMinNum(i3 - (i7 / 2), 0, i5 - i7);
        int a2 = Util.getMinNum(i4 - (i8 / 2), 0, i6 - i8);
        RectF rectF = new RectF((float) a, (float) a2, (float) (i7 + a), (float) (i8 + a2));
        this.mMatrix.mapRect(rectF);
        Util.correctRect(rectF, rect);
    }

    public void cancelFocus() {
        this.mHandler.removeMessages(0);
    }

    public void setForceFocusMode(String str) {
        this.forceFocusMode = str;
    }

    private static boolean hasContains(String str, List<String> list) {
        return list != null && list.indexOf(str) >= 0;
    }

    private boolean isContinuousMode() {
        String e = getSupportFocusMode();
        return (e.equals("infinity") || e.equals("fixed") || e.equals("edof")) ? false : true;
    }
}
