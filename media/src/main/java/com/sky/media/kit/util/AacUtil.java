package com.sky.media.kit.util;

public class AacUtil {
    public static void addADTSHeader(byte[] bArr, int i, int i2, int i3) {
        int a = AacUtil.getFreqIndex(i2);
        bArr[0] = (byte) -1;
        bArr[1] = (byte) -7;
        bArr[2] = (byte) (((a << 2) + 64) + (i3 >> 2));
        bArr[3] = (byte) (((i3 & 3) << 6) + (i >> 11));
        bArr[4] = (byte) ((i & 2047) >> 3);
        bArr[5] = (byte) (((i & 7) << 5) + 31);
        bArr[6] = (byte) -4;
    }

    public static int getFreqIndex(int i) {
        switch (i) {
            case 7350:
                return 12;
            case 8000:
                return 11;
            case 11025:
                return 10;
            case 12000:
                return 9;
            case 16000:
                return 8;
            case 22050:
                return 7;
            case 24000:
                return 6;
            case 32000:
                return 5;
            case 48000:
                return 3;
            case 64000:
                return 2;
            case 88200:
                return 1;
            case 96000:
                return 0;
            default:
                return 4;
        }
    }
}
