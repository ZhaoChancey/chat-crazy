package com.chat.crazy.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author:
 * @Description: 用户类
 * @Date: 2023/4/14 下午4:25
 */
@Data
@TableName("user_info")
public class UserDO {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 状态 1 启用 0 停用
     */
    private Integer status;

    /**
     * 会员类型 0：普通用户，1：会员
     */
    private Integer vipType;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 生效日期
     */
    LocalDateTime startTime;

    /**
     * 失效日期
     */
    LocalDateTime endTime;

    /**
     * 创建时间
     */
    LocalDateTime createTime;

    /**
     * 更新时间
     */
    LocalDateTime updateTime;
}
