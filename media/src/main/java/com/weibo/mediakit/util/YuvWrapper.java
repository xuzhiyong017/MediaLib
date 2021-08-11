package com.weibo.mediakit.util;

public class YuvWrapper {
    private byte[] rgbBuffer;
    private byte[] yuvBuffer;

    private native void bgrToYuv(byte[] bArr, int i, int i2, byte[] bArr2);

    private native void nv21ToI420sp(byte[] bArr, int i, int i2, byte[] bArr2);

    private native void rgbToBgr(byte[] bArr, int i, int i2, byte[] bArr2);

    private native void yuv420spToYuv420(byte[] bArr, int i, int i2, byte[] bArr2);

    static {
        System.loadLibrary("wbyuv");
    }

    public YuvWrapper(int width, int height) {
        this.rgbBuffer = new byte[((width * height) * 4)];
        this.yuvBuffer = new byte[(((width * height) * 3) / 2)];
    }

    public void rgbToYuv420(byte[] bArr, int width, int height, byte[] bArr2) {
        rgbToBgr(bArr, width, height, this.rgbBuffer);
        bgrToYuv(this.rgbBuffer, width, height, this.yuvBuffer);
        yuv420spToYuv420(this.yuvBuffer, width, height, bArr2);
    }

    public void rgbToI420sp(byte[] bArr, int width, int height, byte[] bArr2) {
        rgbToBgr(bArr, width, height, this.rgbBuffer);
        bgrToYuv(this.rgbBuffer, width, height, this.yuvBuffer);
        nv21ToI420sp(this.yuvBuffer, width, height, bArr2);
    }
}
