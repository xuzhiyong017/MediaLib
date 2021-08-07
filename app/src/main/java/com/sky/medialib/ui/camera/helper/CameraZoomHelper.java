package com.sky.medialib.ui.camera.helper;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.view.MotionEvent;
import android.widget.TextView;

import com.sky.medialib.R;


public class CameraZoomHelper {
    private Activity activity;
    private int status = 0;
    private float lastDistance;
    private TextView textZoomValue;

    public CameraZoomHelper(Activity activity) {
        this.activity = activity;
        init();
    }

    private void init() {
        this.textZoomValue = (TextView) this.activity.findViewById(R.id.zoom_value);
    }

    public void onTouch(MotionEvent motionEvent, Camera camera, Parameters parameters) {
        int i = 0;
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                this.status = 0;
                return;
            case MotionEvent.ACTION_MOVE:
                if (this.status == 1 && motionEvent.getPointerCount() >= 2) {
                    float distance = getDistance(motionEvent);
                    int i2 = (int) ((distance - this.lastDistance) / 10.0f);
                    if (i2 >= 1 || i2 <= -1) {
                        i2 += parameters.getZoom();
                        if (i2 > parameters.getMaxZoom()) {
                            i2 = parameters.getMaxZoom();
                        }
                        if (i2 >= 0) {
                            i = i2;
                        }
                        parameters.setZoom(i);
                        if (i == 0) {
                            this.textZoomValue.setText("");
                        } else {
                            this.textZoomValue.setText("x " + i);
                        }
                        try {
                            camera.setParameters(parameters);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                        this.lastDistance = distance;
                        return;
                    }
                    return;
                }
                return;
            case MotionEvent.ACTION_POINTER_DOWN:
                this.status = 1;
                this.lastDistance = getDistance(motionEvent);
                return;
            default:
                return;
        }
    }

    public int getStatus() {
        return this.status;
    }

    private float getDistance(MotionEvent motionEvent) {
        float x = motionEvent.getX(1) - motionEvent.getX(0);
        float y = motionEvent.getY(1) - motionEvent.getY(0);
        return (float) Math.sqrt((double) ((x * x) + (y * y)));
    }
}
