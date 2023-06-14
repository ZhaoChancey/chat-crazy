package com.chat.crazy.front.domain.vo.pay;

import com.chat.crazy.base.domain.query.ChatRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author:
 * @Description:
 * @Date: 2023/6/14 上午9:43
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(title = "订单状态查询响应")
public class PayOrderStatusVO extends ChatRequest {
    
    @Schema(title = "订单状态")
    private Integer tradeStatus;
}
