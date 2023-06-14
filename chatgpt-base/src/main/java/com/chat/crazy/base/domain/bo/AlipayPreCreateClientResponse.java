package com.chat.crazy.base.domain.bo;

import com.alipay.api.internal.mapping.ApiField;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author:
 * @Description:
 * @Date: 2023/6/12 下午7:14
 */
@Data
public class AlipayPreCreateClientResponse {
    @ApiField("out_trade_no")
    private String outTradeNo;

    /**
     * 当前预下单请求生成的二维码码串，可以用二维码生成工具根据该码串值生成对应的二维码
     */
    @ApiField("qr_code")
    private String qrCode;
}
