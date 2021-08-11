package com.sky.medialib.ui.kit.download;

public interface IDownController {
    void onDownInit();

    void startDownLoad();

    void stopDownLoad();

    boolean forceDown();
}
