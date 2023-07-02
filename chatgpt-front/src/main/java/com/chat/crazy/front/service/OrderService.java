package com.chat.crazy.front.service;

import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chat.crazy.base.domain.entity.OrderDO;
import com.chat.crazy.base.domain.entity.UserDO;
import com.chat.crazy.base.handler.response.ResultCode;
import com.chat.crazy.front.domain.request.pay.PayOrderRequest;
import com.chat.crazy.front.domain.request.pay.PayPreCreateRequest;
import com.chat.crazy.front.domain.vo.pay.PayOrderStatusVO;
import com.chat.crazy.front.domain.vo.pay.PayPackageVO;
import com.chat.crazy.front.domain.vo.pay.PayPreCreateVO;

import javax.servlet.http.HttpServletRequest;

public interface OrderService extends IService<OrderDO> {

    ResultCode successOrderAndUser(UserDO userDO, OrderDO orderDO);
}