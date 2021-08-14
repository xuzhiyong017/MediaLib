package com.sky.medialib.ui.editvideo.segment.entity;

import android.text.TextUtils;


import com.sky.medialib.ui.kit.model.Music;

import java.io.Serializable;
import java.util.List;

public class VideoDraft implements Serializable {
    public static final int TYPE_STORY = 1;
    public static final int TYPE_WEIBO = 0;
    private static final long serialVersionUID = 42;
    public List<VideoDraftEffect> effects;
    public String extra;
    public int eyesLevel;
    public int faceLevel;
    public int filterId;
    public long id;
    public boolean isCameraMusic;
    public boolean isKeepVoice = true;
    public boolean isReverse;
    public VideoDraftLocation location;
    public String message = "";
    public Music music;
    public String musicPath;
    public int shareType = 0;
    public int skinLevel;
    public String topic;
    public List<String> topics;
    public String uid;
    public long updateTime;
    public String videoPath;
    public String videoReversePath;
    public List<VideoDraftText> watermarks;
    public int whiteLevel;

    public String toString() {
        return "VideoDraft{id=" + this.id + ", uid='" + this.uid + '\'' + ", updateTime=" + this.updateTime + ", extra='" + this.extra + '\'' + ", videoPath='" + this.videoPath + '\'' + ", videoReversePath='" + this.videoReversePath + '\'' + ", location=" + this.location + ", isKeepVoice=" + this.isKeepVoice + ", isCameraMusic=" + this.isCameraMusic + ", music=" + this.music + ", musicPath='" + this.musicPath + '\'' + ", watermarks=" + this.watermarks + ", effects=" + this.effects + ", isReverse=" + this.isReverse + ", filterId=" + this.filterId + ", whiteLevel=" + this.whiteLevel + ", skinLevel=" + this.skinLevel + ", eyesLevel=" + this.eyesLevel + ", faceLevel=" + this.faceLevel + ", message='" + this.message + '\'' + ", topic='" + this.topic + '\'' + ", topics=" + this.topics + ", shareType=" + this.shareType + '}';
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof VideoDraft)) {
            return false;
        }
        VideoDraft videoDraft = (VideoDraft) obj;
        if (this.id != videoDraft.id || this.updateTime != videoDraft.updateTime || this.isKeepVoice != videoDraft.isKeepVoice || this.isCameraMusic != videoDraft.isCameraMusic || this.isReverse != videoDraft.isReverse || this.filterId != videoDraft.filterId || this.whiteLevel != videoDraft.whiteLevel || this.skinLevel != videoDraft.skinLevel || this.eyesLevel != videoDraft.eyesLevel || this.faceLevel != videoDraft.faceLevel || this.shareType != videoDraft.shareType) {
            return false;
        }
        if (this.extra != null) {
            if (!this.extra.equals(videoDraft.extra)) {
                return false;
            }
        } else if (videoDraft.extra != null) {
            return false;
        }
        if (this.videoPath != null) {
            if (!this.videoPath.equals(videoDraft.videoPath)) {
                return false;
            }
        } else if (videoDraft.videoPath != null) {
            return false;
        }
        if (this.videoReversePath != null) {
            if (!this.videoReversePath.equals(videoDraft.videoReversePath)) {
                return false;
            }
        } else if (videoDraft.videoReversePath != null) {
            return false;
        }
        if (this.music != null) {
            if (!this.music.equals(videoDraft.music)) {
                return false;
            }
        } else if (videoDraft.music != null) {
            return false;
        }
        if (this.musicPath != null) {
            if (!this.musicPath.equals(videoDraft.musicPath)) {
                return false;
            }
        } else if (videoDraft.musicPath != null) {
            return false;
        }
        if (this.watermarks != null) {
            if (!this.watermarks.equals(videoDraft.watermarks)) {
                return false;
            }
        } else if (videoDraft.watermarks != null) {
            return false;
        }
        if (this.effects != null) {
            if (!this.effects.equals(videoDraft.effects)) {
                return false;
            }
        } else if (videoDraft.effects != null) {
            return false;
        }
        if (this.message != null) {
            if (!this.message.equals(videoDraft.message)) {
                return false;
            }
        } else if (videoDraft.message != null) {
            return false;
        }
        if (this.topic != null) {
            if (!this.topic.equals(videoDraft.topic)) {
                return false;
            }
        } else if (videoDraft.topic != null) {
            return false;
        }
        if (this.topics != null) {
            z = this.topics.equals(videoDraft.topics);
        } else if (videoDraft.topics != null) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        int i;
        int i2 = 1;
        int i3 = 0;
        int hashCode = ((!TextUtils.isEmpty(this.extra) ? this.extra.hashCode() : 0) + (((((int) (this.id ^ (this.id >>> 32))) * 31) + ((int) (this.updateTime ^ (this.updateTime >>> 32)))) * 31)) * 31;
        if (TextUtils.isEmpty(this.videoPath)) {
            i = 0;
        } else {
            i = this.videoPath.hashCode();
        }
        hashCode = (i + hashCode) * 31;
        if (TextUtils.isEmpty(this.videoReversePath)) {
            i = 0;
        } else {
            i = this.videoReversePath.hashCode();
        }
        hashCode = (i + hashCode) * 31;
        if (this.isKeepVoice) {
            i = 1;
        } else {
            i = 0;
        }
        hashCode = (i + hashCode) * 31;
        if (this.isCameraMusic) {
            i = 1;
        } else {
            i = 0;
        }
        hashCode = (i + hashCode) * 31;
        if (this.music != null) {
            i = this.music.hashCode();
        } else {
            i = 0;
        }
        hashCode = (i + hashCode) * 31;
        if (TextUtils.isEmpty(this.musicPath)) {
            i = 0;
        } else {
            i = this.musicPath.hashCode();
        }
        hashCode = (i + hashCode) * 31;
        if (this.watermarks == null || this.watermarks.isEmpty()) {
            i = 0;
        } else {
            i = this.watermarks.hashCode();
        }
        hashCode = (i + hashCode) * 31;
        if (this.effects == null || this.effects.isEmpty()) {
            i = 0;
        } else {
            i = this.effects.hashCode();
        }
        i = (i + hashCode) * 31;
        if (!this.isReverse) {
            i2 = 0;
        }
        i2 = (((((((((((i + i2) * 31) + this.filterId) * 31) + this.whiteLevel) * 31) + this.skinLevel) * 31) + this.eyesLevel) * 31) + this.faceLevel) * 31;
        if (TextUtils.isEmpty(this.message)) {
            i = 0;
        } else {
            i = this.message.hashCode();
        }
        i2 = (i + i2) * 31;
        if (TextUtils.isEmpty(this.topic)) {
            i = 0;
        } else {
            i = this.topic.hashCode();
        }
        i = (i + i2) * 31;
        if (!(this.topics == null || this.topics.isEmpty())) {
            i3 = this.topics.hashCode();
        }
        return ((i + i3) * 31) + this.shareType;
    }
}
