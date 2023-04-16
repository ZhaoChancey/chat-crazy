package com.chat.crazy.util;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Slf4j
public class Md5Util {

    private static final String[] strHex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    public static String getMD5(String source) {
        try {
            byte[] data = source.getBytes(StandardCharsets.UTF_8);
            MessageDigest digest = MessageDigest.getInstance("MD5");
            return buffer2Hex(digest.digest(data));
        } catch (Exception e) {
            log.error("Md5Util getMD5 occur error", e);
        }
        return null;
    }

    private static String buffer2Hex(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (int datum : data) {
            int d = datum;
            if (d < 0) {
                d += 256;
            }
            int d1 = d / 16;
            int d2 = d % 16;
            sb.append(strHex[d1]).append(strHex[d2]);
        }
        return sb.toString();
    }
}
