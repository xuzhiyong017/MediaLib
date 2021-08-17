package com.sky.medialib.util;

/**
 * @author: xuzhiyong
 * @date: 2021/8/16  下午5:44
 * @Email: 18971269648@163.com
 * @description:
 */
public class DateUtil {

    public static String getTimeStemp(int i) {
        int i2 = (i / 1000) / 1000;
        if (i2 < 10) {
            return "00:0" + i2;
        }
        return "00:" + i2;
    }
}
