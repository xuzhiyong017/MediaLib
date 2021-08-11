package com.sky.media.kit.transfer;

import java.nio.ShortBuffer;

public interface AudioRemixer {

    public static final AudioRemixer LESS_REMIXER = new LessRemixer();
    public static final AudioRemixer MORE_REMIXER = new MoreRemixer();
    public static final AudioRemixer COPY_REMIXER = new CopyAudioRemixer();

    static class LessRemixer implements AudioRemixer {
        LessRemixer() {
        }

        public void mixBuffer(ShortBuffer shortBuffer, ShortBuffer shortBuffer2) {
            int min = Math.min(shortBuffer.remaining() / 2, shortBuffer2.remaining());
            for (int i = 0; i < min; i++) {
                int i2 = shortBuffer.get() + 32768;
                int i3 = shortBuffer.get() + 32768;
                if (i2 < 32768 || i3 < 32768) {
                    i2 = (i2 * i3) / 32768;
                } else {
                    i2 = (((i2 + i3) * 2) - ((i2 * i3) / 32768)) - 65535;
                }
                if (i2 == 65536) {
                    i2 = 65535;
                }
                shortBuffer2.put((short) (i2 - 32768));
            }
        }
    }

    static class MoreRemixer implements AudioRemixer {
        MoreRemixer() {
        }

        public void mixBuffer(ShortBuffer shortBuffer, ShortBuffer shortBuffer2) {
            int min = Math.min(shortBuffer.remaining(), shortBuffer2.remaining() / 2);
            for (int i = 0; i < min; i++) {
                short s = shortBuffer.get();
                shortBuffer2.put(s);
                shortBuffer2.put(s);
            }
        }
    }

    static class CopyAudioRemixer implements AudioRemixer {
        CopyAudioRemixer() {
        }

        public void mixBuffer(ShortBuffer shortBuffer, ShortBuffer shortBuffer2) {
            shortBuffer2.put(shortBuffer);
        }
    }
    void mixBuffer(ShortBuffer shortBuffer, ShortBuffer shortBuffer2);
}
