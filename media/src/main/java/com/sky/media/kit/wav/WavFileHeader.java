package com.sky.media.kit.wav;

public class WavFileHeader {
    public String chunkID = "RIFF";
    public int chunkSize = 0;
    public String type = "WAVE";
    public String fmtChunkID = "fmt ";
    public int fmtChunkSize = 16;
    public short audioFormat = (short) 1;
    public short channelCount = (short) 2;
    public int sampleRate = 44100;
    public int byterate = 0;
    public short blockalign = (short) 0;
    public short bitspersample = (short) 16;
    public String dataChunkId = "data";
    public int dataChunkSize = 0;

    public WavFileHeader(){}

    public WavFileHeader(int sampleRate, int bitspersample, int channelCount) {
        this.sampleRate = sampleRate;
        this.bitspersample = (short) bitspersample;
        this.channelCount = (short) channelCount;
        this.byterate = ((this.sampleRate * this.channelCount) * this.bitspersample) / 8;
        this.blockalign = (short) ((this.channelCount * this.bitspersample) / 8);
    }
}
