package com.sky.media.kit.mediakit;

import android.content.Context;
import android.util.Log;

import com.sky.media.ffmpeg.executor.FFmpegExecutor;
import com.sky.media.kit.record.audio.AudioEncoder;
import com.sky.media.kit.transfer.OutputSurface;
import com.sky.media.kit.util.DeviceUtil;
import com.sky.media.kit.util.Util;
import com.sky.media.kit.wav.WavFileHeader;
import com.sky.media.kit.wav.WavFileReader;
import com.sky.media.kit.wav.WavFileWriter;
import com.weibo.soundtouch.SoundTouch;

public class MediaKitCompat {
    public static boolean mergeAudioAndVideo(String str, String str2, String str3, Context context) {
        boolean z = false;
        FFmpegExecutor ffmpegExecutor = FFmpegExecutor.getInstance(context);
        String str4 = str.split("\\.")[0] + "_.mp4";
        if (ffmpegExecutor.doPureVideo(str, str4) == 0) {
            if (ffmpegExecutor.mergeAudioAndVideo(str4, str2, str3) == 0) {
                z = true;
            }
            Util.deleteFile(str4);
        }
        return z;
    }

    public static boolean mergeVideoStatus(String str, String str2, String str3, Context context) {
        boolean z = true;
        if (DeviceUtil.isRY6() || DeviceUtil.isHWP9() || DeviceUtil.isMiNote()) {
            FFmpegExecutor fFmpegExecutor = FFmpegExecutor.getInstance(context);
            float round = ((float) Math.round((((float) MediaKit.getVideoDuration(str)) * 100.0f) / ((float) MediaKit.getAudioDuration(str2)))) * 0.01f;
            if (round == 0.0f) {
                return false;
            }
            String str4 = str.split("\\.")[0] + "_.mp4";
            if (round == 1.0f) {
                boolean z2;
                if (fFmpegExecutor.doPureVideo(str, str4) == 0) {
                    z2 = true;
                } else {
                    z2 = false;
                }
                if (!(z2 && fFmpegExecutor.mergeAudioAndVideo(str4, str2, str3) == 0)) {
                    z = false;
                }
            } else if (!(MediaKit.pureVideoSpeedChange(str, str4, round) && fFmpegExecutor.mergeAudioAndVideo(str4, str2, str3) == 0)) {
                z = false;
            }
            Util.deleteFile(str4);
        } else {
            z = MediaKit.mergeAudioAndVideo(str, str2, str3);
        }
        return z;
    }

    public static boolean reverseVideo(String str, String str2, Context context) {
        boolean z = true;
        if (!DeviceUtil.isHWP9() && !DeviceUtil.isHM()) {
            return MediaKit.reverseVideo(str, str2);
        }
        FFmpegExecutor a = FFmpegExecutor.getInstance(context);
        String str3 = str.split("\\.")[0] + "_.mp4";
        if (!((a.doPureVideo(str, str3) == 0) && a.reverseVideo(str3, str2) == 0)) {
            z = false;
        }
        Util.deleteFile(str3);
        return z;
    }

