package com.chat.crazy.base.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Author:
 * @Description:
 * @Date: 2023/4/24 下午2:56
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum UserTypeEnum {
    COMMON (1, "已登录用户"),
    NOT_LOGIN (0, "未登录用户")
    ;

    private int type;
    private String name;
}