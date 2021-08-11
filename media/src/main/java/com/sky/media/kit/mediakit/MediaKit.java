package com.sky.media.kit.mediakit;

import android.media.MediaCodec.BufferInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaMuxer;
import android.util.Log;

import com.sky.media.kit.mediakit.video.VideoDecoder;
import com.sky.media.kit.mediakit.video.VideoEncoder;
import com.sky.media.kit.mediakit.video.onBufferListener;
import com.sky.media.kit.record.audio.AudioDecoder;
import com.sky.media.kit.transfer.AudioTrackTranscoder;
import com.sky.media.kit.transfer.OutputSurface;
import com.sky.media.kit.transfer.QueuedMuxer;
import com.sky.media.kit.transfer.VideoTrackTranscoder;
import com.sky.media.kit.util.AacUtil;
import com.sky.media.kit.util.CodecUtil;
import com.sky.media.kit.util.DeviceUtil;
import com.sky.media.kit.util.ExtractorUtil;
import com.sky.media.kit.util.ExtractorUtil.*;
import com.sky.media.kit.util.MediaHelper;
import com.sky.media.kit.util.Util;
import com.sky.media.kit.wav.WavFileWriter;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class MediaKit {
    public static Metadata getVideoInfo(String str) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        Metadata metadata;
        try {
            mediaMetadataRetriever.setDataSource(str);
            String extractMetadata = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            String extractMetadata2 = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            String extractMetadata3 = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            String extractMetadata4 = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
            String extractMetadata5 = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            String extractMetadata6 = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
            String extractMetadata7 = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_NUM_TRACKS);
            metadata = new Metadata();
            metadata.duration = Util.getLong(extractMetadata);
            metadata.width = Util.getInteger(extractMetadata2);
            metadata.height = Util.getInteger(extractMetadata3);
            metadata.bitrate = Util.getInteger(extractMetadata4, 1);
            metadata.rotation = Util.getInteger(extractMetadata6);
            metadata.numTracks = Util.getInteger(extractMetadata7);
            metadata.mimeType = extractMetadata5;
            return metadata;
        } catch (Throwable e) {
            e.printStackTrace();
            return new Metadata();
        }finally {
            mediaMetadataRetriever.release();
        }
    }

    public static boolean containsAudio(String str) {
        boolean z = false;
        MediaExtractor mediaExtractor = new MediaExtractor();
        try {
            mediaExtractor.setDataSource(str);
            int trackCount = mediaExtractor.getTrackCount();
            for (int i = 0; i < trackCount; i++) {
                if (mediaExtractor.getTrackFormat(i).getString("mime").startsWith("audio/")) {
                    z = true;
                    return z;
                }
            }
            mediaExtractor.release();
            return false;
        } catch (Throwable e) {
            Log.w("MediaKit", Log.getStackTraceString(e));
        } finally {
            mediaExtractor.release();
        }
        return false;
    }

    public static Size getVideoSize(String str) {
        MediaExtractor mediaExtractor = new MediaExtractor();
        Size size;
        try {
            mediaExtractor.setDataSource(str);
            int trackCount = mediaExtractor.getTrackCount();
            for (int i = 0; i < trackCount; i++) {
                MediaFormat trackFormat = mediaExtractor.getTrackFormat(i);
                if (trackFormat.getString("mime").startsWith("video/")) {
                    size = new Size(trackFormat.getInteger("width"), trackFormat.getInteger("height"));
                    Log.w("MediaKit", size.toString());
                    return size;
                }
            }
            mediaExtractor.release();
            return null;
        } catch (Throwable e) {
            return null;
        } finally {
            mediaExtractor.release();
        }
    }

    public static long getVideoDuration(String str) {
        long j = 0;
        MediaExtractor mediaExtractor = new MediaExtractor();
        try {
            mediaExtractor.setDataSource(str);
            int trackCount = mediaExtractor.getTrackCount();
            for (int i = 0; i < trackCount; i++) {
                MediaFormat trackFormat = mediaExtractor.getTrackFormat(i);
                if (trackFormat.getString("mime").startsWith("video/")) {
                    j = trackFormat.getLong("durationUs");
                    break;
                }
            }
            mediaExtractor.release();
        } catch (Throwable e) {
            Log.w("MediaKit", Log.getStackTraceString(e));
        } finally {
            mediaExtractor.release();
        }
        return j;
    }

    public static long getAudioDuration(String str) {
        long j = 0;
        MediaExtractor mediaExtractor = new MediaExtractor();
        try {
            mediaExtractor.setDataSource(str);
            int trackCount = mediaExtractor.getTrackCount();
            for (int i = 0; i < trackCount; i++) {
                MediaFormat trackFormat = mediaExtractor.getTrackFormat(i);
                if (trackFormat.getString("mime").startsWith("audio/")) {
                    j = trackFormat.getLong("durationUs");
                    break;
                }
            }
            mediaExtractor.release();
        } catch (Throwable e) {
            Log.w("MediaKit", Log.getStackTraceString(e));
        } finally {
            mediaExtractor.release();
        }
        return j;
    }

    public static boolean covertAudioToAACFile(String sourcePath, String outPath) {
        MediaExtractor extractor = new MediaExtractor();
        FileOutputStream fileOutputStream = null;
        try {
            extractor.setDataSource(sourcePath);
            MediaInfo info = ExtractorUtil.parseMediaInfo(extractor);
            if(info != null){
                MediaFormat format = info.mAudioFormat;
                int sampleRate = format.getInteger("sample-rate");
                int channelCount = format.getInteger("channel-count");
                ByteBuffer byteBuffer = ByteBuffer.allocate(16 * 16 * 1024);
                extractor.selectTrack(info.audioTrack);
                fileOutputStream = new FileOutputStream(outPath);
                while (true) {
                    int readSampleData = extractor.readSampleData(byteBuffer, 0);
                    if (readSampleData < 0) {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        extractor.release();
                        return true;
                    }
                    byte[] bArr = new byte[readSampleData + 7];
                    AacUtil.addADTSHeader(bArr,readSampleData + 7,sampleRate,channelCount);
                    byteBuffer.get(bArr,7,readSampleData);
                    fileOutputStream.write(bArr,0,bArr.length);
                    byteBuffer.clear();
                    extractor.advance();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(fileOutputStream != null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            extractor.release();
        }
        return false;
    }

    public static boolean mergeAudioAndVideo(String inputvideoPath, String inputAudioPath, String outputFile) {
        MediaExtractor extractor1 = new MediaExtractor();
        MediaExtractor extractor2 = new MediaExtractor();
        try {
            extractor1.setDataSource(inputvideoPath);
            MediaInfo info = ExtractorUtil.parseMediaInfo(extractor1);
            if(info != null){
                MediaFormat mediaFormat1 = info.mVideoFormat;
                int maxInputSize = mediaFormat1.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);
                extractor2.setDataSource(inputAudioPath);
                MediaInfo audioInfo = ExtractorUtil.parseMediaInfo(extractor2);
                if(audioInfo != null){
                    MediaFormat audioFormat = audioInfo.mAudioFormat;
                    long audioDuration = audioFormat.getLong(MediaFormat.KEY_DURATION);
                    setCsd(audioFormat);
                    MediaMuxer mediaMuxer = new MediaMuxer(outputFile,MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
                    MediaFormat videoFormat = info.mVideoFormat;
                    int videoIndex = mediaMuxer.addTrack(videoFormat);
                    int audioIndex = mediaMuxer.addTrack(audioFormat);
                    mediaMuxer.start();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(maxInputSize);
                    BufferInfo audioBufferInfo = new BufferInfo();
                    BufferInfo videoBufferInfo = new BufferInfo();
                    boolean hasFrameRate = videoFormat.containsKey(MediaFormat.KEY_FRAME_RATE);
                    long perFrameTime = 0;
                    if(hasFrameRate){
                        long videoDuration = videoFormat.getLong(MediaFormat.KEY_DURATION);
                        int frameRate = videoFormat.getInteger(MediaFormat.KEY_FRAME_RATE);
                        perFrameTime = (long)(audioDuration * 1000000.0F / (frameRate * videoDuration));

                    }else{
                        int frameCount = getTotalFrame(extractor1,info.videoTrack);
                        perFrameTime = (long)(audioDuration * 1.0f / (float) frameCount);
                    }

                    extractor1.selectTrack(info.videoTrack);
                    while (true){
                        int readVideoSampleSize = extractor1.readSampleData(byteBuffer,0);
                        if(readVideoSampleSize < 0){
                            extractor2.selectTrack(audioInfo.audioTrack);
                            while (true){
                                int readAudioSampleSize = extractor2.readSampleData(byteBuffer,0);
                                if(readAudioSampleSize < 0){
                                    mediaMuxer.stop();
                                    mediaMuxer.release();
                                    extractor1.release();
                                    extractor2.release();
                                    return true;
                                }else{
                                    audioBufferInfo.size = readAudioSampleSize;
                                    audioBufferInfo.offset = 0;
                                    audioBufferInfo.flags = extractor2.getSampleFlags();
                                    audioBufferInfo.presentationTimeUs = extractor2.getSampleTime();
                                    mediaMuxer.writeSampleData(audioIndex,byteBuffer,audioBufferInfo);
                                    extractor2.advance();
                                }
                            }
                        }else{
                            videoBufferInfo.size = readVideoSampleSize;
                            videoBufferInfo.offset = 0;
                            videoBufferInfo.flags = extractor1.getSampleFlags();
                            long r8 = videoBufferInfo.presentationTimeUs;
                            int r6 = (r8 > 0 ? 1 : (r8 == 0 ? 0 : -1));
                            if(r6 <= 0){
                                videoBufferInfo.presentationTimeUs = extractor1.getSampleTime();
                            }else{
                                videoBufferInfo.presentationTimeUs += perFrameTime;
                            }
                            mediaMuxer.writeSampleData(videoIndex,byteBuffer,videoBufferInfo);
                            extractor1.advance();
                        }
                    }

                }else{
                    extractor1.release();
                    extractor2.release();
                    return false;
                }
            }else{
                extractor1.release();
                extractor2.release();
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            extractor1.release();
            extractor2.release();
        }
        return false;
    }

    public static boolean alignAudioToVideoDuration(String sourcePath, String outputPath, long duration) {
        FileOutputStream fileOutputStream = null;
        long curTime = 0;
        boolean isSuccess = false;
        MediaExtractor mediaExtractor = new MediaExtractor();
        try {
            mediaExtractor.setDataSource(sourcePath);
            ExtractorUtil.MediaInfo info = ExtractorUtil.parseMediaInfo(mediaExtractor);
            if (info == null) {
                mediaExtractor.release();
            } else {
                ByteBuffer allocate = ByteBuffer.allocate(16 * 16 * 1024);
                mediaExtractor.selectTrack(info.audioTrack);
                fileOutputStream = new FileOutputStream(outputPath);
                long lastFrameTime = 0;
                while (curTime < duration) {
                    while (true) {
                        int readSampleData = mediaExtractor.readSampleData(allocate, 0);
                        if (readSampleData < 0) {
                            if (curTime < duration) {
                                mediaExtractor.seekTo(0, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);
                            }
                            lastFrameTime = curTime;
                        } else {
                            curTime = mediaExtractor.getSampleTime() + lastFrameTime;
                            if (curTime >= duration) {
                                mediaExtractor.unselectTrack(info.audioTrack);
                                break;
                            }
                            byte[] bArr = new byte[readSampleData];
                            allocate.get(bArr, 0, readSampleData);
                            fileOutputStream.write(bArr, 0, bArr.length);
                            allocate.clear();
                            mediaExtractor.advance();
                        }
                    }
                }
                isSuccess = true;
                mediaExtractor.release();
            }
        } catch (Exception e4) {
            e4.printStackTrace();
        } finally {
            try {
                mediaExtractor.release();
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            } catch (Throwable th2) {
                 Log.w("MediaKit", Log.getStackTraceString(th2));
            }
        }
        return isSuccess;

    }

    public static boolean toWavFile(String str, String str2) {
        MediaExtractor mediaExtractor = new MediaExtractor();
        try {
            mediaExtractor.setDataSource(str);
            MediaInfo mediaInfo = ExtractorUtil.parseMediaInfo(mediaExtractor);
            if (mediaInfo != null) {
                int integer = mediaInfo.mAudioFormat.getInteger("sample-rate");
                int integer2 = mediaInfo.mAudioFormat.getInteger("channel-count");
                final WavFileWriter wavFileWriter = new WavFileWriter();
                wavFileWriter.writeHeaderFile(str2, integer, 16, integer2);
                AudioDecoder audioDecoder = new AudioDecoder(mediaInfo.audioMime, mediaInfo.mAudioFormat);
                audioDecoder.setOnDecodeCallback(new AudioDecoder.OnDecodeCallback() {
                    public void onDecodeData(byte[] bArr, long j) {
                        wavFileWriter.writeData(bArr, 0, bArr.length);
                    }
                });
                ByteBuffer allocate = ByteBuffer.allocate(262144);
                mediaExtractor.selectTrack(mediaInfo.audioTrack);
                while (true) {
                    int readSampleData = mediaExtractor.readSampleData(allocate, 0);
                    if (readSampleData < 0) {
                        audioDecoder.stop();
                        wavFileWriter.close();
                        mediaExtractor.release();
                        return true;
                    }
                    long sampleTime = mediaExtractor.getSampleTime();
                    byte[] bArr = new byte[readSampleData];
                    allocate.get(bArr);
                    audioDecoder.decodeData(bArr, sampleTime);
                    allocate.clear();
                    mediaExtractor.advance();
                }
            }
        } catch (Throwable e) {
            Log.w("MediaKit", Log.getStackTraceString(e));
        } finally {
            mediaExtractor.release();
        }
        return false;
    }

    public static boolean pureVideoSpeedChange(String str, String str2, float speed) {
        MediaExtractor mediaExtractor = new MediaExtractor();
        boolean covertResult;
        try {
            mediaExtractor.setDataSource(str);
            MediaInfo mediaInfo = ExtractorUtil.parseMediaInfo(mediaExtractor);
            if (mediaInfo == null) {
                covertResult = false;
                return covertResult;
            }
            int integer = mediaInfo.mVideoFormat.getInteger("max-input-size");
            long perFrameTime = MediaKit.getFrameTime(mediaExtractor, mediaInfo, speed);
            MediaMuxer mediaMuxer = new MediaMuxer(str2, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            int addTrack = mediaMuxer.addTrack(mediaInfo.mVideoFormat);
            mediaMuxer.start();
            ByteBuffer allocate = ByteBuffer.allocate(integer);
            BufferInfo bufferInfo = new BufferInfo();
            mediaExtractor.selectTrack(mediaInfo.videoTrack);
            while (true) {
                int readSampleData = mediaExtractor.readSampleData(allocate, 0);
                if (readSampleData < 0) {
                    mediaExtractor.unselectTrack(mediaInfo.videoTrack);
                    mediaMuxer.stop();
                    mediaMuxer.release();
                    mediaExtractor.release();
                    return true;
                }
                bufferInfo.size = readSampleData;
                bufferInfo.offset = 0;
                bufferInfo.flags = mediaExtractor.getSampleFlags();
                if (bufferInfo.presentationTimeUs <= 0) {
                    bufferInfo.presentationTimeUs = mediaExtractor.getSampleTime();
                } else {
                    bufferInfo.presentationTimeUs += perFrameTime;
                }
                mediaMuxer.writeSampleData(addTrack, allocate, bufferInfo);
                mediaExtractor.advance();
            }
        } catch (Throwable e) {
            Log.w("MediaKit", Log.getStackTraceString(e));
            covertResult = false;
        } finally {
            mediaExtractor.release();
        }
        return covertResult;
    }

    public static boolean reverseVideo(String str, String str2) {
        MediaExtractor mediaExtractor = new MediaExtractor();
        String str3 = str2.split("\\.")[0] + "_temp.yuv";
        boolean z;
        try {
            mediaExtractor.setDataSource(str);
            MediaInfo a = ExtractorUtil.parseMediaInfo(mediaExtractor);
            if (a == null) {
                z = false;
                return z;
            }
            int integer;
            long a2 = MediaKit.getFrameTime(mediaExtractor, a, 1.0f);
            Log.d("MediaKit", "Decode bitspersample:" + a.mVideoFormat);
            OutputStream fileOutputStream = new FileOutputStream(str3);
            final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            VideoDecoder videoDecoder = new VideoDecoder(mediaExtractor, a.videoTrack);
            final int[] iArr = new int[1];
            final int[] iArr2 = new int[1];
            videoDecoder.startDecode(new onBufferListener() {
                private byte[] bytes;

                public void onDealData(ByteBuffer byteBuffer, BufferInfo bufferInfo) {
                    if (this.bytes == null) {
                        this.bytes = new byte[bufferInfo.size];
                    }
                    byteBuffer.get(this.bytes);
                    try {
                        bufferedOutputStream.write(this.bytes);
                        bufferedOutputStream.flush();
                    } catch (Exception e) {
                    }
                    int[] iArr1 = iArr;
                    iArr[0] = iArr1[0] + 1;
                    iArr2[0] = bufferInfo.size;
                }
            });
            MediaFormat a3 = videoDecoder.getOutputFormat();
            Log.d("MediaKit", "Actual bitspersample:" + a3);
            videoDecoder.release();
            bufferedOutputStream.close();
            fileOutputStream.close();
            Log.i("MediaKit", "Decode finish:" + str3);
            int i = iArr2[0];
            Log.i("MediaKit", "frame count:" + iArr[0] + " frame size:" + i);
            MediaMuxer mediaMuxer = new MediaMuxer(str2, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            final QueuedMuxer queuedMuxer = new QueuedMuxer(mediaMuxer);
            queuedMuxer.setMediaType(1);
            int a4 = MediaHelper.getColorFormat();
            int integer2 = a.mVideoFormat.getInteger("width");
            if (a.mVideoFormat.containsKey("crop-left") && a.mVideoFormat.containsKey("crop-right")) {
                integer = (a.mVideoFormat.getInteger("crop-right") + 1) - a.mVideoFormat.getInteger("crop-left");
            } else {
                integer = integer2;
            }
            integer2 = a.mVideoFormat.getInteger("height");
            if (a.mVideoFormat.containsKey("crop-top") && a.mVideoFormat.containsKey("crop-bottom")) {
                integer2 = (a.mVideoFormat.getInteger("crop-bottom") + 1) - a.mVideoFormat.getInteger("crop-top");
            }
            Size a5 = MediaKit.getAlignSize(a3, integer, integer2, i);
            integer = CodecUtil.fixSize(a5.width);
            integer2 = a5.height;
            Log.d("MediaKit", "reverse resolution:" + integer + "x" + integer2);
            MediaFormat a6 = MediaHelper.getVideoFormat(integer, integer2, a4);
            if (DeviceUtil.isHWP10() || DeviceUtil.isRYV9()) {
                a6.setInteger("max-input-size", (integer2 * integer) * 4);
            }
            VideoEncoder videoEncoder = new VideoEncoder(a6, queuedMuxer);
            videoEncoder.setBufferListener(new onBufferListener() {
                public void onDealData(ByteBuffer byteBuffer, BufferInfo bufferInfo) {
                    queuedMuxer.queueBuff(QueuedMuxer.MediaType.VIDEO, byteBuffer, bufferInfo);
                }
            });
            RandomAccessFile randomAccessFile = new RandomAccessFile(str3, "r");
            byte[] bArr = new byte[i];
            long j = 0;
            for (integer2 = iArr[0] - 1; integer2 > 0; integer2--) {
                randomAccessFile.seek(((long) integer2) * ((long) i));
                randomAccessFile.read(bArr, 0, i);
                videoEncoder.dequeueInputBuffer(bArr, i, j);
                j += a2;
            }
            randomAccessFile.close();
            videoEncoder.release();
            mediaMuxer.stop();
            mediaMuxer.release();
            mediaExtractor.release();
            Util.deleteFile(str3);
            return true;
        } catch (Throwable e) {
            Log.w("MediaKit", Log.getStackTraceString(e));
            z = false;
        } finally {
            mediaExtractor.release();
            Util.deleteFile(str3);
        }
        return false;
    }

    private static Size getAlignSize(MediaFormat mediaFormat, int i, int i2, int i3) {
        int integer;
        int i4 = 0;
        int i5 = 16;
        Log.i("MediaKit", "before align:" + i + "x" + i2);
        if (mediaFormat.containsKey("stride")) {
            integer = mediaFormat.getInteger("stride");
        } else {
            integer = 0;
        }
        if (mediaFormat.containsKey("slice-height")) {
            i4 = mediaFormat.getInteger("slice-height");
        }
        int i6;
        if (i4 == 0) {
            if (integer == 0) {
                i6 = i4;
                i4 = integer;
                integer = i6;
                loop0:
                for (int i7 = 16; i7 <= 128; i7 <<= 1) {
                    for (int i8 = 16; i8 <= i7; i8 <<= 1) {
                        i4 = (((i - 1) / i7) + 1) * i7;
                        integer = (((i2 - 1) / i8) + 1) * i8;
                        if (((i4 * integer) * 3) / 2 == i3) {
                            break loop0;
                        }
                    }
                }
            } else {
                while (i5 <= 128) {
                    i4 = (((i2 - 1) / i5) + 1) * i5;
                    if (((integer * i4) * 3) / 2 == i3) {
                        i6 = i4;
                        i4 = integer;
                        integer = i6;
                        break;
                    }
                    i5 <<= 1;
                }
                i6 = i4;
                i4 = integer;
                integer = i6;
            }
            if (((i4 * integer) * 3) / 2 != i3) {
                integer = i2;
                i4 = i;
            }
        } else {
            i6 = i4;
            i4 = integer;
            integer = i6;
        }
        if (((i * i2) * 3) / 2 != i3) {
            i2 = integer;
            i = i4;
        }
        Log.i("MediaKit", "after align:" + i + "x" + i2);
        return new Size(i, i2);
    }

    public static boolean processVideoAndMuxerWidthDraw(String str, String str2, OutputSurface.OnProcessCallback onProcessCallback) {
        MediaExtractor mediaExtractor = new MediaExtractor();
        try {
            mediaExtractor.setDataSource(str);
            MediaInfo a = ExtractorUtil.parseMediaInfo(mediaExtractor);
            if (a == null || a.mVideoFormat == null) {
                mediaExtractor.release();
                return false;
            }
            MediaFormat a2 = MediaHelper.getVideoFormat(a.mVideoFormat.getInteger("width"), a.mVideoFormat.getInteger("height"), 2130708361);
            MediaMuxer mediaMuxer = new MediaMuxer(str2, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            QueuedMuxer queuedMuxer = new QueuedMuxer(mediaMuxer);
            VideoTrackTranscoder videoTrackTranscoder;
            if (a.mAudioFormat != null) {
                queuedMuxer.setMediaType(0);
                videoTrackTranscoder = new VideoTrackTranscoder(mediaExtractor, a.videoTrack, a2, queuedMuxer, onProcessCallback);
                AudioTrackTranscoder audioTrackTranscoder = new AudioTrackTranscoder(mediaExtractor, a.audioTrack, a.mAudioFormat, queuedMuxer);
                videoTrackTranscoder.startCodec();
                audioTrackTranscoder.startCodec();
                while (true) {
                    if (videoTrackTranscoder.isEndOfStream() && audioTrackTranscoder.isEndOfStream()) {
                        break;
                    }
                    Object obj;
                    if (videoTrackTranscoder.transferData() || audioTrackTranscoder.transferData()) {
                        obj = 1;
                    } else {
                        obj = null;
                    }
                    if (obj == null) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                        }
                    }
                }
                videoTrackTranscoder.release();
                audioTrackTranscoder.release();
            } else {
                queuedMuxer.setMediaType(1);
                videoTrackTranscoder = new VideoTrackTranscoder(mediaExtractor, a.videoTrack, a2, queuedMuxer, onProcessCallback);
                videoTrackTranscoder.startCodec();
                while (!videoTrackTranscoder.isEndOfStream()) {
                    if (!videoTrackTranscoder.transferData()) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e2) {
                        }
                    }
                }
                videoTrackTranscoder.release();
            }
            mediaMuxer.stop();
            mediaMuxer.release();
            mediaExtractor.release();
            return true;
        } catch (Throwable e3) {
            Log.w("MediaKit", Log.getStackTraceString(e3));
            mediaExtractor.release();
            return false;
        }
    }

    public static boolean compressVideo(String str, String str2, float f, boolean z) {
        MediaExtractor mediaExtractor = new MediaExtractor();
        try {
            mediaExtractor.setDataSource(str);
            MediaInfo a = ExtractorUtil.parseMediaInfo(mediaExtractor);
            if (a == null || a.mVideoFormat == null) {
                mediaExtractor.release();
                return false;
            }
            int rotation;
            int videoWidth = a.mVideoFormat.getInteger("width");
            int videoHeight = a.mVideoFormat.getInteger("height");
            if (z) {
                if (a.mVideoFormat.containsKey("rotation-degrees")) {
                    rotation = a.mVideoFormat.getInteger("rotation-degrees");
                } else {
                    rotation = MediaKit.getVideoInfo(str).rotation;
                }
                if (rotation == 90 || rotation == 270) {
                    videoWidth ^= videoHeight;
                    videoHeight ^= videoWidth;
                    videoWidth ^= videoHeight;
                }
            } else {
                rotation = 0;
            }
            MediaFormat a2 = MediaHelper.getVideoFormat(videoWidth, videoHeight, (int) (((float) MediaKit.getVideoInfo(str).bitrate) * f), 2130708361);
            MediaMuxer mediaMuxer = new MediaMuxer(str2, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            QueuedMuxer queuedMuxer = new QueuedMuxer(mediaMuxer);
            VideoTrackTranscoder videoTrackTranscoder;
            if (a.mAudioFormat != null) {
                queuedMuxer.setMediaType(0);
                videoTrackTranscoder = new VideoTrackTranscoder(mediaExtractor, a.videoTrack, a2, queuedMuxer);
                if (z) {
                    videoTrackTranscoder.setRotation(rotation);
                }
                AudioTrackTranscoder audioTrackTranscoder = new AudioTrackTranscoder(mediaExtractor, a.audioTrack, a.mAudioFormat, queuedMuxer);
                videoTrackTranscoder.startCodec();
                audioTrackTranscoder.startCodec();
                while (true) {
                    if (videoTrackTranscoder.isEndOfStream() && audioTrackTranscoder.isEndOfStream()) {
                        break;
                    }
                    boolean z2 = videoTrackTranscoder.transferData() || audioTrackTranscoder.transferData();
                    if (!z2) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                        }
                    }
                }
                videoTrackTranscoder.release();
                audioTrackTranscoder.release();
            } else {
                queuedMuxer.setMediaType(1);
                videoTrackTranscoder = new VideoTrackTranscoder(mediaExtractor, a.videoTrack, a2, queuedMuxer);
                if (z) {
                    videoTrackTranscoder.setRotation(rotation);
                }
                videoTrackTranscoder.startCodec();
                while (!videoTrackTranscoder.isEndOfStream()) {
                    if (!videoTrackTranscoder.transferData()) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e2) {
                        }
                    }
                }
                videoTrackTranscoder.release();
            }
            mediaMuxer.stop();
            mediaMuxer.release();
            mediaExtractor.release();
            return true;
        } catch (Throwable e3) {
            Log.w("MediaKit", Log.getStackTraceString(e3));
            mediaExtractor.release();
            return false;
        }
    }

    private static long getFrameTime(MediaExtractor mediaExtractor, MediaInfo mediaInfo, float speed) {
        if (mediaInfo.mVideoFormat.containsKey("frame-rate")) {
            return (long) (1000000.0f / (((float) mediaInfo.mVideoFormat.getInteger("frame-rate")) * speed));
        }
        return (long) (((float) mediaInfo.mVideoFormat.getLong("durationUs")) / (((float) MediaKit.getTotalFrame(mediaExtractor, mediaInfo.videoTrack)) * speed));
    }

    private static int getTotalFrame(MediaExtractor mediaExtractor, int track) {
        mediaExtractor.selectTrack(track);
        ByteBuffer allocate = ByteBuffer.allocate(262144);
        int i2 = 0;
        while (mediaExtractor.readSampleData(allocate, 0) >= 0) {
            i2++;
            mediaExtractor.advance();
        }
        mediaExtractor.unselectTrack(track);
        return i2;
    }

    private static void setCsd(MediaFormat mediaFormat) {
        if (mediaFormat != null && !mediaFormat.containsKey("csd-0") && mediaFormat.containsKey("aac-codec-specific-data")) {
            mediaFormat.setByteBuffer("csd-0", mediaFormat.getByteBuffer("aac-codec-specific-data"));
        }
    }
}
