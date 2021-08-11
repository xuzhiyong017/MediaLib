package com.weibo.soundtouch;

public final class SoundTouch {

    private long handle = newInstance();
    private int mChannelCount;
    private int offset;

    private native void deleteInstance(long j);

    private native int flush(long j);

    private native long newInstance();

    private native void putSamples(byte[] bArr, int i, int i2, int i3, long j);

    private native int receiveSamples(byte[] bArr, int i, int i2, int i3, long j);

    private native void setChannels(int i, long j);

    private native void setPitchSemiTones(float f, long j);

    private native void setSampleRate(int i, long j);

    private native void setTempoChange(float f, long j);

    static {
        System.loadLibrary("soundtouch");
    }

    public static SoundTouch getInstance() {
        return new SoundTouch();
    }

    private SoundTouch() {
    }

    public synchronized void release() {
        deleteInstance(this.handle);
        this.handle = 0;
    }

    public void setTempo(float f) {
        setTempoChange(f, this.handle);
    }

    public void setPitch(float f) {
        setPitchSemiTones(f, this.handle);
    }

    public void initParams(int channelCount, int sampleRate, int i3) {
        this.mChannelCount = channelCount;
        this.offset = i3;
        setChannels(this.mChannelCount, this.handle);
        setSampleRate(sampleRate, this.handle);
    }

    public void putSamples(byte[] bArr) {
        putSamples(bArr, bArr.length, this.mChannelCount, this.offset, this.handle);
    }

    public int receiveSamples(byte[] bArr) {
        return receiveSamples(bArr, bArr.length, this.mChannelCount, this.offset, this.handle);
    }

    public void flush() {
        flush(this.handle);
    }
}
