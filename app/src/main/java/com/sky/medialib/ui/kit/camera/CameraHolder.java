package com.sky.medialib.ui.kit.camera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;

import com.sky.media.image.core.util.LogUtils;


public class CameraHolder {
    private static CameraHolder sHolder = new CameraHolder();
    private Camera mCamera;
    private int cameraOperateCount = 0;
    private int numberOfCameras = Camera.getNumberOfCameras();
    private int mCurCameraId = -1;
    private int backId = -1;
    private int faceId = -1;
    private CameraInfo[] cameraInfos = new CameraInfo[numberOfCameras];

    public static synchronized CameraHolder getInstance() {
        CameraHolder cameraHolder;
        synchronized (CameraHolder.class) {
            cameraHolder = sHolder;
        }
        return cameraHolder;
    }

    private CameraHolder() {
        for (int i = 0; i < numberOfCameras; i++) {
           cameraInfos[i] = new CameraInfo();
            Camera.getCameraInfo(i, this.cameraInfos[i]);
            if (this.backId == -1 && this.cameraInfos[i].facing == 0) {
                this.backId = i;
            }
            if (this.faceId == -1 && this.cameraInfos[i].facing == 1) {
                this.faceId = i;
            }
        }
    }

    public int getNumberOfCameras() {
        return this.numberOfCameras;
    }

    public CameraInfo[] getCameraInfos() {
        return this.cameraInfos;
    }

    private synchronized Camera openCameraById(int cameraId) {
        Camera camera = null;
        synchronized (this) {
            if (!(this.mCamera == null || this.mCurCameraId == cameraId)) {
                this.mCamera.release();
                this.mCamera = null;
                this.mCurCameraId = -1;
            }
            if (this.mCamera == null) {
                try {
                    this.mCamera = Camera.open(cameraId);
                    this.mCurCameraId = cameraId;
                } catch (Throwable e) {
                    LogUtils.loge("CameraHolder", "fail to connect Camera"+ e.getMessage());
                }
            } else {
                try {
                    this.mCamera.reconnect();
                } catch (Throwable e2) {
                    LogUtils.loge("CameraHolder", "fail to connect Camera" + e2.getMessage());
                }
            }
            this.cameraOperateCount++;
            camera = this.mCamera;
        }
        return camera;
    }

    public static boolean canUseCamera(Activity activity) {
        return !((DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE)).getCameraDisabled(null);
    }

    public static Camera openCamera(int i) {
        return sHolder.openCameraById(i);
    }

    public static void adjustRotation(Parameters parameters, int cameraId, int orientation) {
        int i3 = 0;
        if (orientation != -1) {
            CameraInfo cameraInfo = sHolder.getCameraInfos()[cameraId];
            if (cameraInfo.facing == 1) {
                i3 = ((cameraInfo.orientation - orientation) + 360) % 360;
            } else {
                i3 = (cameraInfo.orientation + orientation) % 360;
            }
        }
        parameters.setRotation(i3);
    }

    public synchronized void stopPreview() {
        try {
            this.cameraOperateCount--;
            this.mCamera.setPreviewCallback(null);
            this.mCamera.stopPreview();
            this.mCamera.release();
            this.mCamera = null;
            this.mCurCameraId = -1;
        } catch (Exception e) {
            LogUtils.loge("CameraHolder", "release failed.");
        }
        return;
    }

    public int getBackCameraId() {
        return this.backId;
    }

    public int getFaceCameraId() {
        return this.faceId;
    }
}
