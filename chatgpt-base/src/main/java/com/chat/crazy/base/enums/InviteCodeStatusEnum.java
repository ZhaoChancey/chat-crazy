package com.chat.crazy.base.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 邀请码状态
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum InviteCodeStatusEnum {
    VALID (0, "未使用"),
    INVALID(1, "已使用")
    ;

    private int status;
    private String name;
}
