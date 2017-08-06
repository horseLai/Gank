package com.example.horselai.gank.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by laixiaolong on 2016/12/26.
 */

public class EncryptionUtil
{

    public static String generateMd5(String in)
    {
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            final byte[] result = md.digest(in.getBytes());
            return new BigInteger(result).toString(32);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return String.valueOf(in.hashCode());
        }

    }

}
