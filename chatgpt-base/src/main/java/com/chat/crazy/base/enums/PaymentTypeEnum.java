package com.chat.crazy.base.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** 支付渠道枚举
 * @Author:
 * @Description:
 * @Date: 2023/6/14 上午9:49
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum PaymentTypeEnum {
    ALI   ("1", "支付宝"),
    ;
    private String type;
    private String name;
}
