package com.sky.medialib.ui.kit.model;

import com.google.gson.annotations.SerializedName;
import com.sky.medialib.ui.kit.download.IDownloadable;
import com.sky.medialib.util.FileUtil;
import com.sky.medialib.util.Storage;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Music implements IDownloadable, Serializable {
    private static final long serialVersionUID = 0;
    public List<String> artist;
    public int duration;
    public String id;
    public String name;
    public String singer;
    public String photo;
    public String tag;
    @SerializedName("a_url")
    public String url;

    public String toString() {
        return "Music{id='" + this.id + '\'' + ", name='" + this.name + '\'' + ", photo='" + this.photo + '\'' + ", url='" + this.url + '\'' + ", duration=" + this.duration + '}';
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Music)) {
            return false;
        }
        Music music = (Music) obj;
        if (!Objects.equals(this.id, music.id)) {
            return false;
        }
        if (this.name != null) {
            return this.name.equals(music.name);
        }
        if (music.name != null) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int hashCode;
        int i = 0;
        if (this.id != null) {
            hashCode = this.id.hashCode();
        } else {
            hashCode = 0;
        }
        hashCode *= 31;
        if (this.name != null) {
            i = this.name.hashCode();
        }
        return hashCode + i;
    }

    public String getDownloadUrl() {
        return this.url;
    }

    public String getTmpPath() {
        return Storage.getFilePathByType(4) + this.id + ".temp";
    }

    public boolean saveTmp2Cache() {
        return FileUtil.INSTANCE.renameTo(getTmpPath(), getFinalPath());
    }

    public boolean haveCache() {
        return FileUtil.INSTANCE.exists(getFinalPath());
    }

    public String getFinalPath() {
        return Storage.getFilePathByType(12) + this.id + ".mp3";
    }
}
