package com.chat.crazy.base.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Author:
 * @Description:
 * @Date: 2023/6/14 上午9:49
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum PackageTypeEnum {
    COMMON_PACKAGE   (1, "普通套餐"),
    PLUS_PACKAGE   (2, "plus套餐"),
    NO_PACKAGE   (-1, "无套餐"),
    ;

    private int id;
    private String name;
}
