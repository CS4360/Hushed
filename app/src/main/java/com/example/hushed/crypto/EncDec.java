package com.example.hushed.crypto;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class EncDec {
    public static byte[] encrypt(Cipher cipher, byte[] data) {
        try {
            byte[] encrypted = cipher.doFinal(data);
            byte[] ivParamSpec = cipher.getIV();

            byte[] ivAndBody = new byte[ivParamSpec.length + encrypted.length];
            System.arraycopy(ivParamSpec, 0, ivAndBody, 0, ivParamSpec.length);
            System.arraycopy(encrypted, 0, ivAndBody, ivParamSpec.length, encrypted.length);

            return ivAndBody;
        }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    public static byte[] decrypt(Cipher cipher, byte[] encrypted) {
        try {
            return cipher.doFinal(encrypted, cipher.getBlockSize(), encrypted.length - cipher.getBlockSize());
        }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    public static SecretKeySpec deriveMacKey(byte[] secretBytes) {
        byte[] digestedBytes = getDigestedBytes(secretBytes, 1);
        byte[] macKeyBytes   = new byte[20];

        System.arraycopy(digestedBytes, 0, macKeyBytes, 0, macKeyBytes.length);
        return new SecretKeySpec(macKeyBytes, "HmacSHA1");
    }

    public static SecretKeySpec deriveCipherKey(byte[] secretBytes) {
        byte[] digestedBytes  = getDigestedBytes(secretBytes, 0);
        byte[] cipherKeyBytes = new byte[16];

        System.arraycopy(digestedBytes, 0, cipherKeyBytes, 0, cipherKeyBytes.length);
        return new SecretKeySpec(cipherKeyBytes, "AES");
    }



    private static byte[] getDigestedBytes(byte[] secretBytes, int iteration) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretBytes, "HmacSHA256"));
            return mac.doFinal(intToByteArray(iteration));
        }
        catch (NoSuchAlgorithmException | java.security.InvalidKeyException e) {
            throw new AssertionError(e);
        }
    }

    private static byte[] intToByteArray(int value) {
        byte[] bytes = new byte[4];
        intToByteArray(bytes, 0, value);
        return bytes;
    }

    private static int intToByteArray(byte[] bytes, int offset, int value) {
        bytes[offset + 3] = (byte)value;
        bytes[offset + 2] = (byte)(value >> 8);
        bytes[offset + 1] = (byte)(value >> 16);
        bytes[offset]     = (byte)(value >> 24);
        return 4;
    }
}
