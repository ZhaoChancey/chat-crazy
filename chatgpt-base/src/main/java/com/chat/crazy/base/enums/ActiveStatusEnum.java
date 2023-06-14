package com.chat.crazy.base.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 用户登录状态
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ActiveStatusEnum {
    LOGIN (0, "登录"),
    EXIT  (1, "退出登录")
    ;

    private int status;
    private String name;
}
