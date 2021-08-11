package com.sky.media.kit.render.sticker;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Sticker {
    public List<Component> component;
    public Map<Component, List<String>> componentResourceMap;
    public int face_count;

    public static class Component implements Serializable {
        public static final int ACTION_HIDE_ALWAYS = 7;
        public static final int ACTION_HIDE_UNTIL_NOT_TRIGGER = 1;
        public static final int ACTION_SHOW_ALWAYS = 6;
        public static final int ACTION_SHOW_LAST = 5;
        public static final int ACTION_SHOW_LAST_UNTIL_NOT_TRIGGER = 8;
        public static final int ACTION_SHOW_ONCE = 4;
        public static final int ACTION_SHOW_ONCE_UNTIL_NOT_TRIGGER = 3;
        public static final int ACTION_SHOW_UNTIL_NOT_TRIGGER = 2;
        public static final int AGAIN_OFF = 1;
        public static final int AGAIN_ON = 0;
        public static final int ANY_FACE = -1;
        public static final int BOTTOM_CENTER = 7;
        public static final int CENTER = 4;
        public static final int FULL_ALL = 3;
        public static final int FULL_LANDSCAPE = 1;
        public static final int FULL_NO = 0;
        public static final int FULL_PORTRAIT = 2;
        public static final int LEFT_BOTTOM = 2;
        public static final int LEFT_CENTER = 8;
        public static final int LEFT_TOP = 0;
        public static final int RIGHT_BOTTOM = 3;
        public static final int RIGHT_CENTER = 6;
        public static final int RIGHT_TOP = 1;
        public static final int TOP_CENTER = 5;
        public static final int TRIGGER_BROW_JUMP = 32;
        public static final int TRIGGER_EYE_BLINK = 2;
        public static final int TRIGGER_HEAD_PITCH = 16;
        public static final int TRIGGER_HEAD_YAW = 8;
        public static final int TRIGGER_MOUTH_AH = 4;
        public static final int TRIGGER_START = 1;
        public static final int TYPE_FACE = 0;
        public static final int TYPE_FIXED = 1;
        public static final int TYPE_MAKEUP = 2;
        private static final long serialVersionUID = 1;
        public int action;
        public int after;
        public int again;
        public int anchor;
        public int bottom;
        public int duration;
        public int face_index = -1;
        public List<FacePoint> faces;
        public int full = 0;
        public int height;
        public String id;
        public int left;
        public int length;
        public int right;
        public float scale = 1.0f;
        public String sound;
        public String src;
        public String text;
        public int top;
        public int trigger;
        public int type;
        public int width;

        public boolean equals(Object obj) {
            boolean z = true;
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Component)) {
                return false;
            }
            Component component = (Component) obj;
            if (this.length != component.length || this.width != component.width || this.height != component.height || Float.compare(component.scale, this.scale) != 0 || this.duration != component.duration || this.type != component.type || this.full != component.full || this.top != component.top || this.right != component.right || this.left != component.left || this.bottom != component.bottom || this.anchor != component.anchor || this.trigger != component.trigger || this.action != component.action || this.again != component.again || this.after != component.after || this.face_index != component.face_index) {
                return false;
            }
            if (this.src != null) {
                if (!this.src.equals(component.src)) {
                    return false;
                }
            } else if (component.src != null) {
                return false;
            }
            if (this.id != null) {
                if (!this.id.equals(component.id)) {
                    return false;
                }
            } else if (component.id != null) {
                return false;
            }
            if (this.faces != null) {
                if (!this.faces.equals(component.faces)) {
                    return false;
                }
            } else if (component.faces != null) {
                return false;
            }
            if (this.sound != null) {
                if (!this.sound.equals(component.sound)) {
                    return false;
                }
            } else if (component.sound != null) {
                return false;
            }
            if (this.text != null) {
                z = this.text.equals(component.text);
            } else if (component.text != null) {
                z = false;
            }
            return z;
        }

        public int hashCode() {
            int hashCode;
            int i = 0;
            int hashCode2 = (this.src != null ? this.src.hashCode() : 0) * 31;
            if (this.id != null) {
                hashCode = this.id.hashCode();
            } else {
                hashCode = 0;
            }
            hashCode2 = (((((((hashCode + hashCode2) * 31) + this.length) * 31) + this.width) * 31) + this.height) * 31;
            if (this.scale != 0.0f) {
                hashCode = Float.floatToIntBits(this.scale);
            } else {
                hashCode = 0;
            }
            hashCode2 = (((((hashCode + hashCode2) * 31) + this.duration) * 31) + this.type) * 31;
            if (this.faces != null) {
                hashCode = this.faces.hashCode();
            } else {
                hashCode = 0;
            }
            hashCode2 = (((((((((((((((((hashCode + hashCode2) * 31) + this.full) * 31) + this.top) * 31) + this.right) * 31) + this.left) * 31) + this.bottom) * 31) + this.anchor) * 31) + this.trigger) * 31) + this.action) * 31;
            if (this.sound != null) {
                hashCode = this.sound.hashCode();
            } else {
                hashCode = 0;
            }
            hashCode = (((((((hashCode + hashCode2) * 31) + this.again) * 31) + this.after) * 31) + this.face_index) * 31;
            if (this.text != null) {
                i = this.text.hashCode();
            }
            return hashCode + i;
        }

        public String toString() {
            return "Component{src='" + this.src + '\'' + '}';
        }
    }

    public static class FacePoint implements Serializable {
        private static final long serialVersionUID = 1;
        public int id;
        public int x;
        public int y;

        public String toString() {
            return "FacePoint{id=" + this.id + ", x=" + this.x + ", y=" + this.y + '}';
        }
    }
}
