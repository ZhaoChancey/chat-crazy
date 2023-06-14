package com.chat.crazy.base.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author:
 * @Description:
 * @Date: 2023/6/12 下午4:30
 */
@Component
public class ServerConfig {
    
    @Value("${server.port}")
    private Integer port;
    
    public Integer getPort() {
        return port;
    }
}
