package com.chat.crazy.base.constant;

/**
 * Redis相关常量
 */
public interface RedisConstant {

    // 验证码5分钟有效期
    Integer PHONE_CODE_VALID_TIME = 300;
    String PHONE_NUMBER_KEY_SUFFIX = "chat:phone:";

    /**
     * redis-key-名称-limit-一个时间窗口内请求的数量累计(限流累计请求数)
     */
    String LIMIT_REQUEST_SUFFIX = "chat:limit:request:";

    /**
     * redis-key-名称-limit-一个时间窗口开始时间(限流开始时间)
     */
    String LIMIT_TIME_SUFFIX = "chat:limit:time:";

    /**
     * 每小时最多验证码发送次数
     */
    String PHONE_MAX_REQUEST_HOUR = "5";

    /**
     * 每天最多验证码发送次数
     */
    String PHONE_MAX_REQUEST_DAY = "10";
    
    /**
     * 小时级时间窗口(毫秒)
     */
    String MAX_REQUEST_TIME_HOUR = "3600000";

    /**
     * 天级时间窗口(毫秒)
     */
    String MAX_REQUEST_TIME_DAY = "86400000";

    /**
     * 每小时聊天次数上限 
     */
    String CHAT_MAX_REQ_HOUR = "25";
}
