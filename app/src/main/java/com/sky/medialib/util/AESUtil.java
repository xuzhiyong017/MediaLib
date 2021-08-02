package com.sky.medialib.util;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {
    private final Cipher mCipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
    private final SecretKeySpec secretKeySpec;
    private AlgorithmParameterSpec parameterSpec;


    public AESUtil() throws Exception {
        MessageDigest instance = MessageDigest.getInstance("SHA-256");
        instance.update("YzM2PbrUa2CxF78r".getBytes("UTF-8"));
        byte[] obj = new byte[32];
        System.arraycopy(instance.digest(), 0, obj, 0, obj.length);
        this.secretKeySpec = new SecretKeySpec(obj, "AES");
        this.parameterSpec = createParameterSpec();
    }

    private AlgorithmParameterSpec createParameterSpec() {
        return new IvParameterSpec(new byte[]{(byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0});
    }

    public String decodeString(String str) {
        try {
            this.mCipher.init(2, this.secretKeySpec, this.parameterSpec);
            return new String(this.mCipher.doFinal(Base64.decode(str, 0)), "UTF-8");
        } catch (Throwable e) {
            return str;
        }
    }
}
