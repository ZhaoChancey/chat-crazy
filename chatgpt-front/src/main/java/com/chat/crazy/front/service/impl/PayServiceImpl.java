package com.chat.crazy.front.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.response.AlipayTradeCancelResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chat.crazy.base.client.AliPayClient;
import com.chat.crazy.base.config.MdcFilter;
import com.chat.crazy.base.config.PayConfig;
import com.chat.crazy.base.domain.bo.AlipayPreCreateClientRequest;
import com.chat.crazy.base.domain.entity.OrderDO;
import com.chat.crazy.base.enums.PackageEnum;
import com.chat.crazy.base.enums.PaymentTypeEnum;
import com.chat.crazy.base.enums.TradeStatusEnum;
import com.chat.crazy.base.exception.ServiceException;
import com.chat.crazy.base.handler.response.ResultCode;
import com.chat.crazy.base.service.DistributeService;
import com.chat.crazy.base.service.impl.RedisService;
import com.chat.crazy.base.util.TimeUtils;
import com.chat.crazy.front.domain.request.pay.PayOrderRequest;
import com.chat.crazy.front.domain.request.pay.PayPreCreateRequest;
import com.chat.crazy.front.domain.vo.pay.PayOrderStatusVO;
import com.chat.crazy.front.domain.vo.pay.PayPackageVO;
import com.chat.crazy.front.domain.vo.pay.PayPreCreateVO;
import com.chat.crazy.front.mapper.PayMapper;
import com.chat.crazy.front.service.OrderService;
import com.chat.crazy.front.service.PayService;
import com.google.gson.Gson;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.chat.crazy.base.constant.RedisConstant.*;
import static com.chat.crazy.base.constant.RedisConstant.MAX_REQUEST_TIME_HOUR;
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

    @Resource
    OrderService orderService;

    private static final int SUCCESS = 0;
    private static final int WAIT = 1;
    private static final int CLOSED = 2;

    private static final Gson gson = new Gson();

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
        PayPreCreateVO payPreCreateVO = new PayPreCreateVO();
        // redis取订单
        RedisService redisService = SpringUtil.getBean(RedisService.class);
        Boolean isSuc = redisService.setNx(DISTRIBUTE_LOCK_KEY_SUFFIX + "order:" + request.getUserType() + ":" + request.getUserId(), MdcFilter.getCurrTraceId(), 10, TimeUnit.SECONDS);
        if (!isSuc) {
            // 加锁失败
            throw new ServiceException(ResultCode.ALI_TRADE_ORDER_REPEAT_ERROR);
        }

        try {
            String orderCache = redisService.getValue(ORDER_KEY_SUFFIX + request.getUserType() + ":" + request.getUserId());
            if (StringUtils.isNotEmpty(orderCache)) {
                return gson.fromJson(orderCache, PayPreCreateVO.class);
            }
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
            payPreCreateVO.setOrderId(orderId);
            payPreCreateVO.setQrCode(response.getQrCode());

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
                log.error("订单保存失败：{}", orderDO);
                throw new ServiceException("订单创建失败");
            }
        } catch (Exception e) {
            log.error("订单创建接口异常，req: {}, error: {}", request, ExceptionUtils.getMessage(e));
        } finally {
            if (MdcFilter.getCurrTraceId().equals(redisService.getValue(DISTRIBUTE_LOCK_KEY_SUFFIX + "order:" + request.getUserType() + ":" + request.getUserId()))) {
                redisService.deleteKey(DISTRIBUTE_LOCK_KEY_SUFFIX + "order:" + request.getUserType() + ":" + request.getUserId());
            }
        }

        return payPreCreateVO;
    }

    /**
     * 与异步协调逻辑
     * @param orderId 订单id
     * @return
     */
