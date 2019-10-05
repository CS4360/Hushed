package com.example.hushed.crypto;

public class KeyPair {
    private byte[] publicKey;
    private byte[] privateKey;
    public KeyPair(byte[] pub, byte[] priv) {
        publicKey = pub;
        privateKey = priv;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public byte[] getPrivateKey() {
        return privateKey;
    }
}
