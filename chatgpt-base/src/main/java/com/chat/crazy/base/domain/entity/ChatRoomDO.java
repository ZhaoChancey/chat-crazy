package com.chat.crazy.base.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chat.crazy.base.enums.ApiTypeEnum;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author hncboy
 * @date 2023/3/25 16:14
 * 聊天室表实体类
 */
@Data
@TableName("chat_room")
public class ChatRoomDO {

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
     * 对话 id
     * 唯一
     */
    private String conversationId;

    /**
     * ip
     */
    private String ip;

    /**
     * 第一条消息主键
     * 唯一
     */
    private Long firstChatMessageId;

    /**
     * 第一条消息 id
     * 唯一
     */
    private String firstMessageId;

    /**
     * 对话标题
     */
    private String title;
//
//    /**
//     * API 类型
//     * 不同类型的对话不能一起存储
//     */
//    private ApiTypeEnum apiType;

    /**
     * 聊天室状态，0：删除，1：正常存在
     */
    private Integer status;
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
