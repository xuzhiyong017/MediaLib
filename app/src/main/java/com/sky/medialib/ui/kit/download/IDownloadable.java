package com.sky.medialib.ui.kit.download;

import java.io.Serializable;

public interface IDownloadable extends Serializable {
    String getDownloadUrl();

    String getTmpPath();

    boolean haveCache();

    boolean saveTmp2Cache();
}
