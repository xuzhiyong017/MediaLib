package com.sky.medialib.ui.editvideo.segment.entity;

import java.io.Serializable;
import java.util.List;

public class VideoDraftEffect implements Serializable {
    private static final long serialVersionUID = 42;
    public long end;
    public List<Integer> filterIds;
    public boolean isReverse;
    public long start;
    public long total;

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof VideoDraftEffect)) {
            return false;
        }
        VideoDraftEffect videoDraftEffect = (VideoDraftEffect) obj;
        if (this.start != videoDraftEffect.start || this.end != videoDraftEffect.end || this.total != videoDraftEffect.total || this.isReverse != videoDraftEffect.isReverse) {
            return false;
        }
        if (this.filterIds != null) {
            z = this.filterIds.equals(videoDraftEffect.filterIds);
        } else if (videoDraftEffect.filterIds != null) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        int i;
        int i2 = 0;
        int i3 = ((((((int) (this.start ^ (this.start >>> 32))) * 31) + ((int) (this.end ^ (this.end >>> 32)))) * 31) + ((int) (this.total ^ (this.total >>> 32)))) * 31;
        if (this.isReverse) {
            i = 1;
        } else {
            i = 0;
        }
        i = (i + i3) * 31;
        if (this.filterIds != null) {
            i2 = this.filterIds.hashCode();
        }
        return i + i2;
    }

    public String toString() {
        return "VideoDraftEffect{start=" + this.start + ", end=" + this.end + ", total=" + this.total + ", isReverse=" + this.isReverse + ", filterIds=" + this.filterIds + '}';
    }
}
