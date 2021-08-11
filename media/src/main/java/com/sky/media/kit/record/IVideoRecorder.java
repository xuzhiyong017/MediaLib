package com.sky.media.kit.record;

public interface IVideoRecorder {
    void setRecordListener(RecordListener recordListener);
    void enableAudio(boolean enableAudio);
    void onEncodeData(byte[] bArr);
    boolean prepared();
    void recordStop();
    boolean isRecording();
}
