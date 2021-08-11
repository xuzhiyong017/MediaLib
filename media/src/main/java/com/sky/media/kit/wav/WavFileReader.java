package com.sky.media.kit.wav;

import android.util.Log;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class WavFileReader {
    private static final String TAG = WavFileReader.class.getSimpleName();
    private DataInputStream inputStream;
    private WavFileHeader wavFileHeader;

    public boolean readWavFileHeader(String str) throws IOException {
        if (this.inputStream != null) {
            closeStream();
        }
        this.inputStream = new DataInputStream(new FileInputStream(str));
        return readWavFileHeaderFromInputStream();
    }

    public void closeStream() throws IOException {
        if (this.inputStream != null) {
            this.inputStream.close();
            this.inputStream = null;
        }
    }

    public WavFileHeader getWavFileHeader() {
        return this.wavFileHeader;
    }

    public int readDatas(byte[] bArr, int i, int i2) {
        if (this.inputStream == null || this.wavFileHeader == null) {
            return -1;
        }
        try {
            int read = this.inputStream.read(bArr, i, i2);
            if (read != -1) {
                return read;
            }
            return -1;
        } catch (Throwable e) {
            e.printStackTrace();
            return -1;
        }
    }

    private boolean readWavFileHeaderFromInputStream() {
        if (this.inputStream == null) {
            return false;
        }
        WavFileHeader wavFileHeader = new WavFileHeader();
        byte[] bArr = new byte[4];
        byte[] bArr2 = new byte[2];
        try {
            wavFileHeader.chunkID = "" + ((char) this.inputStream.readByte()) + ((char) this.inputStream.readByte()) + ((char) this.inputStream.readByte()) + ((char) this.inputStream.readByte());
            Log.d(TAG, "Read file chunkID:" + wavFileHeader.chunkID);
            this.inputStream.read(bArr);
            wavFileHeader.chunkSize = WavFileReader.bytesToInt(bArr);
            Log.d(TAG, "Read file chunkSize:" + wavFileHeader.chunkSize);
            wavFileHeader.type = "" + ((char) this.inputStream.readByte()) + ((char) this.inputStream.readByte()) + ((char) this.inputStream.readByte()) + ((char) this.inputStream.readByte());
            Log.d(TAG, "Read file bitspersample:" + wavFileHeader.type);
            wavFileHeader.fmtChunkID = "" + ((char) this.inputStream.readByte()) + ((char) this.inputStream.readByte()) + ((char) this.inputStream.readByte()) + ((char) this.inputStream.readByte());
            Log.d(TAG, "Read fmt chunkID:" + wavFileHeader.fmtChunkID);
            this.inputStream.read(bArr);
            wavFileHeader.fmtChunkSize = WavFileReader.bytesToInt(bArr);
            Log.d(TAG, "Read fmt chunkSize:" + wavFileHeader.fmtChunkSize);
            this.inputStream.read(bArr2);
            wavFileHeader.audioFormat = WavFileReader.bytesToShort(bArr2);
            Log.d(TAG, "Read audioFormat:" + wavFileHeader.audioFormat);
            this.inputStream.read(bArr2);
            wavFileHeader.channelCount = WavFileReader.bytesToShort(bArr2);
            Log.d(TAG, "Read channel number:" + wavFileHeader.channelCount);
            this.inputStream.read(bArr);
            wavFileHeader.sampleRate = WavFileReader.bytesToInt(bArr);
            Log.d(TAG, "Read samplerate:" + wavFileHeader.sampleRate);
            this.inputStream.read(bArr);
            wavFileHeader.byterate = WavFileReader.bytesToInt(bArr);
            Log.d(TAG, "Read byterate:" + wavFileHeader.byterate);
            this.inputStream.read(bArr2);
            wavFileHeader.blockalign = WavFileReader.bytesToShort(bArr2);
            Log.d(TAG, "Read blockalign:" + wavFileHeader.blockalign);
            this.inputStream.read(bArr2);
            wavFileHeader.bitspersample = WavFileReader.bytesToShort(bArr2);
            Log.d(TAG, "Read bitspersample:" + wavFileHeader.bitspersample);
            wavFileHeader.dataChunkId = "" + ((char) this.inputStream.readByte()) + ((char) this.inputStream.readByte()) + ((char) this.inputStream.readByte()) + ((char) this.inputStream.readByte());
            Log.d(TAG, "Read data chunkID:" + wavFileHeader.dataChunkId);
            this.inputStream.read(bArr);
            wavFileHeader.dataChunkSize = WavFileReader.bytesToInt(bArr);
            Log.d(TAG, "Read data chunkSize:" + wavFileHeader.dataChunkSize);
            Log.d(TAG, "Read wav file success !");
            this.wavFileHeader = wavFileHeader;
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

    private static short bytesToShort(byte[] bArr) {
        return ByteBuffer.wrap(bArr).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }

    private static int bytesToInt(byte[] bArr) {
        return ByteBuffer.wrap(bArr).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }
}
