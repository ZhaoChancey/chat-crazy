package com.chat.crazy.base.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author:
 * @Description: 邀请码类
 */
@Data
@TableName("chat_invite_code")
public class InviteCodeDO {
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 邀请码
     */
    private String inviteCode;

    /**
     * 生成该邀请码的用户ID
     */
    private Integer userId;

    /**
     * 邀请码状态，0：未使用，1：已使用
     */
    private Integer status;

    /**
     * 创建时间
     */
    LocalDateTime createTime;

    /**
     * 更新时间
     */
    LocalDateTime updateTime;
}
