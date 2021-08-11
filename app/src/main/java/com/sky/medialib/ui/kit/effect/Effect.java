package com.sky.medialib.ui.kit.effect;

import com.sky.media.kit.render.sticker.Sticker.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Effect implements Serializable {
    private static final long serialVersionUID = 1;
    public String audio;
    public int count = 1;
    public List<Filter> filter;
    public String name;
    public int type;
    public int version;
    public Voice voice;

    public static class Filter implements Serializable {
        public static final int ACTION_HIDE_ALWAYS = 4;
        public static final int ACTION_HIDE_UNTIL_NOT_TRIGGER = 1;
        public static final int ACTION_SHOW_ALWAYS = 3;
        public static final int ACTION_SHOW_UNTIL_NOT_TRIGGER = 2;
        public static final int FILTER_BUFFING = 0;
        public static final int FILTER_CUSTOM = 5;
        public static final int FILTER_ENLARGE_EYE = 1;
        public static final int FILTER_SLIM_FACE = 2;
        public static final int FILTER_STICKER = 4;
        public static final int FILTER_STICKER_3D = 6;
        public static final int FILTER_STICKER_3D_ST = 7;
        public static final int FILTER_WHITENING = 3;
        public static final int TRIGGER_BROW_JUMP = 32;
        public static final int TRIGGER_EYE_BLINK = 2;
        public static final int TRIGGER_HEAD_PITCH = 16;
        public static final int TRIGGER_HEAD_YAW = 8;
        public static final int TRIGGER_MOUTH_AH = 4;
        public static final int TRIGGER_START = 1;
        private static final long serialVersionUID = 1;
        public int action;
        public int again;
        public List<Component> component;
        public Map<Component, List<String>> componentResourceMap;
        public int face_count;
        public String fshader;
        public int index = -1;
        public String sound;
        public String src;
        public int strength;
        public List<String> textures;
        public int trigger;
        public int type;
        public String vshader;

        public String toString() {
            return "Filter{type=" + this.type + ", strength=" + this.strength + '}';
        }
    }

    public static class Voice implements Serializable {
        private static final long serialVersionUID = 1;
        public float pitch;
        public float tempo;

        public String toString() {
            return "Voice{tempo=" + this.tempo + ", pitch=" + this.pitch + '}';
        }
    }

    public String toString() {
        return "Effect{name='" + this.name + '\'' + ", version=" + this.version + ", type=" + this.type + '}';
    }
}
