package com.sky.medialib.ui.kit.model;

import java.io.Serializable;

public class PublishVideo implements Serializable {
    private static final long serialVersionUID = 0;
    private boolean isUseShiftShaft;
    private boolean isUseVignette;
    private int mBeautyLevel;
    private String mCoverPath;
    private int mFilterId;
    private int mHeight;
    private String mInitId;
    private int mInitPieceLength;
    private int mLastPiece;
    private byte[] mLastPieceBytes;
    private String mModelId;
    private String mMusicId;
    private int mPieces;
    private String mVid;
    private String mVideoPath;
    private int mWidth;

    public String getVideoPath() {
        return this.mVideoPath;
    }

    public void setVideoPath(String str) {
        this.mVideoPath = str;
    }

    public String getCoverPath() {
        return this.mCoverPath;
    }

    public void setCoverPath(String str) {
        this.mCoverPath = str;
    }

    public int getBeautyLevel() {
        return this.mBeautyLevel;
    }

    public void setBeautyLevel(int i) {
        this.mBeautyLevel = i;
    }

    public int getFilterId() {
        return this.mFilterId;
    }

    public void setFilterId(int i) {
        this.mFilterId = i;
    }

    public boolean isUseVignette() {
        return this.isUseVignette;
    }

    public void setUseVignette(boolean z) {
        this.isUseVignette = z;
    }

    public boolean isUseShiftShaft() {
        return this.isUseShiftShaft;
    }

    public void setUseShiftShaft(boolean z) {
        this.isUseShiftShaft = z;
    }

    public int getLastPiece() {
        return this.mLastPiece;
    }

    public void setLastPiece(int i) {
        this.mLastPiece = i;
    }

    public byte[] getLastPieceBytes() {
        return this.mLastPieceBytes;
    }

    public void setLastPieceBytes(byte[] bArr) {
        this.mLastPieceBytes = bArr;
    }

    public int getPieces() {
        return this.mPieces;
    }

    public void setPieces(int i) {
        this.mPieces = i;
    }

    public String getInitId() {
        return this.mInitId;
    }

    public void setInitId(String str) {
        this.mInitId = str;
    }

    public String getMusicId() {
        return this.mMusicId;
    }

    public void setMusicId(String str) {
        this.mMusicId = str;
    }

    public int getInitPieceLength() {
        return this.mInitPieceLength;
    }

    public void setInitPieceLength(int i) {
        this.mInitPieceLength = i;
    }

    public String getModelId() {
        return this.mModelId;
    }

    public void setModelId(String str) {
        this.mModelId = str;
    }

    public int getWidth() {
        return this.mWidth;
    }

    public void setWidth(int i) {
        this.mWidth = i;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public void setHeight(int i) {
        this.mHeight = i;
    }
}
