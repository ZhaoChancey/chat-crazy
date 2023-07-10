package com.chat.crazy.base.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author hncboy
 * @date 2023/3/25 16:19
 * 订单实体类
 */
@Data
@TableName("chat_order")
public class OrderDO {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户 id，user_type=0时表示虚拟用户的主键
     */
    private Integer userId;

    /**
     * 用户类型，0：未登录用户，1：已登录用户
     */
    private Integer userType;

    /**
     * 渠道类型，1：支付宝，2：微信
     */
    private Integer paymentType;

    /**
     * 订单id
     */
    private String orderId;

    /**
     * 渠道订单id
     */
    private String transactionId;

    /**
     * 套餐类型
     */
    private Integer packageId;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 订单状态
     */
    private Integer orderStatus;

    /**
     * 通知校验ID
     */
    private String notifyId;

    /**
     * 交易创建时间
     */
    private LocalDateTime gmtCreateTime;

    /**
     * 交易付款时间
     */
    private LocalDateTime gmtPaymentTime;

    /**
     * 创建时间
     */
//    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
//    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
