package com.chat.crazy.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 用户状态
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum UserStatus {
    INVALID (0, "无效"),
    VALID(1, "有效")
    ;

    private int status;
    private String name;
}
