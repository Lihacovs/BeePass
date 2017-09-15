/*
 * Copyright (C) 2017. Konstantins Lihacovs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lihacovs.android.beepass.utils;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;

/**
 * Simple implementation of AES encryption for data.
 * Every database entry will be encrypted with user master password.
 * Encryption is applied right after string typed to UI, used in Presenter layer classes.
 * Decryption is applied right before string comes to UI, used in Presenter layer classes.
 */

public class EncryptionHelper {

    private static byte[] encrypt(char[] keyChars, byte[] textBytes)
            throws java.io.UnsupportedEncodingException,
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            InvalidAlgorithmParameterException,
            IllegalBlockSizeException,
            BadPaddingException,
            InvalidKeySpecException {
        //Generates key for AES encryption. Since generated key is not stored, we can pass
        //dumb predefined salt and iv for generation. Assume to use proper salt, Iv
        // and iteration count in different implementation;
        byte[] salt = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        byte[] ivBytes = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        KeySpec spec = new PBEKeySpec(keyChars, salt, 128, 128);
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        Key key = f.generateSecret(spec);

        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        //SecretKeySpec newKey = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        return cipher.doFinal(textBytes);
    }

    private static byte[] decrypt(char[] keyChars, byte[] textBytes)
            throws java.io.UnsupportedEncodingException,
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            InvalidAlgorithmParameterException,
            IllegalBlockSizeException,
            BadPaddingException, InvalidKeySpecException {
        byte[] salt = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        byte[] ivBytes = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

        KeySpec spec = new PBEKeySpec(keyChars, salt, 128, 128);
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        Key key = f.generateSecret(spec);

        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        //SecretKeySpec newKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        return cipher.doFinal(textBytes);
    }

    public static String encryptText(String key, String text) {
        // if encryption is not successful return text
        String encryptedText = text;
        if (text != null) {
            try {
                //byte array arguments to pass to encrypt() method
                char[] keyChars = key.toCharArray();
                byte[] textBytes = text.getBytes("UTF-8");

                //Encryption
                byte[] encryptedBytes = EncryptionHelper.encrypt(keyChars, textBytes);
                //Convert bytes to string with Base64
                encryptedText = Base64.encodeToString(encryptedBytes, Base64.DEFAULT);

            } catch (NoSuchAlgorithmException |
                    InvalidKeyException |
                    InvalidAlgorithmParameterException |
                    NoSuchPaddingException |
                    BadPaddingException |
                    UnsupportedEncodingException |
                    InvalidKeySpecException|
                    IllegalBlockSizeException e) {
                Log.e("encryption", e.toString());
                e.printStackTrace();
            }
        }
        return encryptedText;
    }

    public static String decryptText(String key, String text) {
        // if decryption is not successful return encrypted text
        String decryptedText = text;
        if (text != null) {
            try {
                //byte array arguments to pass to encrypt() method
                char[] keyChars = key.toCharArray();
                byte[] base64TextBytes = text.getBytes("UTF-8");

                //TODO: check incorrect == padding throwing illegal argument exception
                byte[] encryptedBytes = Base64.decode(base64TextBytes, Base64.DEFAULT);

                //Decryption
                byte[] decryptedBytes = EncryptionHelper.decrypt(keyChars, encryptedBytes);
                //Convert bytes to string
                decryptedText = new String(decryptedBytes, "UTF-8");

            } catch (NoSuchAlgorithmException |
                    InvalidKeyException |
                    InvalidAlgorithmParameterException |
                    NoSuchPaddingException |
                    BadPaddingException |
                    UnsupportedEncodingException |
                    IllegalBlockSizeException |
                    IllegalArgumentException|
                    InvalidKeySpecException e) {
                e.printStackTrace();
            }
        }
        return decryptedText;
    }
}