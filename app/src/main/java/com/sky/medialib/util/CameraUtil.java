package com.sky.medialib.util;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.location.Location;

import java.util.List;

public class CameraUtil {

    public static boolean isSupport720(Parameters parameters) {
        Size previewSize = parameters.getPreviewSize();
        if (previewSize.width == 1280 && previewSize.height == 720) {
            return true;
        }
        List<Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        if (supportedPreviewSizes != null) {
            for (Size previewSize2 : supportedPreviewSizes) {
                if (previewSize2.width == 1280 && previewSize2.height == 720) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isSupport1080(Parameters parameters) {
        Size previewSize = parameters.getPreviewSize();
        if (previewSize.width == 1920 && previewSize.height == 1080) {
            return true;
        }
        List<Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        if (supportedPreviewSizes != null) {
            for (Size previewSize2 : supportedPreviewSizes) {
                if (previewSize2.width == 1920 && previewSize2.height == 1080) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void setLocation(Parameters parameters, Location location) {
        parameters.removeGpsData();
        parameters.setGpsTimestamp(System.currentTimeMillis() / 1000);
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            Object obj = (latitude == 0.0d && longitude == 0.0d) ? null : 1;
            if (obj != null) {
                parameters.setGpsLatitude(latitude);
                parameters.setGpsLongitude(longitude);
                parameters.setGpsProcessingMethod(location.getProvider().toUpperCase());
                if (location.hasAltitude()) {
                    parameters.setGpsAltitude(location.getAltitude());
                } else {
                    parameters.setGpsAltitude(0.0d);
                }
                if (location.getTime() != 0) {
                    parameters.setGpsTimestamp(location.getTime() / 1000);
                }
            }
        }
    }

    public static int getRotation(Activity activity) {
        switch (activity.getWindowManager().getDefaultDisplay().getRotation()) {
            case 1:
                return 90;
            case 2:
                return 180;
            case 3:
                return 270;
            default:
                return 0;
        }
    }

    public static int getDisplayOrientation(int activityRotation, int cameraId) {
        CameraInfo cameraInfo = new CameraInfo();
        Camera.getCameraInfo(cameraId, cameraInfo);
        if (cameraInfo.facing == 1) {
            return (360 - ((cameraInfo.orientation + activityRotation) % 360)) % 360;
        }
        return ((cameraInfo.orientation - activityRotation) + 360) % 360;
    }

    public static int getOrientation(int i, int i2) {
        Object obj = 1;
        if (i2 != -1) {
            int abs = Math.abs(i - i2);
            if (Math.min(abs, 360 - abs) < 50) {
                obj = null;
            }
        }
        if (obj != null) {
            return (((i + 45) / 90) * 90) % 360;
        }
        return i2;
    }
}
