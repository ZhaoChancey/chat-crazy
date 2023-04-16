package com.chat.crazy.service.impl;

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

    public void setPhoneCode(String phone, String code) {
        chatRedisTemplate.opsForValue().set(phone, code, PHONE_CODE_VALID_TIME, TimeUnit.SECONDS);
    }

    public String getPhoneCode(String phone) {
        return chatRedisTemplate.opsForValue().get(phone);
    }

    //TODO: 黑名单，让token失效
}
