package com.sky.media.kit.mediakit;

public class Metadata {
    public String mimeType;
    public int width;
    public int height;
    public long duration;
    public int rotation;
    public int numTracks;
    public int bitrate;

    public String toString() {
        return "Metadata{mimeType='" + this.mimeType + '\'' + ", width=" + this.width + ", height=" + this.height + ", duration=" + this.duration + ", rotation=" + this.rotation + ", tracks=" + this.numTracks + ", bitrate=" + this.bitrate + '}';
    }
}
