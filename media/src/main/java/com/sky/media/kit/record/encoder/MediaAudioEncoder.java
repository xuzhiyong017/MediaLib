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
        mAudioCapturer.init();
        mAudioCapturer.setOnRecordDataCallback((onRecordDataCallback) this);
        mTrackIndex = -1;
        mIsEOS = false;
        mMuxerStarted = false;
        int sampleRate = mAudioCapturer.getSimpleRate();
        int channelCount = mAudioCapturer.getChannelCount();
        int f = mAudioCapturer.getEncoderBit();
        MediaFormat format = MediaHelper.getAudioFormat(sampleRate, channelCount);
        mMediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC);
        mMediaCodec.configure(format, null, null,  MediaCodec.CONFIGURE_FLAG_ENCODE);
        mMediaCodec.start();
        if (!TextUtils.isEmpty(mAudioPath)) {
            mAudioEncoder = new AudioEncoder(mAudioPath);
            mAudioEncoder.startAudioEncoder(sampleRate, channelCount);
        }
        if (mSoundTouch != null) {
            mSoundTouch.initParams(channelCount, sampleRate, f / 8);
        }
    }

    protected void startRecording() {
        super.startRecording();
        mAudioCapturer.startCapture();
    }

    protected void stopRecording() {
        mAudioCapturer.stop();
        super.stopRecording();
    }

    protected void release() {
        super.release();
        if (mSoundTouch != null) {
            mSoundTouch.release();
        }
        if (mAudioEncoder != null) {
            mAudioEncoder.release();
        }
    }

    public void recordData(byte[] bArr) {
        long pts;
        if (mSoundTouch != null) {
            int i;
            byte[] bArr2 = new byte[4096];
            mSoundTouch.putSamples(bArr);
            ArrayList arrayList = new ArrayList();
            int b;
            do {
                b = mSoundTouch.receiveSamples(bArr2);
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
            if (mAudioEncoder != null) {
                mAudioEncoder.encodeData(bArr3, pts);
            }
            encode(bArr, bArr.length, pts);
            frameAvailableSoon();
            return;
        }
        pts = getPTSUs();
        if (mAudioEncoder != null) {
            mAudioEncoder.encodeData(bArr, pts);
        }
        encode(bArr, bArr.length, pts);
        frameAvailableSoon();
    }
}
