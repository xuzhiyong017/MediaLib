package com.sky.media.kit.record.encoder;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.text.TextUtils;

import com.sky.media.kit.record.audio.AudioCapturer;
import com.sky.media.kit.record.audio.AudioCapturer.*;
import com.sky.media.kit.record.audio.AudioEncoder;
import com.sky.media.kit.util.MediaHelper;
import com.weibo.soundtouch.SoundTouch;

import java.util.ArrayList;

class MediaAudioEncoder extends MediaEncoder implements AudioCapturer.onRecordDataCallback {

    private AudioCapturer mAudioCapturer = new AudioCapturer();
    private AudioEncoder mAudioEncoder;
    private String mAudioPath;
    private SoundTouch mSoundTouch;

    MediaAudioEncoder(MediaMuxerWrapper mediaMuxerWrapper, String str, SoundTouch soundTouch) {
        super(mediaMuxerWrapper);
        this.mAudioPath = str;
        this.mSoundTouch = soundTouch;
    }

    protected void prepare() throws Exception {
        this.mAudioCapturer.init();
        this.mAudioCapturer.setOnRecordDataCallback((onRecordDataCallback) this);
        this.mTrackIndex = -1;
        this.mIsEOS = false;
        this.mMuxerStarted = false;
        int sampleRate = this.mAudioCapturer.getSimpleRate();
        int channelCount = this.mAudioCapturer.getChannelCount();
        int f = this.mAudioCapturer.getEncoderBit();
        MediaFormat format = MediaHelper.getAudioFormat(sampleRate, channelCount);
        this.mMediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC);
        this.mMediaCodec.configure(format, null, null,  MediaCodec.CONFIGURE_FLAG_ENCODE);
        this.mMediaCodec.start();
        if (!TextUtils.isEmpty(this.mAudioPath)) {
            this.mAudioEncoder = new AudioEncoder(this.mAudioPath);
            this.mAudioEncoder.startAudioEncoder(sampleRate, channelCount);
        }
        if (this.mSoundTouch != null) {
            this.mSoundTouch.initParams(channelCount, sampleRate, f / 8);
        }
    }

    protected void startRecording() {
        super.startRecording();
        this.mAudioCapturer.startCapture();
    }

    protected void stopRecording() {
        this.mAudioCapturer.stop();
        super.stopRecording();
    }

    protected void release() {
        super.release();
        if (this.mSoundTouch != null) {
            this.mSoundTouch.release();
        }
        if (this.mAudioEncoder != null) {
            this.mAudioEncoder.release();
        }
    }

    public void recordData(byte[] bArr) {
        long pts;
        if (this.mSoundTouch != null) {
            int i;
            byte[] bArr2 = new byte[4096];
            this.mSoundTouch.putSamples(bArr);
            ArrayList arrayList = new ArrayList();
            int b;
            do {
                b = this.mSoundTouch.receiveSamples(bArr2);
                for (i = 0; i < b; i++) {
                    arrayList.add(Byte.valueOf(bArr2[i]));
                }
            } while (b != 0);
            int size = arrayList.size();
            byte[] bArr3 = new byte[size];
            for (i = 0; i < size; i++) {
                bArr3[i] = ((Byte) arrayList.get(i)).byteValue();
            }
            pts = getPTSUs();
            if (this.mAudioEncoder != null) {
                this.mAudioEncoder.encodeData(bArr3, pts);
            }
            encode(bArr, bArr.length, pts);
            frameAvailableSoon();
            return;
        }
        pts = getPTSUs();
        if (this.mAudioEncoder != null) {
            this.mAudioEncoder.encodeData(bArr, pts);
        }
        encode(bArr, bArr.length, pts);
        frameAvailableSoon();
    }
}
