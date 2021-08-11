package com.sky.media.kit.wav;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class WavFileWriter {
    private String path;
    private int length = 0;
    private DataOutputStream outputStream;

    public synchronized boolean writeHeaderFile(String str, int sampleRate, int format, int channelCount) throws IOException {
        if (this.outputStream != null) {
            close();
        }
        this.path = str;
        this.length = 0;
        this.outputStream = new DataOutputStream(new FileOutputStream(str));
        return writeHeaderFileToStream(sampleRate, format, channelCount);
    }

    public synchronized boolean close() throws IOException {
        boolean z;
        z = true;
        if (this.outputStream != null) {
            z = updateWavFileHeader();
            this.outputStream.close();
            this.outputStream = null;
        }
        return z;
    }

    public boolean writeData(byte[] bArr, int i, int i2) {
        if (this.outputStream == null) {
            return false;
        }
        try {
            this.outputStream.write(bArr, i, i2);
            this.length += i2;
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean writeHeaderFileToStream(int i, int i2, int i3) {
        if (this.outputStream == null) {
            return false;
        }
        WavFileHeader wavFileHeader = new WavFileHeader(i, i2, i3);
        try {
            this.outputStream.writeBytes(wavFileHeader.chunkID);
            this.outputStream.write(WavFileWriter.intToBytes(wavFileHeader.chunkSize), 0, 4);
            this.outputStream.writeBytes(wavFileHeader.type);
            this.outputStream.writeBytes(wavFileHeader.fmtChunkID);
            this.outputStream.write(WavFileWriter.intToBytes(wavFileHeader.fmtChunkSize), 0, 4);
            this.outputStream.write(WavFileWriter.shortToBytes(wavFileHeader.audioFormat), 0, 2);
            this.outputStream.write(WavFileWriter.shortToBytes(wavFileHeader.channelCount), 0, 2);
            this.outputStream.write(WavFileWriter.intToBytes(wavFileHeader.sampleRate), 0, 4);
            this.outputStream.write(WavFileWriter.intToBytes(wavFileHeader.byterate), 0, 4);
            this.outputStream.write(WavFileWriter.shortToBytes(wavFileHeader.blockalign), 0, 2);
            this.outputStream.write(WavFileWriter.shortToBytes(wavFileHeader.bitspersample), 0, 2);
            this.outputStream.writeBytes(wavFileHeader.dataChunkId);
            this.outputStream.write(WavFileWriter.intToBytes(wavFileHeader.dataChunkSize), 0, 4);
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean updateWavFileHeader() {
        if (this.outputStream == null) {
            return false;
        }
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(this.path, "rw");
            randomAccessFile.seek(4);
            randomAccessFile.write(WavFileWriter.intToBytes(this.length + 36), 0, 4);
            randomAccessFile.seek(40);
            randomAccessFile.write(WavFileWriter.intToBytes(this.length), 0, 4);
            randomAccessFile.close();
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

    private static byte[] intToBytes(int i) {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(i).array();
    }

    private static byte[] shortToBytes(short s) {
        return ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(s).array();
    }
}