//    @Override
//    public PayOrderStatusVO getOrderStatus(String orderId) {
//        PayOrderStatusVO vo = new PayOrderStatusVO();
//        OrderDO orderDO = getOrderByOrderId(orderId);
//        if (orderDO == null) {
//            log.error("订单不存在，{}", orderId);
//            throw new ServiceException("订单不存在");
//        }
//        if (orderDO.getOrderStatus() == TradeStatusEnum.TRADE_SUCCESS.getStatus() ||
//                orderDO.getOrderStatus() == TradeStatusEnum.TRADE_FINISHED.getStatus()) {
//            vo.setTradeStatus(SUCCESS);
//        } else if (orderDO.getOrderStatus() == TradeStatusEnum.TRADE_CLOSED.getStatus()) {
//            vo.setTradeStatus(CLOSED);
//        } else if (orderDO.getOrderStatus() == TradeStatusEnum.WAIT_BUYER_PAY.getStatus()) {
//            // 查询订单，2分钟后还没收到异步通知就主动查询支付宝
////            Duration durationon = Duration.between(orderDO.getCreateTime(), LocalDateTime.now());
////            if (duration.throwoMinutes() > 2) {
//            AlipayTradeQueryResponse response = aliPayClient.getOrderStatus(orderId, false);
//            int cnt = 2;
//            while ("20000".equals(response.getCode()) && "isp.unknow-error".equals(response.getSubCode()) && cnt > 0) {
//                cnt--;
//                log.error("订单状态主动查询重试, cnt: {}", cnt);
//                response = aliPayClient.getOrderStatus(orderId, false);
//            }
//
//            if ("ACQ.TRADE_NOT_EXIST".equals(response.getSubCode())) {
//                log.error("用户还未扫码");
//                vo.setTradeStatus(WAIT);
//                return vo;
//            }
//            if (response.isSuccess()) {
//                if (TradeStatusEnum.WAIT_BUYER_PAY.getAliStatus().equals(response.getTradeStatus())) {
//                    vo.setTradeStatus(WAIT);
//                } else if (TradeStatusEnum.TRADE_SUCCESS.getAliStatus().equals(response.getTradeStatus()) ||
//                                TradeStatusEnum.TRADE_FINISHED.getAliStatus().equals(response.getTradeStatus())) {
//                    // 校验金额之类
//                    OrderDO updateOrder = new OrderDO();
//                    updateOrder.setId(orderDO.getId());
//                    updateOrder.setOrderStatus(Integer.valueOf(response.getTradeStatus()));
//                    updateOrder.setTransactionId(response.getTradeNo());
//                    String gmtCreateTime = params.getOrDefault("gmt_create", "");
//                    String gmtPaymentTime = params.getOrDefault("gmt_payment", "");
//                    if (StringUtils.isNotEmpty(gmtCreateTime)) {
//                        updateOrder.setGmtCreateTime(TimeUtils.strToDateTime(gmtCreateTime));
//                    }
//                    if (StringUtils.isNotEmpty(gmtPaymentTime)) {
//                        updateOrder.setGmtPaymentTime(TimeUtils.strToDateTime(gmtPaymentTime));
//                    }
//                    int cnt = updateOrder(updateOrder);
//                    if (cnt > 0) {
//                        log.info("订单更新成功，{}", updateOrder);
//                    }
//                    vo.setTradeStatus(SUCCESS);
//                } else if (TradeStatusEnum.TRADE_CLOSED.getAliStatus().equals(response.getTradeStatus())) {
//                    vo.setTradeStatus(CLOSED);
//                }
//            } else {
//                throw new ServiceException("订单查询异常");
//            }
////            } else {
////                vo.setTradeStatus(WAIT);
////            }
//        } else {
//            throw new ServiceException("订单状态获取失败，请刷新重试");
//        }
//
//        return vo;
//
//    }

    /**
     * 纯同步逻辑
     * @param request
     * @return
     */
    @Override
    public PayOrderStatusVO getOrderStatus(PayOrderRequest request) {
        String orderId = request.getOrderId();
        if (StringUtils.isEmpty(orderId)) {
            throw new ServiceException("订单ID为空");
        }
        PayOrderStatusVO vo = new PayOrderStatusVO();
        OrderDO orderDO = getOrderByOrderId(request.getOrderId());
        if (orderDO == null) {
            log.error("订单不存在，{}", request.getOrderId());
            throw new ServiceException("订单不存在");
        }
        if (orderDO.getOrderStatus() == TradeStatusEnum.TRADE_SUCCESS.getStatus() ||
                orderDO.getOrderStatus() == TradeStatusEnum.TRADE_FINISHED.getStatus()) {
            vo.setTradeStatus(SUCCESS);
        } else if (orderDO.getOrderStatus() == TradeStatusEnum.TRADE_CLOSED.getStatus()) {
            vo.setTradeStatus(CLOSED);
        } else if (orderDO.getOrderStatus() == TradeStatusEnum.WAIT_BUYER_PAY.getStatus()) {
            // 查询订单，2分钟后还没收到异步通知就主动查询支付宝
//            Duration durationon = Duration.between(orderDO.getCreateTime(), LocalDateTime.now());
//            if (duration.throwoMinutes() > 2) {
            AlipayTradeQueryResponse response = aliPayClient.getOrderStatus(orderId, true);
            int cnt = 2;
            while ("20000".equals(response.getCode()) && "isp.unknow-error".equals(response.getSubCode()) && cnt > 0) {
                cnt--;
                log.error("订单状态主动查询重试, cnt: {}", cnt);
                response = aliPayClient.getOrderStatus(orderId, true);
            }

            if ("20000".equals(response.getCode()) && "isp.unknow-error".equals(response.getSubCode())) {
                log.error("多次重试后请求支付宝异常，{}", response);
            }

            if ("ACQ.TRADE_NOT_EXIST".equals(response.getSubCode())) {
                log.error("用户还未扫码");
                vo.setTradeStatus(WAIT);
                vo.setIsTrading(false);
                return vo;
            }
            if (response.isSuccess()) {
                if (TradeStatusEnum.WAIT_BUYER_PAY.getAliStatus().equals(response.getTradeStatus())) {
                    vo.setTradeStatus(WAIT);
                } else if (TradeStatusEnum.TRADE_SUCCESS.getAliStatus().equals(response.getTradeStatus()) ||
                        TradeStatusEnum.TRADE_FINISHED.getAliStatus().equals(response.getTradeStatus())) {
                    // 校验金额之类
                    RedisService redisService = SpringUtil.getBean(RedisService.class);
                    Boolean isSuc = redisService.setNx(DISTRIBUTE_LOCK_KEY_SUFFIX + "order:update:" + request.getUserType() + ":" + request.getUserId(), MdcFilter.getCurrTraceId(), 3, TimeUnit.SECONDS);
                    if (!isSuc) {
                        // 加锁失败
                        throw new ServiceException(ResultCode.ALI_TRADE_ORDER_REPEAT_ERROR);
                    }
                    try {
                        OrderDO updateOrder = new OrderDO();
                        updateOrder.setId(orderDO.getId());
                        updateOrder.setOrderStatus(TradeStatusEnum.statusOf(response.getTradeStatus()).getStatus()
                        );
                        updateOrder.setTransactionId(response.getTradeNo());
                        updateOrder.setPackageId(orderDO.getPackageId());
                        if (response.getSendPayDate() != null) {
                            updateOrder.setGmtPaymentTime(response.getSendPayDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                        }
                        // 设置用户VIP时间
                        orderService.successOrderAndUser(request.getUser(), updateOrder);
                        vo.setTradeStatus(SUCCESS);
                    } catch (Exception e) {
                        vo.setTradeStatus(WAIT);
                        log.error("订单状态查询接口异常，req: {}, error: {}", request, ExceptionUtils.getMessage(e));
                    } finally {
                        redisService.deleteKey(DISTRIBUTE_LOCK_KEY_SUFFIX + "order:update:" + request.getUserType() + ":" + request.getUserId());
                    }
                } else if (TradeStatusEnum.TRADE_CLOSED.getAliStatus().equals(response.getTradeStatus())) {
                    vo.setTradeStatus(CLOSED);
                }
            } else {
                throw new ServiceException("订单查询异常");
            }
        } else {
            throw new ServiceException("订单状态获取失败，请刷新重试");
        }

        return vo;
    }

    @Override
    public String cancelOrder(String orderId) {
        // 查询订单状态，如果未支付状态则调撤销接口
        // 如果撤销成功，打印返回情况再定论
        OrderDO orderDO = getOrderByOrderId(orderId);
        if (orderDO == null || orderDO.getOrderStatus() == TradeStatusEnum.TRADE_SUCCESS.getStatus() ||
                orderDO.getOrderStatus() == TradeStatusEnum.TRADE_FINISHED.getStatus()) {
            return "订单已支付成功，无需撤销";
        }

        if (orderDO.getOrderStatus() == TradeStatusEnum.TRADE_CLOSED.getStatus()) {
            return "订单已关闭，无需撤销";
        }

        // 调用查询接口
        PayOrderRequest payOrderRequest = new PayOrderRequest();
        payOrderRequest.setOrderId(orderDO.getOrderId());
        PayOrderStatusVO orderStatus = getOrderStatus(payOrderRequest);
        if (orderStatus.getTradeStatus() == WAIT) {
            AlipayTradeCancelResponse response = aliPayClient.cancelOrder(orderId, false);
            if (response.isSuccess()) {
                // 撤销成功，保存订单状态
                OrderDO updateDo = new OrderDO();
                updateDo.setId(orderDO.getId());
                updateDo.setOrderStatus(TradeStatusEnum.TRADE_CLOSED.getStatus());
                String action = response.getAction();
                if ("close".equals(action)) {
                    return gson.toJson(response);
                } else if ("refund".equals(action)) {
                    return gson.toJson(response);
                }
            }
        }
        return "订单无需撤销";
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
            // 修改数据库订单状态
            OrderDO updateOrder = new OrderDO();
            TradeStatusEnum tradeStatus = TradeStatusEnum.statusOf(params.getOrDefault("trade_status", ""));
            if (tradeStatus == TradeStatusEnum.ERROR) {
                log.error("订单状态异常: {}", params.get("trade_status"));
                return "fail";
            }
            updateOrder.setId(orderDO.getId());
            updateOrder.setOrderStatus(tradeStatus.getStatus());
            updateOrder.setNotifyId(params.getOrDefault("notify_id", ""));
            updateOrder.setTransactionId(params.getOrDefault("trade_no", ""));
            String gmtCreateTime = params.getOrDefault("gmt_create", "");
            String gmtPaymentTime = params.getOrDefault("gmt_payment", "");
            if (StringUtils.isNotEmpty(gmtCreateTime)) {
                updateOrder.setGmtCreateTime(TimeUtils.strToDateTime(gmtCreateTime));
            }
            if (StringUtils.isNotEmpty(gmtPaymentTime)) {
                updateOrder.setGmtPaymentTime(TimeUtils.strToDateTime(gmtPaymentTime));
            }
            OrderDO repeatOrder = getOrderByOrderId(orderId);
            if (repeatOrder.getOrderStatus() == TradeStatusEnum.TRADE_SUCCESS.getStatus() ||
                    repeatOrder.getOrderStatus() == TradeStatusEnum.TRADE_FINISHED.getStatus()) {
                log.info("订单已经被更新，无需重置数据库");
                return "success";
            }
            int cnt = updateOrder(updateOrder);
            if (cnt > 0) {
                log.info("订单更新成功，{}", updateOrder);
            }
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

    private int updateOrder(OrderDO orderDO) {
        return getBaseMapper().updateById(orderDO);
    }

    public static void main(String[] args) {
        BigDecimal bigDecimal = new BigDecimal("");
        System.out.println(bigDecimal);
    }
}
