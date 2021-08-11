package com.sky.media.kit.util;

import android.os.Build;

public class DeviceUtil {
    public static boolean isHM() {
        return "HM NOTE 1LTE".equals(Build.MODEL);
    }

    public static boolean isMiNote() {
        return "m1 note".equals(Build.MODEL);
    }

    public static boolean isHWP9() {
        return "EVA-AL00".equals(Build.MODEL);
    }

    public static boolean isHWP10() {
        return "VTR-AL00".equals(Build.MODEL);
    }

    public static boolean isRYV9() {
        return "DUK-TL30".equals(Build.MODEL);
    }

    public static boolean isRY6() {
        return "H60-L02".equals(Build.MODEL);
    }

    public static boolean isMateX() {
        return "Moto X Pro".equals(Build.MODEL);
    }
}
