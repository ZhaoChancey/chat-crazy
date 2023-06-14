package com.chat.crazy.base.domain.bo;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author:
 * @Description:
 * @Date: 2023/6/12 下午7:14
 */
@Data
public class AlipayPreCreateClientRequest {
    // 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
    // 需保证商户系统端不能重复，建议通过数据库sequence生成
    @SerializedName("out_trade_no")
    private String outTradeNo;

    // 订单标题，粗略描述用户的支付目的。如“喜士多（浦东店）消费”
    private String subject;

    // 订单总金额，整形，此处单位为元，精确到小数点后2位，不能超过1亿元
    @SerializedName("total_amount")
    private BigDecimal totalAmount;

    // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
    private String body;

    // (推荐使用，相对时间) 支付超时时间，5m 5分钟
    @SerializedName("timeout_express")
    private String timeoutExpress;
}
