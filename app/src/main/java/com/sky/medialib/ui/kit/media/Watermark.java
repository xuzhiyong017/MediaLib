package com.sky.medialib.ui.kit.media;

public class Watermark {

    public String path;
    public int x;
    public int y;
    public float scale = 1.0f;

    public String toString() {
        return "Watermark{path='" + this.path + '\'' + ", x=" + this.x + ", y=" + this.y + ", scale=" + this.scale + '}';
    }
}
