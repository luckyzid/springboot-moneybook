package kr.money.book.utils;

import java.util.concurrent.ThreadLocalRandom;

public class StringUtil {

    public static String generateRandomString(int length) {
        final String characters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final StringBuilder randomString = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = ThreadLocalRandom.current().nextInt(characters.length());
            char randomChar = characters.charAt(index);
            randomString.append(randomChar);
        }
        return randomString.toString();
    }
}
