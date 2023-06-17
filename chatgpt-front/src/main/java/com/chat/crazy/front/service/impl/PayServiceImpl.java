package com.chat.crazy.front.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chat.crazy.base.client.AliPayClient;
import com.chat.crazy.base.config.PayConfig;
import com.chat.crazy.base.domain.entity.DistributeDo;
import com.chat.crazy.base.domain.entity.OrderDO;
import com.chat.crazy.front.domain.vo.pay.PayOrderStatusVO;
import com.chat.crazy.front.domain.vo.pay.PayPackageVO;
import com.chat.crazy.front.domain.vo.pay.PayPreCreateVO;
import com.chat.crazy.front.mapper.PayMapper;
import com.chat.crazy.front.service.PayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static com.alipay.api.AlipayConstants.CHARSET;
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
    public PayPreCreateVO orderPreCreate(Integer packageId) {
        return null;
    }

    @Override
    public PayOrderStatusVO getOrderStatus(String orderId) {
        return null;
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
                return "error";
            }

            String totalAmount = params.getOrDefault("total_amount", "");

            String appId = params.getOrDefault("app_id", "");

        } else {
            // TODO 验签失败则记录异常日志，并在response中返回fail. 
        }    
        return "success";
    }

    private OrderDO getOrderByOrderId(String orderId) {
        return getBaseMapper().selectOne(new LambdaQueryWrapper<OrderDO>()
                .eq(OrderDO::getOrderId, orderId));
    }
}
