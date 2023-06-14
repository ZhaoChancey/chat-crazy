package com.chat.crazy.front.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chat.crazy.base.client.AliPayClient;
import com.chat.crazy.base.domain.entity.OrderDO;
import com.chat.crazy.front.domain.vo.pay.PayOrderStatusVO;
import com.chat.crazy.front.domain.vo.pay.PayPackageVO;
import com.chat.crazy.front.domain.vo.pay.PayPreCreateVO;
import com.chat.crazy.front.mapper.PayMapper;
import com.chat.crazy.front.service.PayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

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
}
