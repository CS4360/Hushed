package com.example.hushed;

import java.util.Base64;

import com.example.hushed.crypto.EncDec;
import com.example.hushed.crypto.KeyPair;
import com.example.hushed.crypto.Keygen;


import org.junit.Assert;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoTester {

    @Test
    public void testEncryptDecrypt() {
        try {
            // Generate random private/public keypairs.
            byte[] privateA = Keygen.generatePrivateKey();
            byte[] privateB = Keygen.generatePrivateKey();
            byte[] publicA = Keygen.generatePublicKey(privateA);
            byte[] publicB = Keygen.generatePublicKey(privateB);
            KeyPair pairA = new KeyPair(publicA, privateA);
            KeyPair pairB = new KeyPair(publicB, privateB);

            // Derive shared secret keys
            byte[] sharedA = Keygen.generateSharedKey(pairA.getPrivateKey(), pairB.getPublicKey());
            byte[] sharedB = Keygen.generateSharedKey(pairB.getPrivateKey(), pairA.getPublicKey());

            // Make sure that two sides can derive the same shared key
            Assert.assertArrayEquals(sharedA, sharedB);

            // Create cipher instances
            Cipher enc = Cipher.getInstance("AES/CBC/PKCS5Padding");
            Cipher dec = Cipher.getInstance("AES/CBC/PKCS5Padding");

            // Derive AES keys for shared key
            SecretKeySpec sharedKey1 = EncDec.deriveCipherKey(sharedA);
            SecretKeySpec sharedKey2 = EncDec.deriveCipherKey(sharedB);

            SecretKeySpec privateKey1 = EncDec.deriveCipherKey(privateA);
            SecretKeySpec publicKey1 = EncDec.deriveCipherKey(publicA);

            // Simulate sending a message: "Hi bob"
            // This would be entered by the user
            String message = "Hi bob";
            // Convert message to bytes
            byte[] messageBytes = message.getBytes();

            // Encrypt the message, first using sender's private key
            //enc.init(Cipher.ENCRYPT_MODE, privateKey1);
            //byte[] signedMessageBytes = EncDec.encrypt(enc, messageBytes);
            // Encrypt the message again, using the shared private key
            enc.init(Cipher.ENCRYPT_MODE, sharedKey1);
            byte[] encryptedMessageBytes = EncDec.encrypt(enc, messageBytes);

            // Encode to base64, this is what would be stored in the database
            String base64EncodedEncryptedMessage = Base64.getEncoder().encodeToString(encryptedMessageBytes);

            // We have to decrypt in the opposite order...
            byte[] decodedEncryptedMessageBytes = Base64.getDecoder().decode(base64EncodedEncryptedMessage);

            // Decrypt the encrypted message, first use the shared private key
            // Decryption requires an extra parameter to be set when initializing the Cipher
            IvParameterSpec iv = new IvParameterSpec(decodedEncryptedMessageBytes, 0, dec.getBlockSize());
            dec.init(Cipher.DECRYPT_MODE, sharedKey2, iv);
            byte[] decryptedMessageBytes = EncDec.decrypt(dec, decodedEncryptedMessageBytes);

            //IvParameterSpec iv2 = new IvParameterSpec(decryptedSignedMessageBytes, 0, dec.getBlockSize());
            //dec.init(Cipher.DECRYPT_MODE, publicKey1, iv2);
            //byte[] decryptedMessageBytes = EncDec.decrypt(dec, decryptedSignedMessageBytes);


            // Conver the decrypted message back into a String
            String result = new String(decryptedMessageBytes);

            // Check that we got the correct message after decryption:
            if (!result.equals(message)) {
                throw new RuntimeException("Encrypted/Decrypted Result messaged did not equal original message");
            }
            //*/
        } catch (Exception e) { throw new RuntimeException(e); }
    }


}
