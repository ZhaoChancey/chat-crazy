package com.chat.crazy.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author:
 * @Description: 虚拟用户实体类
 * @Date: 2023/4/14 下午4:25
 */
@Data
@TableName("virtual_user")
public class VirtualUserDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 虚拟id 
     */
    private String virId;

    /**
     * 状态 1 启用 0 停用
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
