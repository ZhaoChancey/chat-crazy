package com.chat.crazy.service.impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chat.crazy.constant.ApplicationConstant;
import com.chat.crazy.constant.VipType;
import com.chat.crazy.domain.entity.UserActiveDO;
import com.chat.crazy.domain.entity.UserDO;
import com.chat.crazy.domain.request.user.UserInfoRes;
import com.chat.crazy.exception.AuthException;
import com.chat.crazy.mapper.UserActiveMapper;
import com.chat.crazy.mapper.UserMapper;
import com.chat.crazy.service.UserActiveService;
import com.chat.crazy.service.UserService;
import com.chat.crazy.util.TimeUtils;
import com.chat.crazy.util.WebTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @Author:
 * @Description:
 * @Date: 2023/4/14 下午5:23
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    @Override
    public UserInfoRes getUserInfo(String token) {
        UserInfoRes userInfoRes = new UserInfoRes();
        UserInfoRes.UserIdentity identity = new UserInfoRes.UserIdentity();
        if (StringUtils.isEmpty(token)) {
            // 未登录
            identity.setVipType(VipType.NOT_LOGIN.getType());
            identity.setStartTs(0L);
            identity.setEndTs(0L);
            identity.setIsUsed(true);
            userInfoRes.setIdentity(identity);
            userInfoRes.setNickName(ApplicationConstant.DEFAULT_NICKNAME);
            userInfoRes.setPhone("");
        } else {
            // 登录
            DecodedJWT jwt = WebTokenUtil.decode(token);
            if (jwt == null) {
                throw new AuthException("token校验失败");
            }
            Long userId = Long.parseLong(jwt.getSubject());
            UserDO userDO = getById(userId);
            LocalDateTime startTime = userDO.getStartTime();
            LocalDateTime endTime = userDO.getEndTime();
            LocalDateTime vipStartTime = userDO.getVipStartTime();
            LocalDateTime vipEndTime = userDO.getVipEndTime();
            identity.setVipType(getVipType(vipStartTime, vipEndTime));
            identity.setStartTs(identity.getVipType() == VipType.NORMAL.getType() ? 
                    TimeUtils.getMilliSecond(startTime) : TimeUtils.getMilliSecond(vipStartTime));
            identity.setEndTs(identity.getVipType() == VipType.NORMAL.getType() ?
                    TimeUtils.getMilliSecond(endTime) : TimeUtils.getMilliSecond(vipEndTime));
            identity.setIsUsed(isUserExpire(identity.getVipType(), endTime, vipEndTime));
            userInfoRes.setIdentity(identity);
            userInfoRes.setId(userId);
            userInfoRes.setNickName(userDO.getNickName());
            userInfoRes.setPhone(userDO.getPhone());
        }
        return userInfoRes;
    }

    /**
     * 根据VIP的生效和失效时间判断VIP类型，注意不能用来判断未登录用户！
     * @param vipStartTime
     * @param vipEndTime
     * @return vip类型
     */
    private int getVipType(LocalDateTime vipStartTime, LocalDateTime vipEndTime) {
        if (vipStartTime == null || vipEndTime == null) {
            return VipType.NORMAL.getType();
        }
        return vipEndTime.isAfter(LocalDateTime.now()) ? VipType.VIP.getType() : VipType.NORMAL.getType();
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
        if (vipType == VipType.NORMAL.getType()) {
            return endTime.isAfter(LocalDateTime.now());
        } else if (vipType == VipType.VIP.getType()){
            return vipEndTime.isAfter(LocalDateTime.now());
        }
        return true;
    }
}
