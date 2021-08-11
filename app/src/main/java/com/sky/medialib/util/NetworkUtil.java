package com.sky.medialib.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sky.media.kit.BaseMediaApplication;


public class NetworkUtil {

    public static ConnectivityManager getConnectivityManager(Context context) {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public static boolean isNetConnected(Context context) {
        if (context == null) {
            context = BaseMediaApplication.sContext;
        }
        NetworkInfo activeNetworkInfo = NetworkUtil.getConnectivityManager(context).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isWifiConnected(Context context) {
        NetworkInfo activeNetworkInfo = NetworkUtil.getConnectivityManager(context).getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI && activeNetworkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public static boolean isMobileConnected(Context context) {
        NetworkInfo activeNetworkInfo = NetworkUtil.getConnectivityManager(context).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE && activeNetworkInfo.isConnected();
    }

    public static String getNetConnectName(Context context) {
        String str = "";
        ConnectivityManager a = NetworkUtil.getConnectivityManager(context);
        if (a == null) {
            return str;
        }
        NetworkInfo activeNetworkInfo = a.getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            return str;
        }
        if (activeNetworkInfo.getType() == 1) {
            return "WIFI";
        }
        if (activeNetworkInfo.getType() != 0) {
            return str;
        }
        str = activeNetworkInfo.getSubtypeName();
        switch (activeNetworkInfo.getSubtype()) {
            case 1:
            case 2:
            case 4:
            case 7:
            case 11:
                return "2G";
            case 3:
            case 5:
            case 6:
            case 8:
            case 9:
            case 10:
            case 12:
            case 14:
            case 15:
                return "3G";
            case 13:
                return "4G";
            default:
                if (str.equalsIgnoreCase("TD-SCDMA") || str.equalsIgnoreCase("WCDMA") || str.equalsIgnoreCase("CDMA2000")) {
                    return "3G";
                }
                return str;
        }
    }
}
