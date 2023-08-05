package com.chat.crazy.front.domain.vo.pay;

import com.chat.crazy.base.domain.query.ChatRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Author:
 * @Description:
 * @Date: 2023/6/13 下午7:40
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(title = "套餐信息响应")
public class PayPackageVO {
    @Schema(title = "扩展信息")
    private String extendStr;
    
    @Schema(title = "套餐元信息")
    private Map<String, List<PayPackageItem>> packages;
    
    @Data
    @Builder
    public static class PayPackageItem {
        @Schema(title = "id")
        private Integer id;
        
        @Schema(title = "套餐类型")
        private String subject;
        
        @Schema(title = "套餐价格")
        private BigDecimal price;
    }
}
