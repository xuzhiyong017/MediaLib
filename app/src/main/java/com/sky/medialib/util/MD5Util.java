package com.sky.medialib.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;

public class MD5Util {
    public static String toMD5String(String str) {
        int i = 0;
        if (str == null) {
            return null;
        }
        String str2;
        char[] cArr = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(str.getBytes());
            byte[] digest = instance.digest();
            char[] cArr2 = new char[32];
            int i2 = 0;
            while (i < 16) {
                byte b = digest[i];
                int i3 = i2 + 1;
                cArr2[i2] = cArr[(b >>> 4) & 15];
                i2 = i3 + 1;
                cArr2[i3] = cArr[b & 15];
                i++;
            }
            str2 = new String(cArr2);
        } catch (Throwable e) {
            e.printStackTrace();
            str2 = null;
        }
        return str2;
    }
    public static String getFileMD5(File file) {

        String str = null;
        FileInputStream fileInputStream = null;
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            fileInputStream = new FileInputStream(file);
            byte[] bArr = new byte[8192];
            while (true) {
                int read = fileInputStream.read(bArr);
                if (read == -1) {
                    break;
                }
                instance.update(bArr, 0, read);
            }
            str = MD5Util.byteArrayToHexString(instance.digest());
            return str;
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            if(fileInputStream != null){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;

    }

    public static String encodeByteArray(byte[] bArr) {
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(bArr);
            return MD5Util.byteArrayToHexString(instance.digest());
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String encodeString(String str) {
        try {
            byte[] bytes = str.getBytes();
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(bytes);
            return MD5Util.byteArrayToHexString(instance.digest());
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String byteArrayToHexString(byte[] bArr) {
        char[] cArr = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        char[] cArr2 = new char[(bArr.length * 2)];
        for (int i = 0; i < bArr.length; i++) {
            byte b = bArr[i];
            cArr2[i * 2] = cArr[(b >>> 4) & 15];
            cArr2[(i * 2) + 1] = cArr[b & 15];
        }
        return new String(cArr2);
    }
}
