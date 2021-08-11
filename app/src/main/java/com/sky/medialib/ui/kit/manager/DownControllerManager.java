package com.sky.medialib.ui.kit.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


import com.sky.media.kit.BaseMediaApplication;
import com.sky.medialib.ui.kit.download.IDownController;
import com.sky.medialib.util.NetworkUtil;

import java.util.ArrayList;
import java.util.Iterator;

public class DownControllerManager {
    private static DownControllerManager downControllerManager = new DownControllerManager();
    private BroadcastReceiver statusChangeReceiver = new NetWorkStatusChangeReceiver();
    private ArrayList<IDownController> arrayList = new ArrayList();

    class NetWorkStatusChangeReceiver extends BroadcastReceiver {
        NetWorkStatusChangeReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (NetworkUtil.isWifiConnected(context)) {
                DownControllerManager.this.onWifiConnected();
            } else if (NetworkUtil.isMobileConnected(context)) {
                DownControllerManager.this.onMobileConnected();
            } else {
                DownControllerManager.this.noConnected();
            }
        }
    }

    private DownControllerManager() {
    }

    public static DownControllerManager getInstance() {
        return downControllerManager;
    }

    public void initDownList() {
//        this.arrayList.add(FiltersDataManager.getInstances());
//        this.arrayList.add(MagicsDataManager.getInstance());
        BaseMediaApplication.sContext.registerReceiver(this.statusChangeReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        Iterator it = this.arrayList.iterator();
        while (it.hasNext()) {
            ((IDownController) it.next()).onDownInit();
        }
    }

    private void onWifiConnected() {
        Iterator it = this.arrayList.iterator();
        while (it.hasNext()) {
            ((IDownController) it.next()).startDownLoad();
        }
    }

    private void onMobileConnected() {
        Iterator it = this.arrayList.iterator();
        while (it.hasNext()) {
            IDownController iDownController = (IDownController) it.next();
            if (iDownController.forceDown()) {
                iDownController.startDownLoad();
            } else {
                iDownController.stopDownLoad();
            }
        }
    }

    private void noConnected() {
        Iterator it = this.arrayList.iterator();
        while (it.hasNext()) {
            ((IDownController) it.next()).stopDownLoad();
        }
    }
}
