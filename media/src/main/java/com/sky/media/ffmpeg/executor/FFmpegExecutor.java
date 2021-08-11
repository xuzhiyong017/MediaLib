package com.sky.media.ffmpeg.executor;

import android.content.Context;
import android.util.Log;


import com.sky.media.ffmpeg.FFmpegConst;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FFmpegExecutor {
    private static FFmpegExecutor sExecutor;
    private String mFFmpegCmdPath;
    private boolean debug = true;
    private ConsoleReader.ConsoleReaderCallback mCallback = new ExecuteCallback();

    class ExecuteCallback implements ConsoleReader.ConsoleReaderCallback {
        ExecuteCallback() {
        }

        public void onReadLine(String str) {
            if (FFmpegExecutor.this.debug) {
                Log.i("FFmpeg", str);
            }
        }
    }

    public static FFmpegExecutor getInstance(Context context) {
        if (sExecutor == null) {
            try {
                sExecutor = new FFmpegExecutor(context);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return sExecutor;
    }

    private FFmpegExecutor(Context context) throws Exception {
        this.mFFmpegCmdPath = getFFmpegPath(context.getApplicationContext(), FFmpegConst.RawConst.ffmpeg);
        Runtime.getRuntime().exec("chmod 0755 " + this.mFFmpegCmdPath).waitFor();
    }

    private String getFFmpegPath(Context context, int i) throws IOException, InterruptedException {
        File file = new File(context.getDir("ffmpeg", 0), "ffmpeg");
        if (file.exists()) {
            return file.getCanonicalPath();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        InputStream openRawResource = context.getResources().openRawResource(i);
        byte[] bArr = new byte[1024];
        while (true) {
            int read = openRawResource.read(bArr);
            if (read > 0) {
                fileOutputStream.write(bArr, 0, read);
            } else {
                fileOutputStream.close();
                openRawResource.close();
                return file.getCanonicalPath();
            }
        }
    }

    private ArrayList<String> getFFCommand() {
        ArrayList<String> arrayList = new ArrayList();
        arrayList.add(this.mFFmpegCmdPath);
        return arrayList;
    }

    public int executeCmd(List<String> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String append : list) {
            stringBuilder.append(append);
            stringBuilder.append(' ');
        }
        Log.i("FFmpeg", stringBuilder.toString());
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(list);
            processBuilder.directory(new File(this.mFFmpegCmdPath).getParentFile());
            Process start = processBuilder.start();
            new ConsoleReader(start.getInputStream(), this.mCallback).start();
            new ConsoleReader(start.getErrorStream(), this.mCallback).start();
            return start.waitFor();
        } catch (Throwable e) {
            Log.e("FFmpeg", Log.getStackTraceString(e));
            return -1;
        }
    }

    public int cutVideo(String inputFile, String outputFile, float start, float duration) {
        List a = getFFCommand();
        a.add("-ss");
        a.add(String.valueOf(start));
        a.add("-accurate_seek");
        a.add("-i");
        a.add(inputFile);
        a.add("-t");
        a.add(String.valueOf(duration));
        a.add("-vcodec");
        a.add("copy");
        a.add("-acodec");
        a.add("copy");
        a.add("-y");
        a.add(outputFile);
        return executeCmd(a);
    }

    public int setKeyFrameAndGop(String inputFile, String outFile, float startTime, float duration, int keyFrameDur, int gop) {
        List a = getFFCommand();
        a.add("-y");
        a.add("-i");
        a.add(inputFile);
        a.add("-ss");
        a.add(String.valueOf(startTime));
        a.add("-t");
        a.add(String.valueOf(duration));
        a.add("-c:a");
        a.add("copy");
        a.add("-vcodec");
        a.add("h264");
        a.add("-keyint_min");
        a.add(String.valueOf(keyFrameDur));
        a.add("-g");
        a.add(String.valueOf(gop));
        a.add("-y");
        a.add(outFile);
        return executeCmd(a);
    }

    public int cutAudio(String inputFile, String outFile, float startTime, float duration) {
        List a = getFFCommand();
        a.add("-ss");
        a.add(String.valueOf(startTime));
        a.add("-i");
        a.add(inputFile);
        a.add("-t");
        a.add(String.valueOf(duration));
        a.add("-acodec");
        a.add("copy");
        a.add("-y");
        a.add(outFile);
        return executeCmd(a);
    }

    public int mixAudioToVideo(String str, String str2, String str3) {
        List a = getFFCommand();
        a.add("-y");
        a.add("-i");
        a.add(str);
        a.add("-i");
        a.add(str2);
        a.add("-filter_complex");
        a.add("[0:a] pan=stereo|c0=1*c0|c1=1*c1,volume=2.0 [a1], [1:a] pan=stereo|c0=1*c0|c1=1*c1,volume=1.0 [a2],[a1][a2]amix=duration=first,pan=stereo|c0<c0+c1|c1<c2+c3,pan=mono|c0=c0+c1[a]");
        a.add("-map");
        a.add("[a]");
        a.add("-map");
        a.add("0:v");
        a.add("-c:v");
        a.add("libx264");
        a.add("-preset");
        a.add("ultrafast");
        a.add("-c:a");
        a.add("aac");
        a.add("-strict");
        a.add("-2");
        a.add("-ac");
        a.add("2");
        a.add(str3);
        return executeCmd(a);
    }

    public int mergeAudioAndVideo(String input1, String input2, String output) {
        List a = getFFCommand();
        a.add("-i");
        a.add(input1);
        a.add("-i");
        a.add(input2);
        a.add("-vcodec");
        a.add("copy");
        a.add("-acodec");
        a.add("copy");
        a.add("-absf");
        a.add("aac_adtstoasc");
        a.add("-y");
        a.add(output);
        return executeCmd(a);
    }

    private int covertVideoToTs(String str, String str2) {
        List a = getFFCommand();
        a.add("-i");
        a.add(str);
        a.add("-c");
        a.add("copy");
        a.add("-bsf:v");
        a.add("h264_mp4toannexb");
        a.add("-f");
        a.add("mpegts");
        a.add("-y");
        a.add(str2);
        return executeCmd(a);
    }

    private int concatSegment(String str, String... strArr) {
        StringBuilder stringBuilder = new StringBuilder("concat:");
        stringBuilder.append(strArr[0]);
        for (int i = 1; i < strArr.length; i++) {
            stringBuilder.append("|");
            stringBuilder.append(strArr[i]);
        }
        List a = getFFCommand();
        a.add("-i");
        a.add(stringBuilder.toString());
        a.add("-c");
        a.add("copy");
        a.add("-bsf:a");
        a.add("aac_adtstoasc");
        a.add("-y");
        a.add(str);
        return executeCmd(a);
    }

    public int concatVideoSegment(String str, String... strArr) {
        int i;
        int i2 = 0;
        String str2 = str.split("\\.")[0] + "_";
        ArrayList arrayList = new ArrayList();
        for (int i3 = 0; i3 < strArr.length; i3++) {
            String str3 = str2 + i3 + ".ts";
            covertVideoToTs(strArr[i3], str3);
            arrayList.add(str3);
        }
        String[] strArr2 = new String[arrayList.size()];
        for (i = 0; i < arrayList.size(); i++) {
            strArr2[i] = (String) arrayList.get(i);
        }
        i = concatSegment(str, strArr2);
        while (i2 < arrayList.size()) {
            File file = new File((String) arrayList.get(i2));
            if (file.exists()) {
                file.delete();
            }
            i2++;
        }
        return i;
    }

    public int doPureVideo(String str, String str2) {
        List a = getFFCommand();
        a.add("-i");
        a.add(str);
        a.add("-vcodec");
        a.add("copy");
        a.add("-an");
        a.add("-y");
        a.add(str2);
        return executeCmd(a);
    }

    public int audioToAAC(String str, String str2) {
        List a = getFFCommand();
        a.add("-i");
        a.add(str);
        a.add("-c:a");
        a.add("aac");
        a.add("-strict");
        a.add("-2");
        a.add("-y");
        a.add(str2);
        return executeCmd(a);
    }

    public int reverseVideo(String str, String str2) {
        List a = getFFCommand();
        a.add("-i");
        a.add(str);
        a.add("-vf");
        a.add("reverse");
        a.add("-acodec");
        a.add("copy");
        a.add("-c:v");
        a.add("libx264");
        a.add("-y");
        a.add(str2);
        return executeCmd(a);
    }
}
