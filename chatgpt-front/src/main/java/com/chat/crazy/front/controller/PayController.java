package com.chat.crazy.front.controller;

import com.chat.crazy.base.annotation.FrontPreAuth;
import com.chat.crazy.base.config.ServerConfig;
import com.chat.crazy.base.handler.response.R;
import com.chat.crazy.front.domain.request.pay.PayOrderRequest;
import com.chat.crazy.front.domain.request.pay.PayPreCreateRequest;
import com.chat.crazy.front.domain.vo.pay.PayOrderStatusVO;
import com.chat.crazy.front.domain.vo.pay.PayPackageVO;
import com.chat.crazy.front.domain.vo.pay.PayPreCreateVO;
import com.chat.crazy.front.service.PayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author:
 * @Description:
 * @Date: 2023/6/9 下午2:21
 */
@RestController
@Tag(name = "支付相关接口")
@RequestMapping("/pay")
public class PayController {
    @Resource
    PayService payService;
    
    @Resource
    ServerConfig serverConfig;
    
    @PostMapping("/package")
    @Operation(summary = "获取套餐信息")
    @FrontPreAuth
    public R<PayPackageVO> getPackageInfo() {
        return R.data(payService.getPackageInfo());
    }
    
    @PostMapping("/precreate")
    @Operation(summary = "获取二维码链接")
    @FrontPreAuth
    public R<PayPreCreateVO> orderPreCreate(@RequestBody PayPreCreateRequest request) {
        return null;
    }
    
    @PostMapping("/status")
    @Operation(summary = "获取支付状态")
    @FrontPreAuth
    public R<PayOrderStatusVO> getOrderStatus(@RequestBody PayOrderRequest request) {
        return null;

    }
    
    @PostMapping("/order/cancel")
    @Operation(summary = "主动关闭订单")
    @FrontPreAuth
    public R<String> cancelOrder(@RequestBody PayOrderRequest request) {
        return null;
    }
}
