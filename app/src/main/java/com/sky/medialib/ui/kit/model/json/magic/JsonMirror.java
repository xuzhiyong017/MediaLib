package com.sky.medialib.ui.kit.model.json.magic;

import com.sky.medialib.util.FileUtil;
import com.sky.medialib.util.Storage;

import java.io.Serializable;

public class JsonMirror implements Serializable {
    public static final String STATUS_OFFLINE = "0";
    public static final String STATUS_ONLINE = "1";
    private static final String SUFFIX_CACHE = ".zip";
    private static final String SUFFIX_TMP = ".tmp";
    private static final long serialVersionUID = 0;
    public int mid;
    public String mtime;
    public String status = "1";
    public int tid;
    public String zip_url;

    public String toString() {
        return "JsonMirror{mid=" + this.mid + ", tid=" + this.tid + ", zip_url='" + this.zip_url + '\'' + '}';
    }

    public String getCachePath() {
        return Storage.getFilePathByType(22) + this.tid + "/" + this.mid + ".zip";
    }

    public String getCacheUnzipDirPath() {
        return Storage.getFilePathByType(22) + this.tid + "/" + this.mid;
    }

    public String getTmpPath() {
        return Storage.getFilePathByType(22) + this.tid + "/" + this.mid + ".tmp";
    }

    public String getDownloadUrl() {
        return this.zip_url;
    }

    public boolean saveTmp2Cache() {
        return FileUtil.INSTANCE.renameTo(getTmpPath(), getCachePath());
    }

    public boolean haveCache() {
        return FileUtil.INSTANCE.exists(getCachePath());
    }
}
