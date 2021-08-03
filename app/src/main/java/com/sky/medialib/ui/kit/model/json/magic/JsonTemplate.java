package com.sky.medialib.ui.kit.model.json.magic;

import java.io.Serializable;
import java.util.List;

public class JsonTemplate implements Serializable {
    public static final String TAG_TYPE_HOT = "2";
    public static final String TAG_TYPE_NEW = "1";
    public static final String TAG_TYPE_NORMAL = "0";
    public static final String TAG_TYPE_RECOMMEND = "3";
    private static final long serialVersionUID = 0;
    public String icon_large;
    public String icon_small;
    public List<JsonMirror> mirrors;
    public String mtime;
    public String name;
    public String status;
    public String tag_type = "0";
    public int tid;

    public String toString() {
        return "JsonTemplate{tid=" + this.tid + ", name='" + this.name + '\'' + ", mirrors=" + this.mirrors + '}';
    }
}
