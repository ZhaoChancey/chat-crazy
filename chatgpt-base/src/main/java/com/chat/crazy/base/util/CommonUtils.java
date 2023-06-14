package com.chat.crazy.base.util;

public class CommonUtils {

    /**
     * 生成6位验证码
     * @return 验证码
     */
    public static String genCode() {
        return String.valueOf((int)((Math.random() * 9 + 1) * Math.pow(10,5)));
    }

}
