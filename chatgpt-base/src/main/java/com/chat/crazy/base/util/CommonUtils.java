package com.chat.crazy.base.util;

import java.util.Random;

public class CommonUtils {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    /**
     * 生成6位验证码
     * @return 验证码
     */
    public static String genCode() {
        return String.valueOf((int)((Math.random() * 9 + 1) * Math.pow(10,5)));
    }

    /**
     * 随机生成length长度的字符串
     * @param length
     * @return
     */
    public static String generateRandomString(int length) {
        Random random = new Random();
        StringBuilder builder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            builder.append(CHARACTERS.charAt(index));
        }

        return builder.toString();
    }

    public static void main(String[] args) {
        System.out.println(generateRandomString(8)); // 生成一个长度为8的随机字符串
    }

}
