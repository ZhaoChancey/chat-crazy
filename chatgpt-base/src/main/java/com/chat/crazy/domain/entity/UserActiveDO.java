package com.chat.crazy.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author:
 * @Description: 用户登录信息实体类
 * @Date: 2023/4/14 下午4:25
 */
@Data
@TableName("user_active_info")
public class UserActiveDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * ip地址
     */
    private String ip;

    /**
     * 登录/退出状态，0: 登录，1：退出登录
     */
    private Integer activeType;

    /**
     * 登录/退出时间
     */
    LocalDateTime activeTime;
    /**
     * 创建时间
     */
    LocalDateTime createTime;
}
