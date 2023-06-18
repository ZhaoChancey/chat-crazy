package com.chat.crazy.base.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 支付状态
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum TradeStatusEnum {
    TRADE_SUCCESS  (1000, "TRADE_SUCCESS", "交易支付成功"),
    WAIT_BUYER_PAY (1001, "WAIT_BUYER_PAY", "交易创建，等待买家付款"),
    TRADE_FINISHED  (1002, "TRADE_FINISHED", "交易结束，不可退款"),
    TRADE_CLOSED  (1003, "TRADE_CLOSED", "未付款交易超时关闭，或支付完成后全额退款"),
    ERROR  (9999, "error", "error"),
    ;

    private int status;
    private String aliStatus;
    private String desc;
}
