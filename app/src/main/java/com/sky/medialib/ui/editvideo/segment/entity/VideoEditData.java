package com.sky.medialib.ui.editvideo.segment.entity;


import com.sky.medialib.ui.editvideo.process.VideoProcessExt;
import com.sky.medialib.ui.editvideo.segment.listener.IData;
import com.sky.medialib.ui.editvideo.segment.listener.IDataChangedListener;
import com.sky.medialib.ui.kit.model.Music;

import java.util.ArrayList;
import java.util.List;

public class VideoEditData implements IData {

    public VideoProcessExt processExt;
    public boolean jumpInputActivity;
    public boolean isShare;
    public boolean needPlay;
    private VideoDraft mVideoDraft;
    private List<IDataChangedListener> iDataChangedListeners = new ArrayList();

    public VideoEditData(VideoDraft videoDraft) {
        this.mVideoDraft = videoDraft;
    }

    public void addDataChangedListener(IDataChangedListener iDataChangedListener) {
        if (!this.iDataChangedListeners.contains(iDataChangedListener)) {
            this.iDataChangedListeners.add(iDataChangedListener);
        }
    }

    public void removeDataChangedListener(IDataChangedListener iDataChangedListener) {
        if (this.iDataChangedListeners.contains(iDataChangedListener)) {
            this.iDataChangedListeners.remove(iDataChangedListener);
        }
    }

    public void notifyChange() {
        for (IDataChangedListener onDataChanged : this.iDataChangedListeners) {
            onDataChanged.onDataChanged(this);
        }
    }

    public String getVideoPath() {
        return this.mVideoDraft.videoPath;
    }

    public void setVideoPath(String str) {
        this.mVideoDraft.videoPath = str;
        notifyChange();
    }

    public String getVideoReversePath() {
        return this.mVideoDraft.videoReversePath;
    }

    public void setVideoReversePath(String str) {
        this.mVideoDraft.videoReversePath = str;
        notifyChange();
    }

    public boolean isKeepVoice() {
        return this.mVideoDraft.isKeepVoice;
    }

    public void setKeepVoice(boolean z) {
        this.mVideoDraft.isKeepVoice = z;
        notifyChange();
    }

    public boolean isCameraMusic() {
        return this.mVideoDraft.isCameraMusic;
    }

    public void isCameraMusic(boolean z) {
        this.mVideoDraft.isCameraMusic = z;
        notifyChange();
    }

    public Music getMusic() {
        return this.mVideoDraft.music;
    }

    public void setMusic(Music music) {
        this.mVideoDraft.music = music;
        notifyChange();
    }

    public String getMusicPath() {
        return this.mVideoDraft.musicPath;
    }

    public void setMusicPath(String str) {
        this.mVideoDraft.musicPath = str;
        notifyChange();
    }

    public List<VideoDraftText> getWaterMarkList() {
        return this.mVideoDraft.watermarks;
    }

    public void setWaterMarkList(List<VideoDraftText> list) {
        this.mVideoDraft.watermarks = list;
        notifyChange();
    }

    public void setEffectList(List<VideoDraftEffect> list) {
        this.mVideoDraft.effects = list;
        notifyChange();
    }

    public boolean isReverse() {
        return this.mVideoDraft.isReverse;
    }

    public void setReverse(boolean z) {
        this.mVideoDraft.isReverse = z;
        notifyChange();
    }

    public int getFilterId() {
        return this.mVideoDraft.filterId;
    }

    public void setFilterId(int i) {
        this.mVideoDraft.filterId = i;
        notifyChange();
    }

    public int getWhitenLevel() {
        return this.mVideoDraft.whiteLevel;
    }

    public void setWhiteLevel(int i) {
        this.mVideoDraft.whiteLevel = i;
        notifyChange();
    }

    public int getSkinLevel() {
        return this.mVideoDraft.skinLevel;
    }

    public void setSkinLevel(int i) {
        this.mVideoDraft.skinLevel = i;
        notifyChange();
    }

    public int getEyesLevel() {
        return this.mVideoDraft.eyesLevel;
    }

    public void setEyesLevel(int i) {
        this.mVideoDraft.eyesLevel = i;
        notifyChange();
    }

    public int getFaceLevel() {
        return this.mVideoDraft.faceLevel;
    }

    public void setFaceLevel(int i) {
        this.mVideoDraft.faceLevel = i;
        notifyChange();
    }

    public String getMessage() {
        return this.mVideoDraft.message;
    }

    public void setMessage(String str) {
        this.mVideoDraft.message = str;
        notifyChange();
    }

    public String getTopic() {
        return this.mVideoDraft.topic;
    }

    public void getTopic(String str) {
        this.mVideoDraft.topic = str;
        notifyChange();
    }

    public void setTopicList(List<String> list) {
        this.mVideoDraft.topics = list;
        notifyChange();
    }

    public int getShareType() {
        return this.mVideoDraft.shareType;
    }

    public void setShareType(int i) {
        this.mVideoDraft.shareType = i;
        notifyChange();
    }

    public VideoDraft getVideoDraft() {
        return this.mVideoDraft;
    }

    public String toString() {
        return this.mVideoDraft.toString();
    }
}
