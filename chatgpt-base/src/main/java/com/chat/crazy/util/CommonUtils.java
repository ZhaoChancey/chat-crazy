package com.chat.crazy.util;

public class CommonUtils {

    public static String genCode() {
        return String.valueOf((int)((Math.random() * 9 + 1) * Math.pow(10,5)));
    }
}
