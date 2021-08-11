package com.sky.media.kit.record;

public interface RecordListener {
    void onRecordStart();

    void onRecordSuccess(boolean z);

    void onRecordStop();
}
