package com.sky.medialib.ui.kit.model;

import android.text.TextUtils;

import java.io.Serializable;

public class GalleryModel implements Serializable, Comparable<GalleryModel> {
    private static final long serialVersionUID = 0;
    private String coverPath;
    private long duration;
    private String filePath;
    private long modifyTime;
    private int videoHeight;
    private int videoWidth;


    public GalleryModel(){

    }

    public GalleryModel(String str, String str2) {
        this.filePath = str;
        this.coverPath = str2;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String str) {
        this.filePath = str;
    }

    public String getCoverPath() {
        return this.coverPath;
    }

    public void setCoverPath(String str) {
        this.coverPath = str;
    }

    public long getModifyTime() {
        return this.modifyTime;
    }

    public void setModifyTime(long j) {
        this.modifyTime = j;
    }

    public String getDuration() {
        if (this.duration < 0) {
            return "0";
        }
        long j = this.duration / 1000;
        if (this.duration % 1000 > 500) {
            j++;
        }
        long j2 = j / 60;
        j %= 60;
        return j2 + ":" + (j < 10 ? "0" + j : Long.valueOf(j));
    }

    public int getVideoWidth() {
        return this.videoWidth;
    }

    public void setVideoWidth(int i) {
        this.videoWidth = i;
    }

    public int getVideoHeight() {
        return this.videoHeight;
    }

    public void setVideoHeight(int i) {
        this.videoHeight = i;
    }

    public void setDuration(long j) {
        this.duration = j;
    }

    public boolean isVideo() {
        return !TextUtils.isEmpty(this.filePath) && this.filePath.endsWith(".mp4");
    }

    public int compareTo(GalleryModel galleryModel) {
        if (galleryModel.modifyTime > this.modifyTime) {
            return 1;
        }
        if (galleryModel.modifyTime < this.modifyTime) {
            return -1;
        }
        return 0;
    }
}
