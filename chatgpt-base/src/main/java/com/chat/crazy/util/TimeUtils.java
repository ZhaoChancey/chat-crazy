package com.chat.crazy.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @Author:
 * @Description:
 * @Date: 2023/4/19 下午1:09
 */
public class TimeUtils {
    
    public static long getMilliSecond(LocalDateTime dateTime) {
        return dateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }
}