    public static boolean covertAACToWav(String str, String str2, float speed) {
        if (((double) speed) == 1.0d || ((double) speed) == 0.0d) {
            return false;
        }
        String str3 = str2.split("\\.")[0] + "_temp.wav";
        String str4 = str2.split("\\.")[0] + "_temp1.wav";
        if (!MediaKit.toWavFile(str, str3)) {
            return false;
        }
        try {
            int b;
            WavFileReader wavFileReader = new WavFileReader();
            wavFileReader.readWavFileHeader(str3);
            WavFileHeader b2 = wavFileReader.getWavFileHeader();
            int i = b2.channelCount;
            int i2 = b2.sampleRate;
            short s = b2.bitspersample;
            WavFileWriter wavFileWriter = new WavFileWriter();
            wavFileWriter.writeHeaderFile(str4, i2, s, i);
            SoundTouch a = SoundTouch.getInstance();
            a.initParams(i, i2, s / 8);
            a.setTempo((speed - 1.0f) * 100.0f);
            byte[] bArr = new byte[4096];
            while (wavFileReader.readDatas(bArr, 0, bArr.length) > 0) {
                a.putSamples(bArr);
                do {
                    b = a.receiveSamples(bArr);
                    wavFileWriter.writeData(bArr, 0, b);
                } while (b != 0);
            }
            a.flush();
            do {
                b = a.receiveSamples(bArr);
                wavFileWriter.writeData(bArr, 0, b);
            } while (b != 0);
            wavFileReader.closeStream();
            wavFileWriter.close();
            AudioEncoder audioEncoder = new AudioEncoder(str2);
            audioEncoder.startAudioEncoder(i2, i);
            WavFileReader wavFileReader2 = new WavFileReader();
            wavFileReader2.readWavFileHeader(str4);
            while (wavFileReader2.readDatas(bArr, 0, bArr.length) > 0) {
                audioEncoder.encodeData(bArr, System.nanoTime() / 1000);
            }
            wavFileReader2.closeStream();
            audioEncoder.release();
            Util.deleteFile(str3);
            Util.deleteFile(str4);
            return true;
        } catch (Throwable e) {
            Log.e("MediaKit", Log.getStackTraceString(e));
            return false;
        }
    }

    public static boolean covertSpeedToVideo(String str, String str2, float speed, Context context) {
        boolean z = true;
        if (!MediaKit.containsAudio(str)) {
            return MediaKit.pureVideoSpeedChange(str, str2, speed);
        }
        String str3 = str.split("\\.")[0];
        String str4 = str3 + "_.aac";
        String str5 = str3 + "_1.aac";
        try {
            boolean z2 = MediaKit.covertAudioToAACFile(str, str4) && MediaKitCompat.covertAACToWav(str4, str5, speed);
            if (!(z2 && MediaKitCompat.mergeVideoStatus(str, str5, str2, context))) {
                z = false;
            }
            Util.deleteFile(str4);
            Util.deleteFile(str5);
            return z;
        } catch (Throwable th) {
            Util.deleteFile(str4);
            Util.deleteFile(str5);
        }
        return false;
    }

    public static boolean processVideo(String str, String str2, OutputSurface.OnProcessCallback onProcessCallback, Context context) {
        boolean z = true;
        String str3 = str2.split("\\.")[0];
        String str4 = str3 + "_.mp4";
        String str5 = str3 + "_1.mp4";
        String str6 = str3 + "_.aac";
        if (FFmpegExecutor.getInstance(context).doPureVideo(str, str4) != 0) {
            z = false;
        } else if (MediaKit.containsAudio(str)) {
            boolean z2 = MediaKit.processVideoAndMuxerWidthDraw(str4, str5, onProcessCallback) && MediaKit.covertAudioToAACFile(str, str6);
            if (!(z2 && MediaKitCompat.mergeAudioAndVideo(str5, str6, str2, context))) {
                z = false;
            }
        } else {
            z = MediaKit.processVideoAndMuxerWidthDraw(str4, str2, onProcessCallback);
        }
        Util.deleteFile(str4);
        Util.deleteFile(str5);
        Util.deleteFile(str6);
        return z;
    }

    public static boolean cropVideo(String str, String str2, float f, boolean z, Context context) {
        boolean z2 = true;
        String str3 = str2.split("\\.")[0];
        String str4 = str3 + "_.mp4";
        String str5 = str3 + "_1.mp4";
        String str6 = str3 + "_.aac";
        if (FFmpegExecutor.getInstance(context).doPureVideo(str, str4) != 0) {
            z2 = false;
        } else if (MediaKit.containsAudio(str)) {
            boolean z3 = MediaKit.compressVideo(str4, str5, f, z) && MediaKit.covertAudioToAACFile(str, str6);
            if (!(z3 && MediaKitCompat.mergeAudioAndVideo(str5, str6, str2, context))) {
                z2 = false;
            }
        } else {
            z2 = MediaKit.compressVideo(str4, str2, f, z);
        }
        Util.deleteFile(str4);
        Util.deleteFile(str5);
        Util.deleteFile(str6);
        return z2;
    }
}
