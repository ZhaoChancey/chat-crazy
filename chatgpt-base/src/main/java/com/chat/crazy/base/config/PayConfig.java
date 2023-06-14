package com.chat.crazy.base.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author:
 * @Description: 支付配置项
 * @Date: 2023/6/9 下午5:46
 */
@Data
@Component
@ConfigurationProperties(prefix = "pay")
public class PayConfig {
    private AliPayConfig aliPay; // 支付宝相关配置
    
    @Data
    public static class AliPayConfig {
        private String paymentType;
        
        private String appId;

        private String privateKey;

        private String alipayPublicKey;

        private String notifyUrl;

        private String gatewayUrl;
    }
}
