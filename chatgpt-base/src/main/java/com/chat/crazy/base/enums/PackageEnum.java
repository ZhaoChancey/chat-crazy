package com.chat.crazy.base.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Author:
 * @Description:
 * @Date: 2023/6/14 上午9:49
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum PackageEnum {
    MONTH_VIP   (1, "月卡会员", BigDecimal.valueOf(29.9), 30, PackageTypeEnum.COMMON_PACKAGE),
    SEASON_VIP   (2, "季卡会员", BigDecimal.valueOf(79.9), 180, PackageTypeEnum.COMMON_PACKAGE),
    YEAR_VIP   (3, "年卡会员", BigDecimal.valueOf(299.9), 365, PackageTypeEnum.COMMON_PACKAGE),

    PLUS_MONTH_VIP   (4, "月卡会员", BigDecimal.valueOf(39.9), 30, PackageTypeEnum.PLUS_PACKAGE),
    PLUS_SEASON_VIP   (5, "季卡会员", BigDecimal.valueOf(119.9), 180, PackageTypeEnum.PLUS_PACKAGE),
    PLUS_YEAR_VIP   (6, "年卡会员", BigDecimal.valueOf(399.9), 365, PackageTypeEnum.PLUS_PACKAGE),
    ERROR   (-1, "异常", null, 0, PackageTypeEnum.NO_PACKAGE),

    ;
    private int id;
    private String subject;
    private BigDecimal price;
    private int days;
    private PackageTypeEnum packageType;

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
