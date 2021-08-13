package com.sky.medialib.ui.kit.common.base.adapter;

import androidx.collection.ArrayMap;

import java.util.Map;

class TypeUtil {
    private Map<Object, Integer> ItemMap = new ArrayMap();

    TypeUtil() {
    }

    int getTypeId(Object obj) {
        Integer num = (Integer) this.ItemMap.get(obj);
        if (num == null) {
            num = Integer.valueOf(this.ItemMap.size());
            this.ItemMap.put(obj, num);
        }
        return num.intValue();
    }
}
