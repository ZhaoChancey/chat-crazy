package com.chat.crazy.base.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author hncboy
 * @date 2023/3/27 21:41
 * 应用相关常量
 */
public interface ApplicationConstant {

    /**
     * ADMIN 路径前缀
     */
    String ADMIN_PATH_PREFIX = "admin";
    String API_PATH_PREFIX = "api";
    
    String TRACE_ID = "trace_id";
    
    String DEFAULT_NICKNAME = "小柴";
}
