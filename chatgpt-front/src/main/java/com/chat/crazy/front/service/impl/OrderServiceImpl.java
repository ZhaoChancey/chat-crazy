package com.chat.crazy.front.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chat.crazy.base.constant.RedisConstant;
import com.chat.crazy.base.domain.entity.OrderDO;
import com.chat.crazy.base.domain.entity.UserDO;
import com.chat.crazy.base.enums.PackageEnum;
import com.chat.crazy.base.enums.TradeStatusEnum;
import com.chat.crazy.base.exception.ServiceException;
import com.chat.crazy.base.handler.response.ResultCode;
import com.chat.crazy.base.service.impl.RedisService;
import com.chat.crazy.base.util.TimeUtils;
import com.chat.crazy.front.mapper.OrderMapper;
import com.chat.crazy.front.service.OrderService;
import com.chat.crazy.front.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static com.chat.crazy.base.enums.UserTypeEnum.COMMON;

@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderDO> implements OrderService {

    @Resource
    UserService userService;

    @Resource
    RedisService redisService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultCode successOrderAndUser(UserDO userDO, OrderDO orderDO) {
//        log.info("订单：{}", orderDO);
        ResultCode code = ResultCode.SUCCESS;
        try {
            int cnt = getBaseMapper().update(orderDO, new LambdaUpdateWrapper<OrderDO>().eq(OrderDO::getId, orderDO.getId()).eq(OrderDO::getOrderStatus, TradeStatusEnum.WAIT_BUYER_PAY.getStatus()));
//        int cnt = getBaseMapper().updateById(orderDO);
            if (cnt <= 0) {
                log.error("订单更新失败：{}", orderDO);
                throw new ServiceException("订单更新失败");
            }

            redisService.setValue(RedisConstant.USER_VIP_INFO_KEY_SUFFIX + COMMON.getType() + ":" + userDO.getId(),
                    (userDO.getVipStartTime() == null ? "null" : TimeUtils.dateTimeToStr(userDO.getVipStartTime()))
                            + "#" + (userDO.getVipEndTime() == null ? "null" : TimeUtils.dateTimeToStr(userDO.getVipEndTime())),
                    RedisConstant.USER_VIP_INFO_VALID_TIME, TimeUnit.SECONDS);
            // 更新用户VIP时间
            PackageEnum packageEnum = PackageEnum.typeOf(orderDO.getPackageId());
            UserDO updateDo = new UserDO();
            updateDo.setId(userDO.getId());
            LocalDateTime vipStartTime = LocalDateTime.now();
            LocalDateTime vipEndTime = vipStartTime.plusDays(packageEnum.getDays());
            updateDo.setVipStartTime(vipStartTime);
            updateDo.setVipEndTime(vipEndTime);
            updateDo.setPackageType(packageEnum.getPackageType().getId());
            int userCnt = userService.updateUserInfo(updateDo);
            if (userCnt <= 0) {
                log.error("用户状态更新失败：{}", orderDO);
                throw new ServiceException("用户状态更新失败");
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error("订单成功状态更新失败，{}", ExceptionUtils.getStackTrace(e));
            code = ResultCode.INTERNAL_SERVER_ERROR;
        }
        return code;
    }
}
