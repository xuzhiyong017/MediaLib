package com.sky.media.kit.transfer;

import android.media.MediaCodec.BufferInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class QueuedMuxer {
    private final MediaMuxer mediaMuxer;
    private MediaFormat videoFormat;
    private MediaFormat audioFormat;
    private int videoTrackIndex;
    private int audioTrackIndex;
    private ByteBuffer byteBuffer;
    private final List<QueueTask> queueTasks;
    private boolean hasStarted;
    private int curType = 0;

    private static class QueueTask {
        private final MediaType mediaType;
        private final int size;
        private final long presentationTimeUs;
        private final int flags;

        private QueueTask(MediaType mediaType, int i, BufferInfo bufferInfo) {
            this.mediaType = mediaType;
            this.size = i;
            this.presentationTimeUs = bufferInfo.presentationTimeUs;
            this.flags = bufferInfo.flags;
        }

        private void setOffset(BufferInfo bufferInfo, int offset) {
            bufferInfo.set(offset, this.size, this.presentationTimeUs, this.flags);
        }
    }

    public enum MediaType {
        VIDEO,
        AUDIO
    }

    public QueuedMuxer(MediaMuxer mediaMuxer) {
        this.mediaMuxer = mediaMuxer;
        this.queueTasks = new ArrayList();
    }

    public void setMediaType(int i) {
        this.curType = i;
    }

    public void executeTask(MediaType format, MediaFormat mediaFormat) {
        switch (format) {
            case VIDEO:
                this.videoFormat = mediaFormat;
                break;
            case AUDIO:
                this.audioFormat = mediaFormat;
                break;
            default:
                throw new AssertionError();
        }
        startDeal();
    }

    private void startDeal() {
        int i = 0;
        switch (this.curType) {
            case 1:
                if (this.videoFormat == null) {
                    return;
                }
                break;
            case 16:
                if (this.audioFormat == null) {
                    return;
                }
                break;
            default:
                if (this.videoFormat == null || this.audioFormat == null) {
                    return;
                }
        }
        if (this.videoFormat != null) {
            this.videoTrackIndex = this.mediaMuxer.addTrack(this.videoFormat);
            Log.v("QueuedMuxer", "Added track #" + this.videoTrackIndex + " with " + this.videoFormat.getString("mime") + " to muxer");
        }
        if (this.audioFormat != null) {
            this.audioTrackIndex = this.mediaMuxer.addTrack(this.audioFormat);
            Log.v("QueuedMuxer", "Added track #" + this.audioTrackIndex + " with " + this.audioFormat.getString("mime") + " to muxer");
        }
        this.mediaMuxer.start();
        this.hasStarted = true;
        if (this.byteBuffer == null) {
            this.byteBuffer = ByteBuffer.allocate(0);
        }
        this.byteBuffer.flip();
        Log.v("QueuedMuxer", "Output bitspersample determined, writing " + this.queueTasks.size() + " samples / " + this.byteBuffer.limit() + " bytes to muxer.");
        BufferInfo bufferInfo = new BufferInfo();
        Iterator it = this.queueTasks.iterator();
        while (true) {
            int i2 = i;
            if (it.hasNext()) {
                QueueTask queueTask = (QueueTask) it.next();
                queueTask.setOffset(bufferInfo, i2);
                this.mediaMuxer.writeSampleData(getCurMediaType(queueTask.mediaType), this.byteBuffer, bufferInfo);
                i = queueTask.size + i2;
            } else {
                this.queueTasks.clear();
                this.byteBuffer = null;
                return;
            }
        }
    }

    public void queueBuff(MediaType mediaType, ByteBuffer byteBuffer, BufferInfo bufferInfo) {
        if (this.hasStarted) {
            this.mediaMuxer.writeSampleData(getCurMediaType(mediaType), byteBuffer, bufferInfo);
            return;
        }
        byteBuffer.limit(bufferInfo.offset + bufferInfo.size);
        byteBuffer.position(bufferInfo.offset);
        if (this.byteBuffer == null) {
            this.byteBuffer = ByteBuffer.allocate(262144);
        }
        this.byteBuffer.put(byteBuffer);
        this.queueTasks.add(new QueueTask(mediaType, bufferInfo.size, bufferInfo));
    }

    private int getCurMediaType(MediaType mediaType) {
        switch (mediaType) {
            case VIDEO:
                return this.videoTrackIndex;
            case AUDIO:
                return this.audioTrackIndex;
            default:
                throw new AssertionError();
        }
    }
}
