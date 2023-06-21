package com.chat.crazy.front.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chat.crazy.base.client.AliPayClient;
import com.chat.crazy.base.config.PayConfig;
import com.chat.crazy.base.domain.bo.AlipayPreCreateClientRequest;
import com.chat.crazy.base.domain.entity.OrderDO;
import com.chat.crazy.base.enums.PackageEnum;
import com.chat.crazy.base.enums.PaymentTypeEnum;
import com.chat.crazy.base.enums.TradeStatusEnum;
import com.chat.crazy.base.exception.ServiceException;
import com.chat.crazy.base.service.DistributeService;
import com.chat.crazy.front.domain.request.pay.PayPreCreateRequest;
import com.chat.crazy.front.domain.vo.pay.PayOrderStatusVO;
import com.chat.crazy.front.domain.vo.pay.PayPackageVO;
import com.chat.crazy.front.domain.vo.pay.PayPreCreateVO;
import com.chat.crazy.front.mapper.PayMapper;
import com.chat.crazy.front.service.PayService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

import static com.chat.crazy.base.enums.PackageEnum.*;

/**
 * @Author:
 * @Description:
 * @Date: 2023/6/9 下午3:18
 */
@Slf4j
@Service
public class PayServiceImpl extends ServiceImpl<PayMapper, OrderDO> implements PayService {
    @Resource
    private PayConfig payConfig;

    @Resource
    private AliPayClient aliPayClient;

    @Resource
    DistributeService distributeService;

    private static final List<PayPackageVO.PayPackageItem> PACKAGE_INFO = Arrays.asList(
            PayPackageVO.PayPackageItem.builder().id(MONTH_VIP.getId()).subject(MONTH_VIP.getSubject()).price(MONTH_VIP.getPrice()).build(),
            PayPackageVO.PayPackageItem.builder().id(SEASON_VIP.getId()).subject(SEASON_VIP.getSubject()).price(SEASON_VIP.getPrice()).build(),
            PayPackageVO.PayPackageItem.builder().id(YEAR_VIP.getId()).subject(YEAR_VIP.getSubject()).price(YEAR_VIP.getPrice()).build()
    );
    @Override
    public PayPackageVO getPackageInfo() {
        PayPackageVO payPackageVO = new PayPackageVO();
        payPackageVO.setExtendStr("");
        payPackageVO.setPackages(PACKAGE_INFO);
        return payPackageVO;
    }

    @Override
    public PayPreCreateVO orderPreCreate(PayPreCreateRequest request) {
        PackageEnum packageEnum = typeOf(request.getPackageId());
        if (packageEnum == null) {
            throw new ServiceException("套餐id错误");
        }
        AlipayPreCreateClientRequest clientRequest = new AlipayPreCreateClientRequest();
        // 生成订单id
        String orderId = distributeService.genDistributeId(PaymentTypeEnum.ALI.getType());
        clientRequest.setOutTradeNo(orderId);
        clientRequest.setSubject(packageEnum.getSubject());
        clientRequest.setTotalAmount(packageEnum.getPrice());
        clientRequest.setBody(getBody(packageEnum));
        clientRequest.setTimeoutExpress("15m");
        AlipayTradePrecreateResponse response = aliPayClient.tradePreCreate(clientRequest);
        // 保存到数据库
        OrderDO orderDO = new OrderDO();
        orderDO.setUserId(request.getUserId());
        orderDO.setUserType(request.getUserType());
        orderDO.setOrderId(orderId);
        orderDO.setTransactionId("");
        orderDO.setAmount(packageEnum.getPrice());
        orderDO.setPaymentType(Integer.valueOf(PaymentTypeEnum.ALI.getType()));
        orderDO.setPackageId(packageEnum.getId());
        orderDO.setAmount(packageEnum.getPrice());
        orderDO.setOrderStatus(TradeStatusEnum.WAIT_BUYER_PAY.getStatus());
        int cnt = getBaseMapper().insert(orderDO);
        if (cnt <= 0) {
            throw new ServiceException("订单存储失败");
        }
        PayPreCreateVO payPreCreateVO = new PayPreCreateVO();
        payPreCreateVO.setOrderId(orderId);
        payPreCreateVO.setQrCode(response.getQrCode());
        return payPreCreateVO;
    }

    @Override
    public AlipayTradeQueryResponse getOrderStatus(String orderId) {
        return aliPayClient.getOrderStatus(orderId);
    }

    @Override
    public String cancelOrder(String orderId) {
        return null;
    }

    @Override
    public String notifyAsync(HttpServletRequest request) throws AlipayApiException {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。 
            params.put(name, valueStr);
        }
        log.info("receive request notify: {}", params);
        //调用SDK验证签名
        //公钥验签示例代码
        boolean signVerified = AlipaySignature.rsaCheckV1(params, payConfig.getAliPay().getAlipayPublicKey(), "UTF-8", "RSA2") ;
        //公钥证书验签示例代码

        if (signVerified){
            //按照支付结果异步通知中的描述，对支付结果中的业务内容进行1\2\3\4二次校验，校验成功后在response中返回success
            String orderId = params.getOrDefault("out_trade_no", "");
            OrderDO orderDO = getOrderByOrderId(orderId);
            if (orderDO == null) {
                log.error("订单ID不存在：{}", orderId);
                return "fail";
            }

            String totalAmount = params.getOrDefault("total_amount", "");
            if (StringUtils.isEmpty(totalAmount)) {
                log.error("付款金额不能为空：{}", totalAmount);
                return "fail";
            }
            BigDecimal amount = new BigDecimal(totalAmount);
            if (!amount.equals(orderDO.getAmount())) {
                log.error("付款金额无法对应：{}", totalAmount);
                return "fail";
            }
            String appId = params.getOrDefault("app_id", "");
            if (!payConfig.getAliPay().getAppId().equals(appId)) {
                log.error("appId异常：{}", appId);
                return "fail";
            }
            // 校验成功
        } else {
            log.error("验签失败");
            return "fail";
        }    
        return "success";
    }

    private OrderDO getOrderByOrderId(String orderId) {
        return getBaseMapper().selectOne(new LambdaQueryWrapper<OrderDO>()
                .eq(OrderDO::getOrderId, orderId));
    }

    public static void main(String[] args) {
        BigDecimal bigDecimal = new BigDecimal("");
        System.out.println(bigDecimal);
    }
}
