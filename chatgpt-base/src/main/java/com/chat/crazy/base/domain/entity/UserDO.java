package com.chat.crazy.base.domain.entity;

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
    private Integer id;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 状态 1 启用 0 停用
     */
    private Integer status;


    /**
     * 昵称
     */
    private String nickName;

    /**
     * 登录用户-生效日期
     */
    LocalDateTime startTime;

    /**
     * 登录用户-失效日期
     */
    LocalDateTime endTime;

    /**
     * 邀请码ID
     */
    Integer inviteCodeId;

    /**
     * 是否具备生成邀请码能力,1:具备
     */
    Integer isInvitePerm;
    /**
     * vip用户-生效日期
     */
    LocalDateTime vipStartTime;

    /**
     * vip用户-失效日期
     */
    LocalDateTime vipEndTime;

    /**
     * 套餐类型，1：普通，2：plus
     */
    Integer packageType;

    /**
     * 创建时间
     */
    LocalDateTime createTime;

    /**
     * 更新时间
     */
    LocalDateTime updateTime;
}
