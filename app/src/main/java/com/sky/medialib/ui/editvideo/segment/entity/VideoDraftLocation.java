package com.sky.medialib.ui.editvideo.segment.entity;

import java.io.Serializable;

public class VideoDraftLocation implements Serializable {
    private static final long serialVersionUID = 42;
    public double latitude;
    public double longitude;

    public String toString() {
        return "VideoDraftLocation{latitude=" + this.latitude + ", longitude=" + this.longitude + '}';
    }
}
