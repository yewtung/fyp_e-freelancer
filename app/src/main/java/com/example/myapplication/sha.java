package com.example.myapplication;

import java.security.MessageDigest;

public class sha {
    public static byte[] encryptSHA(byte[] data, String shaS) throws Exception{
        MessageDigest sha = MessageDigest.getInstance(shaS);
        sha.update(data);
        return sha.digest();

    }
}
