package com.chat.crazy.base.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author:
 * @Description:
 * @Date: 2023/6/14 上午9:49
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum PackageEnum {
    MONTH_VIP   (1, "月卡会员", BigDecimal.valueOf(29.9), 30),
    SEASON_VIP   (2, "季卡会员", BigDecimal.valueOf(79.9), 180),
    YEAR_VIP   (3, "年卡会员", BigDecimal.valueOf(299.9), 365),
    ERROR   (-1, "异常", null, 0),

    ;
    private int id;
    private String subject;
    private BigDecimal price;
    private int days;

    public static PackageEnum typeOf(Integer id) {
        for (PackageEnum packageEnum : PackageEnum.values()) {
            if (packageEnum.getId() == id) {
                return packageEnum;
            }
        }
        return ERROR;
    }

    public static String getBody(PackageEnum packageEnum) {
        return packageEnum.getSubject() + packageEnum.getPrice() + "元";
    }
}
