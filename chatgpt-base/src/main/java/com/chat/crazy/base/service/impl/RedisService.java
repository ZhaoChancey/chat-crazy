package com.chat.crazy.base.service.impl;

import com.chat.crazy.base.exception.ServiceException;
import com.chat.crazy.base.util.RedisLimitUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisService {
    @Resource(name = "chatRedisTemplate")
    private RedisTemplate<String, String> chatRedisTemplate;

    /**
     * 自定义参数限流(自定义多少时间限制多少请求)字符串脚本
     */
    private static final String LIMIT_CUSTOM_SCRIPT = getScript("redis/limit-custom.lua");

    /**
     * 设置key
     * @param key redis-key
     * @param value redis-value
     * @param ttl 过期时间
     * @param timeUnit 单位
     */
    public void setValue(String key, String value, Integer ttl, TimeUnit timeUnit) {
        chatRedisTemplate.opsForValue().set(key, value, ttl, timeUnit);
    }

    public String getValue(String key) {
        return chatRedisTemplate.opsForValue().get(key);
    }

    public void deleteKey(String key) {
        chatRedisTemplate.delete(key);
    }

    public void decrementKey(String key) {
        chatRedisTemplate.opsForValue().decrement(key);
    }
    /**
     * 自定义参数限流判断(自定义多少时间限制多少请求)
     *
     * @param limitTimeKey 时间窗口key 
     * @param limitRequestKey 请求数量key 
     * @param maxRequest 限流最大请求数
     * @param timeRequest 一个时间窗口(毫秒)
     * @return  返回值>0说明请求有效，反之限流
     */
    public Long limit(String limitTimeKey, String limitRequestKey, String maxRequest, String timeRequest) {
        // 获取key名，一个时间窗口开始时间(限流开始时间)和一个时间窗口内请求的数量累计(限流累计请求数)
        List<String> keys = new ArrayList<>();
        keys.add(limitTimeKey);
        keys.add(limitRequestKey);
        // 传入参数，限流最大请求数，当前时间戳，一个时间窗口时间(毫秒)(限流时间)
//        List<String> args = new ArrayList<>();
//        args.add(maxRequest);
//        args.add(String.valueOf(System.currentTimeMillis()));
//        args.add(timeRequest);
        return execute(LIMIT_CUSTOM_SCRIPT, keys, maxRequest, String.valueOf(System.currentTimeMillis()), timeRequest);
    }

    /**
     * 执行Lua脚本方法
     *
     * @param script
     * @param keys
     * @param args
     * @return java.lang.Object
     * @throws
     * @author wliduo[i@dolyw.com]
     * @date 2019/11/26 10:50
     */
    private Long execute(String script, List<String> keys, Object... args) {
        // 执行脚本
        RedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        Long res = 0L;
        try {
            res = chatRedisTemplate.execute(redisScript, keys, args);
        } catch (Exception e) {
            log.error("Redis脚本执行异常，keys: {}, args: {}, error: {}", keys, args, ExceptionUtils.getStackTrace(e));
        }
        // 结果请求数大于0说明不被限流
        return res;
    }

    /**
     * 获取Lua脚本
     *
     * @param path
     * @return java.lang.String
     * @throws
     * @author wliduo[i@dolyw.com]
     * @date 2019/11/25 17:57
     */
    private static String getScript(String path) {
        StringBuilder stringBuilder = new StringBuilder();
        InputStream inputStream = RedisLimitUtil.class.getClassLoader().getResourceAsStream(path);
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                stringBuilder.append(str).append(System.lineSeparator());
            }
        } catch (IOException e) {
            log.error(Arrays.toString(e.getStackTrace()));
            throw new ServiceException("获取Lua限流脚本出现问题: " + Arrays.toString(e.getStackTrace()));
        }
        return stringBuilder.toString();
    }
}
