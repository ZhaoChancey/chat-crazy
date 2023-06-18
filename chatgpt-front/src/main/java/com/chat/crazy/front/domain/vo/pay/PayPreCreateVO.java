package com.chat.crazy.front.domain.vo.pay;

import com.chat.crazy.base.domain.query.ChatRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author:
 * @Description:
 * @Date: 2023/6/14 上午9:38
 */
@Data
@Schema(title = "订单二维码获取响应")
public class PayPreCreateVO {
    
    @Schema(title = "支付订单id")
    private String orderId;
    
    @Schema(title = "二维码码串")
    private String qrCode;
}
