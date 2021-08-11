package com.sky.media.kit.record.encoder;

import android.media.MediaCodec;
import android.media.MediaFormat;

import com.sky.media.kit.util.CodecUtil;
import com.sky.media.kit.util.MediaHelper;
import com.weibo.mediakit.util.YuvWrapper;

import java.io.IOException;

class MediaVideoEncoder extends MediaEncoder {

    private final int width;
    private final int height;
    private YuvWrapper mYuvWrapper;
    private int colorFormat;
    private byte[] outputBuffer;

    MediaVideoEncoder(MediaMuxerWrapper mediaMuxerWrapper, int width, int height) {
        super(mediaMuxerWrapper);
        this.width = width;
        this.height = height;
        outputBuffer = new byte[(((this.width * this.height) * 3) / 2)];
    }

    void ColorCovertToEncode(byte[] bArr) {
        switch (this.colorFormat) {
            case 19:
                this.mYuvWrapper.rgbToYuv420(bArr, this.width, this.height, this.outputBuffer);
                break;
            case 21:
                this.mYuvWrapper.rgbToI420sp(bArr, this.width, this.height, this.outputBuffer);
                break;
            default:
                return;
        }
        encode(this.outputBuffer, this.outputBuffer.length, getPTSUs());
        frameAvailableSoon();
    }

    protected void prepare() throws Exception {
        this.mTrackIndex = -1;
        this.mIsEOS = false;
        this.mMuxerStarted = false;
        this.mYuvWrapper = new YuvWrapper(this.width, this.height);
        this.colorFormat = CodecUtil.getColorFormatByType(CodecUtil.getMediaCodecInfo(MediaFormat.MIMETYPE_VIDEO_AVC), "video/avc");
        if (this.colorFormat == 0) {
            throw new IOException("couldn't find a support color format");
        }
        MediaFormat a = MediaHelper.getVideoFormat(this.width, this.height, this.colorFormat);
        this.mMediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
        this.mMediaCodec.configure(a, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        this.mMediaCodec.start();
    }
}
