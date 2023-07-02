package com.chat.crazy.base.client;

import cn.hutool.json.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeCancelRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeCancelResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.chat.crazy.base.config.PayConfig;
import com.chat.crazy.base.domain.bo.AlipayPreCreateClientRequest;
import com.chat.crazy.base.exception.ServiceException;
import com.chat.crazy.base.handler.response.ResultCode;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class AliPayClient {

    @Resource
    private PayConfig payConfig;

    private final Gson gson = new Gson();
    public AlipayTradePrecreateResponse tradePreCreate(AlipayPreCreateClientRequest preCreateBo) {
        log.info("订单创建请求：{}", preCreateBo);
        PayConfig.AliPayConfig aliPayConfig = payConfig.getAliPay();
        AlipayClient alipayClient = new DefaultAlipayClient(aliPayConfig.getGatewayUrl(), aliPayConfig.getAppId(),
                aliPayConfig.getPrivateKey(), "json", "UTF-8",
                aliPayConfig.getAlipayPublicKey(), "RSA2");

        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        request.setNotifyUrl(aliPayConfig.getNotifyUrl());
        request.setBizContent(gson.toJson(preCreateBo));
        AlipayTradePrecreateResponse result = null;
        try {
            result = alipayClient.execute(request);
        } catch (Exception e) {
            log.error("aplipay precreate bo: {}, error: {}", preCreateBo, ExceptionUtils.getMessage(e));
            throw new ServiceException(ResultCode.INTERNAL_SERVER_ERROR.getMessage());
        }
        if (result.isSuccess()) {
            log.info("aplipay paycreate execute success: {}", result);
        } else {
            log.error("aplipay precreate execute service exception: {}", result);
            throw new ServiceException(ResultCode.ALI_TRADE_PRE_CREATE_ERROR.getMessage());
        }
        return result;
    }

    public AlipayTradeQueryResponse getOrderStatus(String orderId, boolean isLocal) {
        PayConfig.AliPayConfig aliPayConfig = payConfig.getAliPay();
        AlipayClient alipayClient = new DefaultAlipayClient(aliPayConfig.getGatewayUrl(), aliPayConfig.getAppId(),
                aliPayConfig.getPrivateKey(), "json", "UTF-8",
                aliPayConfig.getAlipayPublicKey(), "RSA2");
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JsonObject jsonObject = new JsonObject();
        if (isLocal) {
            jsonObject.addProperty("out_trade_no", orderId);
        } else {
            jsonObject.addProperty("trade_no", orderId);
        }
        request.setBizContent(jsonObject.toString());
        AlipayTradeQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (Exception e) {
            log.error("aplipay query order status orderId: {}, error: {}", orderId, ExceptionUtils.getMessage(e));
            throw new ServiceException(ResultCode.INTERNAL_SERVER_ERROR.getMessage());
        }
        if (response.isSuccess()) {
            log.info("aplipay query order status execute success: {}", queryResToString(response));
        } else {
            log.error("aplipay query order status execute service exception: {}", response);

        }
        return response;
    }

    public AlipayTradeCancelResponse cancelOrder(String orderId, boolean isLocal) {
        PayConfig.AliPayConfig aliPayConfig = payConfig.getAliPay();
        AlipayClient alipayClient = new DefaultAlipayClient(aliPayConfig.getGatewayUrl(), aliPayConfig.getAppId(),
                aliPayConfig.getPrivateKey(), "json", "UTF-8",
                aliPayConfig.getAlipayPublicKey(), "RSA2");
        AlipayTradeCancelRequest request = new AlipayTradeCancelRequest();
        JsonObject bizContent = new JsonObject();
        if (isLocal) {
            bizContent.addProperty("out_trade_no", orderId);
        } else {
            bizContent.addProperty("trade_no", orderId);
        }
        request.setBizContent(bizContent.toString());

        AlipayTradeCancelResponse response;
        try {
            response = alipayClient.execute(request);
        } catch (Exception e) {
            log.error("aplipay cancel order status orderId: {}, error: {}", orderId, ExceptionUtils.getMessage(e));
            throw new ServiceException(ResultCode.INTERNAL_SERVER_ERROR.getMessage());
        }
        if (response.isSuccess()) {
            log.info("aplipay cancel order status execute success: {}", response);
        } else {
            log.error("aplipay cancel order status execute service exception: {}", response);

        }
        return response;
    }

    public String queryResToString(AlipayTradeQueryResponse response) {
        return "AlipayTradeQueryResponse(trade_no=" + response.getTradeNo();

    }
}
