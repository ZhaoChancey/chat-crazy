package com.chat.crazy.front.service;

import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chat.crazy.base.domain.entity.OrderDO;
import com.chat.crazy.front.domain.vo.pay.PayOrderStatusVO;
import com.chat.crazy.front.domain.vo.pay.PayPackageVO;
import com.chat.crazy.front.domain.vo.pay.PayPreCreateVO;

import javax.servlet.http.HttpServletRequest;

public interface PayService extends IService<OrderDO> {
    /**
     * 获取套餐信息
     * @return 套餐
     */
    PayPackageVO getPackageInfo();

    /**
     * 预创建订单，获取二维码链接
     * @param packageId 套餐类型
     * @return
     */
    PayPreCreateVO orderPreCreate(Integer packageId);

    /**
     * 获取支付状态
     * @param orderId 订单id
     * @return
     */
    PayOrderStatusVO getOrderStatus(String orderId);

    /**
     * 主动关闭订单
     * @param orderId 订单id
     * @return
     */
    String cancelOrder(String orderId);
    
    String notifyAsync(HttpServletRequest request) throws AlipayApiException;
}