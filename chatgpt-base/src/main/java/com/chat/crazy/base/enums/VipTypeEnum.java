package com.chat.crazy.base.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 用户类型
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum VipTypeEnum {
    NOT_LOGIN   (-1, "未登录"),
    NORMAL  (0, "普通用户"),
    VIP   (1, "付费用户"),
    ;

    private int type;
    private String name;
}
