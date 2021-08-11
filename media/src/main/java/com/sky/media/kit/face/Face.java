package com.sky.media.kit.face;

import android.graphics.PointF;
import android.graphics.Rect;

public class Face implements Cloneable {
    public static final int BROW_JUMP = 32;
    public static final int EYE_BLINK = 2;
    public static final int HEAD_PITCH = 16;
    public static final int HEAD_YAW = 8;
    public static final int INVALID_VALUE = Integer.MAX_VALUE;
    public static final int MOUTH_AH = 4;
    public float mEyeDistance;
    public int mFaceAction;
    public int mFaceId;
    public PointF[] mFacePointArray;
    public Rect mFaceRect;
    public float mPitch = 2.14748365E9f;
    public float mRoll = 2.14748365E9f;
    public float mScore;
    public float mYaw = 2.14748365E9f;

    public Face(int i) {
        this.mFacePointArray = new PointF[i];
    }

    public Object clone() throws CloneNotSupportedException {
        Face face = (Face) super.clone();
        face.mFacePointArray = new PointF[this.mFacePointArray.length];
        for (int i = 0; i < this.mFacePointArray.length; i++) {
            PointF pointF = new PointF();
            pointF.x = this.mFacePointArray[i].x;
            pointF.y = this.mFacePointArray[i].y;
            face.mFacePointArray[i] = pointF;
        }
        return face;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        if (this.mFaceId != ((Face) obj).mFaceId) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.mFaceId;
    }
}
