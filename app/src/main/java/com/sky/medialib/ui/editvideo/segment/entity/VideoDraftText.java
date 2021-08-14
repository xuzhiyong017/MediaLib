package com.sky.medialib.ui.editvideo.segment.entity;

import java.io.Serializable;
import java.util.Arrays;

public class VideoDraftText implements Serializable {
    private static final long serialVersionUID = 42;
    public int color;
    public float[] matrix;
    public String text;

    public String toString() {
        return "VideoDraftText{text='" + this.text + '\'' + ", color=" + this.color + ", matrix=" + Arrays.toString(this.matrix) + '}';
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof VideoDraftText)) {
            return false;
        }
        VideoDraftText videoDraftText = (VideoDraftText) obj;
        if (this.color != videoDraftText.color) {
            return false;
        }
        if (this.text != null) {
            if (!this.text.equals(videoDraftText.text)) {
                return false;
            }
        } else if (videoDraftText.text != null) {
            return false;
        }
        return Arrays.equals(this.matrix, videoDraftText.matrix);
    }

    public int hashCode() {
        return ((((this.text != null ? this.text.hashCode() : 0) * 31) + this.color) * 31) + Arrays.hashCode(this.matrix);
    }
}
