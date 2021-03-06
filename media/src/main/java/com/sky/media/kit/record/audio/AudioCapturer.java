package com.sky.media.kit.record.audio;

import static android.media.AudioFormat.ENCODING_PCM_8BIT;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.sky.media.image.core.util.LogUtils;
import com.sky.media.kit.util.MediaHelper;


public class AudioCapturer {
    private AudioRecord mAudioRecord;
    private int mSimpleRate = MediaHelper.DEFAULT_SIMPLE_HZ;
    private int bufferSizeInBytes;
    private Thread captureThread;
    private boolean captureing = false;
    private volatile boolean f12079f = false;
    private byte[] buffer = new byte[2048];
    private onRecordDataCallback mOnRecordDataCallback;

    private class AudioRecordImp implements Runnable {
        private AudioRecordImp() {
        }

        public void run() {
            while (!AudioCapturer.this.f12079f) {
                int read = AudioCapturer.this.mAudioRecord.read(AudioCapturer.this.buffer, 0, AudioCapturer.this.buffer.length);
                if (read == -3) {
                    Log.e("AudioCapturer", "Error ERROR_INVALID_OPERATION");
                } else if (read == -2) {
                    Log.e("AudioCapturer", "Error ERROR_BAD_VALUE");
                } else if (AudioCapturer.this.mOnRecordDataCallback != null) {
                    AudioCapturer.this.mOnRecordDataCallback.recordData(AudioCapturer.this.buffer);
                }
            }
        }
    }

    public interface onRecordDataCallback {
        void recordData(byte[] bArr);
    }

    public void init() {
        this.mAudioRecord = createAudioRecord();
        LogUtils.logd("AudioCapturer","AudioCapturer "+mAudioRecord.getState());
    }

    public boolean startCapture() {
        if (this.captureing) {
            Log.e("AudioCapturer", "Capture already started !");
            return false;
        } else if (this.mAudioRecord == null || this.mAudioRecord.getState() == 0) {
            Log.e("AudioCapturer", "AudioRecord initialize failed !");
            return false;
        } else {
            this.mAudioRecord.startRecording();
            this.f12079f = false;
            this.captureThread = new Thread(new AudioRecordImp());
            this.captureThread.start();
            this.captureing = true;
            Log.d("AudioCapturer", "Start audio capture success.");
            return true;
        }
    }

    public void stop() {
        if (this.captureing) {
            this.f12079f = true;
            this.captureing = false;
            try {
                this.captureThread.interrupt();
                this.captureThread.join(1000);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            if (this.mAudioRecord != null && this.mAudioRecord.getRecordingState() == 3) {
                this.mAudioRecord.stop();
            }
            this.mOnRecordDataCallback = null;
            Log.d("AudioCapturer", "Stop audio capture success.");
        }
    }

    public int getSimpleRate() {
        return this.mSimpleRate;
    }

    public int getChannelCount() {
        if (this.mAudioRecord != null) {
            return getChannelCountByConfig(this.mAudioRecord.getChannelConfiguration());
        }
        throw new RuntimeException("AudioRecord has not initialized.");
    }

    public int getChannelCountByConfig(int i) {
        switch (i) {
            case 16:
                return 1;
            default:
                return 2;
        }
    }

    public int getEncoderBit() {
        if (this.mAudioRecord != null) {
            return getPCMBitByFormat(this.mAudioRecord.getAudioFormat());
        }
        throw new RuntimeException("AudioRecord has not initialized.");
    }

    public int getPCMBitByFormat(int i) {
        switch (i) {
            case ENCODING_PCM_8BIT:
                return 8;
            default:
                return 16;
        }
    }

    public void setOnRecordDataCallback(onRecordDataCallback onRecordDataCallback) {
        this.mOnRecordDataCallback = onRecordDataCallback;
    }

    private static final int[] AUDIO_FORMAT = new int[]{AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO};
    private static final int[] AUDIO_ENCODEING_FORMAT = new int[]{AudioFormat.ENCODING_PCM_16BIT, AudioFormat.ENCODING_PCM_8BIT};
    private static final int[] AUDIO_SAMPLERATE = new int[]{44100, 22050, 11025, 8000};

    private AudioRecord createAudioRecord() {

        for (int sampleRate : AUDIO_SAMPLERATE) {
            for (int source : AUDIO_ENCODEING_FORMAT) {
                for (int formatType : AUDIO_FORMAT) {
                    try {
                        Log.d("AudioCapturer", "Attempting rate:" + sampleRate + "Hz, bits:" + getPCMBitByFormat(source) + ", channel:" + getChannelCountByConfig(formatType));
                        int minBufferSize = AudioRecord.getMinBufferSize(sampleRate, formatType, source);
                        if (minBufferSize != -2) {
                            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, formatType, source, minBufferSize);
                            if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                                this.mSimpleRate = sampleRate;
                                this.bufferSizeInBytes = minBufferSize;
                                return audioRecord;
                            }
                        } else {
                            continue;
                        }
                    } catch (Throwable e) {
                        Log.e("AudioCapturer", "Init AudioRecord Error." + Log.getStackTraceString(e));
                    }
                }
            }
        }
        return null;
    }
}
