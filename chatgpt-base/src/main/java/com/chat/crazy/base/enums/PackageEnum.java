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
    MONTH_VIP   (1, "月卡会员", BigDecimal.valueOf(29.9)),
    SEASON_VIP   (2, "季卡会员", BigDecimal.valueOf(79.9)),
    YEAR_VIP   (3, "年卡会员", BigDecimal.valueOf(299.9)),
    
    ;
    private int id;
    private String subject;
    private BigDecimal price;
    
}
