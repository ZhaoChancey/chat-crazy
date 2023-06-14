package com.chat.crazy.base.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 用户状态
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum UserStatusEnum {
    INVALID (0, "无效"),
    VALID(1, "有效")
    ;

    private int status;
    private String name;
}
