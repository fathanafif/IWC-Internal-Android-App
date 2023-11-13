package com.iwc.iwctablet.utility;

import android.util.Log;

import java.security.SecureRandom;
import java.util.Random;

public class RandomIDGenerator {
    public static final char[] CHARSET_aZ = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    public static final char[] CHARSET_AZ_09 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
    public static final char[] CHARSET_HEX = "0123456789ABCDEF".toCharArray();
    public static final char[] CHARSET_SPECIAL = { '!', 'A', 'B' };

    public static String randomString(char[] characterSet, int length) {
        Random random = new SecureRandom();
        char[] result = new char[length];
        for (int i = 0; i < result.length; i++) {
            // picks a random index out of character set > random character
            int randomCharIndex = random.nextInt(characterSet.length);
            result[i] = characterSet[randomCharIndex];
        }
        return new String(result);
    }

    public static void main(String[] args) {
//        System.out.println("using a-z A-Z");
//        for (int i = 0; i < 5; i++)
//            System.out.println(randomString(CHARSET_aZ, 6));

//        System.out.println("\nusing A-Z 0-9");
        for (int i = 0; i < 5; i++)
            Log.d("angie", randomString(CHARSET_AZ_09, 6));
//
//        System.out.println("\nusing HEX");
//        for (int i = 0; i < 5; i++)
//            System.out.println(randomString(CHARSET_HEX, 6));
//
//        System.out.println("\nusing SPECIAL");
//        for (int i = 0; i < 5; i++)
//            System.out.println(randomString(CHARSET_SPECIAL, 6));
//
//        System.out.println("\nincreasing length");
//        for (int i = 0; i < 10; i++)
//            System.out.println("len " + i + " => " + randomString(CHARSET_AZ_09, i));
    }
}
