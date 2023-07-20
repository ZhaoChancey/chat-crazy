package com.chat.crazy.front.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chat.crazy.base.constant.ApplicationConstant;
import com.chat.crazy.base.domain.entity.UserDO;
import com.chat.crazy.base.domain.query.ChatRequest;
import com.chat.crazy.front.domain.request.chat.ChatProcessRequest;
import com.chat.crazy.front.service.UserService;
import com.chat.crazy.base.enums.VipTypeEnum;
import com.chat.crazy.front.domain.vo.user.UserInfoVo;
import com.chat.crazy.front.mapper.UserMapper;
import com.chat.crazy.base.util.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @Author:
 * @Description:
 * @Date: 2023/4/14 下午5:23
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    @Override
    public UserInfoVo getUserInfo(ChatRequest request) {
        UserInfoVo userInfoRes = new UserInfoVo();
        UserInfoVo.UserIdentity identity = new UserInfoVo.UserIdentity();
        if (request.getUser() == null) {
            // 未登录
            identity.setVipType(VipTypeEnum.NOT_LOGIN.getType());
            identity.setStartTs(0L);
            identity.setEndTs(0L);
            identity.setVipStartTs(0L);
            identity.setVipEndTs(0L);
            identity.setMasterType(0);
//            identity.setIsUsed(true);
            userInfoRes.setIdentity(identity);
            userInfoRes.setNickName(ApplicationConstant.DEFAULT_NICKNAME);
            userInfoRes.setPhone("");
        } else {
            // 登录
            UserDO userDO = request.getUser();
            LocalDateTime startTime = userDO.getStartTime();
            LocalDateTime endTime = userDO.getEndTime();
            LocalDateTime vipStartTime = userDO.getVipStartTime();
            LocalDateTime vipEndTime = userDO.getVipEndTime();
            identity.setVipType(getVipType(vipStartTime, vipEndTime));
            identity.setStartTs(TimeUtils.getMilliSecond(startTime));
            identity.setEndTs(TimeUtils.getMilliSecond(endTime));
            identity.setVipStartTs(identity.getVipType() == VipTypeEnum.VIP.getType() ? TimeUtils.getMilliSecond(vipStartTime) : null);
            identity.setVipEndTs(identity.getVipType() == VipTypeEnum.VIP.getType() ? TimeUtils.getMilliSecond(vipEndTime) : null);
            LocalDateTime now = LocalDateTime.now();
            identity.setFreeLastDays(Math.max((int) Duration.between(now, endTime).toDays(), 0));
            identity.setVipLastDays(identity.getVipType() == VipTypeEnum.VIP.getType() ?
                            Math.max((int) Duration.between(now, vipEndTime).toDays(), 0) : null);
//            identity.setIsUsed(isUserExpire(identity.getVipType(), endTime, vipEndTime));
            identity.setMasterType(userDO.getIsInvitePerm());
            userInfoRes.setIdentity(identity);
            userInfoRes.setId(userDO.getId());
            userInfoRes.setNickName(userDO.getNickName());
            userInfoRes.setPhone(userDO.getPhone());
        }
        return userInfoRes;
    }

    @Override
    public boolean verifySendMessage(ChatProcessRequest request) {
        if (request.getUser() == null) {
            // 未登录用户
            // 不允许多轮对话
            request.setOptions(null);
            return true;
        } else {
            UserDO user = request.getUser();
            return isUserExpire(getVipType(user.getVipStartTime(), user.getVipEndTime()), user.getEndTime(), user.getVipEndTime());
        }
    }

    @Override
    public int updateUserInfo(UserDO userDO) {
        return getBaseMapper().updateById(userDO);
    }

    /**
     * 根据VIP的生效和失效时间判断VIP类型，注意不能用来判断未登录用户！
     * @param vipStartTime
     * @param vipEndTime
     * @return vip类型
     */
    private int getVipType(LocalDateTime vipStartTime, LocalDateTime vipEndTime) {
        if (vipStartTime == null || vipEndTime == null) {
            return VipTypeEnum.NORMAL.getType();
        }
        return vipEndTime.isAfter(LocalDateTime.now()) ? VipTypeEnum.VIP.getType() : VipTypeEnum.NORMAL.getType();
    }

    /**
     * 判断用户是否可以继续使用
     * 1. 未登录用户，可以
     * 2. 登录用户，三天使用时间
     * 3. VIP用户，判断
     * @param vipType
     * @param endTime 用户失效时间
     * @return
     */
    private boolean isUserExpire(int vipType, LocalDateTime endTime, LocalDateTime vipEndTime) {
        if (vipType == VipTypeEnum.NORMAL.getType()) {
            return endTime.isAfter(LocalDateTime.now());
        } else if (vipType == VipTypeEnum.VIP.getType()){
            return vipEndTime.isAfter(LocalDateTime.now());
        }
        return true;
    }


}
