package com.chat.crazy.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
    @Resource(name = "chatRedisTemplate")
    private RedisTemplate<String, String> chatRedisTemplate;

    // 验证码10分钟
    private final Integer PHONE_CODE_VALID_TIME = 600;
    private final String PHONE_NUMBER_KEY_SUFFIX = "chat:phone:";
    private final String PHONE_RECORD_HOUR_KEY_SUFFIX = "chat:phone:hour";
    private final String PHONE_RECORD_DAY_KEY_SUFFIX = "chat:phone:day";

    /**
     * 存储验证码和验证码的发送记录
     * @param phone 手机号
     * @param code 验证码
     */
    public void setPhoneCode(String phone, String code) {
        chatRedisTemplate.opsForValue().set(PHONE_NUMBER_KEY_SUFFIX + phone, code, PHONE_CODE_VALID_TIME, TimeUnit.SECONDS);
        // 每小时不得超过5次
        if (Boolean.TRUE.equals(chatRedisTemplate.hasKey(PHONE_RECORD_HOUR_KEY_SUFFIX + phone))) {
            chatRedisTemplate.opsForValue().increment(PHONE_RECORD_HOUR_KEY_SUFFIX + phone);
        } else {
            chatRedisTemplate.opsForValue().set(PHONE_RECORD_HOUR_KEY_SUFFIX + phone, "1", 1, TimeUnit.HOURS);
        }

        // 每天不得超过10次
        if (Boolean.TRUE.equals(chatRedisTemplate.hasKey(PHONE_RECORD_DAY_KEY_SUFFIX + phone))) {
            chatRedisTemplate.opsForValue().increment(PHONE_RECORD_DAY_KEY_SUFFIX + phone);
        } else {
            chatRedisTemplate.opsForValue().set(PHONE_RECORD_DAY_KEY_SUFFIX + phone, "1", 1, TimeUnit.DAYS);
        }
    }

    /**
     * 获取手机验证码
     * @param phone
     * @return
     */
    public String getPhoneCode(String phone) {
        return chatRedisTemplate.opsForValue().get(PHONE_NUMBER_KEY_SUFFIX + phone);
    }

    /**
     * 使用后的验证码失效
     * @param phone
     */
    public void deletePhoneCode(String phone) {
        chatRedisTemplate.delete(PHONE_NUMBER_KEY_SUFFIX + phone);
    }

    public int getHourSendRecord(String phone) {
        String hourCnt = chatRedisTemplate.opsForValue().get(PHONE_RECORD_HOUR_KEY_SUFFIX + phone);
        return StringUtils.isNotEmpty(hourCnt) ? Integer.parseInt(hourCnt) : 0;
    }

    public int getDaySendRecord(String phone) {
        String dayCnt = chatRedisTemplate.opsForValue().get(PHONE_RECORD_DAY_KEY_SUFFIX + phone);
        return StringUtils.isNotEmpty(dayCnt) ? Integer.parseInt(dayCnt) : 0;
    } 
}
