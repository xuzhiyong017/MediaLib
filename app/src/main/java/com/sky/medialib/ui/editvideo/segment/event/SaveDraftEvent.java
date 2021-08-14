package com.sky.medialib.ui.editvideo.segment.event;


import com.sky.medialib.ui.editvideo.segment.entity.VideoDraft;

public class SaveDraftEvent {
    public VideoDraft draft;

    public SaveDraftEvent(VideoDraft videoDraft) {
        this.draft = videoDraft;
    }
}
