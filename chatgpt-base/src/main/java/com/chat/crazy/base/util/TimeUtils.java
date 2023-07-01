package com.chat.crazy.base.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @Author:
 * @Description:
 * @Date: 2023/4/19 下午1:09
 */
public class TimeUtils {
    
    public static long getMilliSecond(LocalDateTime dateTime) {
        return dateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    public static String dateTimeToStr(LocalDateTime dateTime) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(fmt);
    }

    public static LocalDateTime strToDateTime(String time) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(time, fmt);
    }
}
