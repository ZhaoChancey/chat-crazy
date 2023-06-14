package com.chat.crazy.front.domain.request.pay;

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
@EqualsAndHashCode(callSuper = false)
@Schema(title = "订单二维码获取请求")
public class PayPreCreateRequest extends ChatRequest {
    
    @Schema(title = "套餐类型")
    private Integer packageId;
}
