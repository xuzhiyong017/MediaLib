package com.sky.medialib.ui.kit.download;


import androidx.collection.ArrayMap;

public class DownFileParams {
    private ArrayMap<String, Object> params;

    public DownFileParams() {
        this.params = null;
        this.params = new ArrayMap();
    }

    public DownFileParams(String str, Object obj) {
        this();
        put(str, obj);
    }

    public Object get(String str) {
        return this.params.get(str);
    }

    public String getString(String str) {
        Object obj = get(str);
        return obj == null ? null : obj.toString();
    }

    public boolean has(String str) {
        return this.params.containsKey(str);
    }

    public void put(String str, Object obj) {
        this.params.put(str, obj);
    }

}
