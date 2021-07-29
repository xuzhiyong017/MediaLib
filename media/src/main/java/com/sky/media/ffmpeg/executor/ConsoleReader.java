package com.sky.media.ffmpeg.executor;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

class ConsoleReader extends Thread {

    private InputStream mInputStream;
    private ConsoleReaderCallback mCallback;

    public interface ConsoleReaderCallback {
        void onReadLine(String str);
    }

    ConsoleReader(InputStream inputStream, ConsoleReaderCallback consoleReaderCallback) {
        this.mInputStream = inputStream;
        this.mCallback = consoleReaderCallback;
    }

    public void run() {
        try {
            Reader inputStreamReader = new InputStreamReader(this.mInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    bufferedReader.close();
                    inputStreamReader.close();
                    return;
                } else if (this.mCallback != null) {
                    this.mCallback.onReadLine(readLine);
                }
            }
        } catch (Throwable e) {
            Log.w("FFmpeg", Log.getStackTraceString(e));
        }
    }
}
