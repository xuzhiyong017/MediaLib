package com.sky.media.kit.face;

public interface IFaceDetect {
    void destroy();

    Face[] trackBitmap(byte[] bArr, int i, int i2, int i3);

    Face[] trackCamera(byte[] bArr, int i, int i2, int i3);

    Face[] trackVideo(byte[] bArr, int i, int i2, int i3);
}
