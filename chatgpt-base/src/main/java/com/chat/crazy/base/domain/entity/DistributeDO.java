package com.chat.crazy.base.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Author:
 * @Description:
 * @Date: 2023/6/12 下午4:38
 */
@Data
@TableName("chat_distribute")
public class DistributeDO {
    private Integer id;
    
    private String port;
    
    private Integer workerId;
    
    private Integer datacenterId;
}
