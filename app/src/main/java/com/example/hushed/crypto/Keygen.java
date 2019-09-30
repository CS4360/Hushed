package com.example.hushed.crypto;

import java.security.SecureRandom;

import org.whispersystems.curve25519.java.Sha512;
import org.whispersystems.curve25519.java.curve_sigs;
import org.whispersystems.curve25519.java.scalarmult;

public class Keygen {

    private static SecureRandom random = new SecureRandom();

    public static byte[] generatePublicKey(byte[] privateKey) {
        byte[] bytes = new byte[32];
        curve_sigs.curve25519_keygen(bytes, privateKey);
        return bytes;
    }

    public static byte[] generateSharedKey(byte[] localPrivateKey, byte[] remotePublicKey) {
        byte[] bytes = new byte[32];
        scalarmult.crypto_scalarmult(bytes, localPrivateKey, remotePublicKey);
        return bytes;
    }

    public static byte[] generatePrivateKey() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        bytes[0] &= 0xF8;  // 248 (1111 1000)
        bytes[31] &= 0x7F; // 127 (0111 1111)
        bytes[31] |= 0x40; // 64  (0100 0000)

        return bytes;
    }

}
