package com.chat.crazy.service.impl;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chat.crazy.SmsClient;
import com.chat.crazy.constant.ActiveStatus;
import com.chat.crazy.constant.ApplicationConstant;
import com.chat.crazy.constant.UserStatus;
import com.chat.crazy.domain.entity.UserActiveDO;
import com.chat.crazy.domain.entity.UserDO;
import com.chat.crazy.domain.request.user.*;
import com.chat.crazy.exception.AuthException;
import com.chat.crazy.exception.ServiceException;
import com.chat.crazy.handler.response.ResultCode;
import com.chat.crazy.service.AuthService;
import com.chat.crazy.service.UserActiveService;
import com.chat.crazy.service.UserService;
import com.chat.crazy.service.VirtualService;
import com.chat.crazy.util.CommonUtils;
import com.chat.crazy.util.WebTokenUtil;
import com.chat.crazy.util.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author hncboy
 * @date 2023/3/22 20:05
 * 鉴权相关业务实现类
 */
@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Resource
    RedisService redisService;

    @Resource
    VirtualService virtualService;

    @Resource
    UserActiveService userActiveService;

    @Resource
    UserService userService;

    @Resource
    SmsClient smsClient;

    @Override
    public boolean verifyWebToken(String token, HttpServletResponse response) {
        DecodedJWT jwtToken = WebTokenUtil.decode(token);
        if (jwtToken == null) {
            return false;
        }
        //从JWT里取出存放在payload段里的userid，查询这个用户信息得到用户最后登录时间
        Long userId = Long.valueOf(jwtToken.getSubject());
        try {
            //校验
            WebTokenUtil.verify(token);
        } catch (SignatureVerificationException e) {
            log.error(e.getMessage());
            return false;
        } catch (TokenExpiredException e) {
            // 允许一段时间有效时间同时返回新的token
            String newToken = WebTokenUtil.getRefreshToken(jwtToken);
            if (StringUtils.isEmpty(newToken)) {
                log.error(e.getMessage());
                return false;
            }
            log.debug("Subject : [" + userId + "] token expired, allow get refresh token [" + newToken + "]");
            response.setHeader("token", newToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public UserLoginRes login(UserLoginReq req) {
        String code = redisService.getPhoneCode(req.getPhone());
        if (StringUtils.isNotEmpty(code) && req.getCode().equals(code)) {
            LocalDateTime now = LocalDateTime.now().withNano(0);
            // 登录成功
            UserDO user = userService.getOne(new LambdaQueryWrapper<UserDO>().eq(UserDO::getPhone, req.getPhone()));
            if (user == null) {
                // 保存用户
                UserDO userDO = new UserDO();
                userDO.setPhone(req.getPhone());
                userDO.setStatus(UserStatus.VALID.getStatus());
                userDO.setNickName(ApplicationConstant.DEFAULT_NICKNAME);
                userDO.setStartTime(now);
                userDO.setEndTime(now.plusDays(3));
                userService.save(userDO);
                user = userDO;
            }
            // 记录登录时间
            UserActiveDO userActiveDO = new UserActiveDO();
            userActiveDO.setUserId(user.getId());
            userActiveDO.setActiveType(ActiveStatus.LOGIN.getStatus());
            userActiveDO.setActiveTime(now);
            userActiveDO.setIp(WebUtil.getIp());
            userActiveService.save(userActiveDO);
            // 生成token 
            Instant instant = now.toInstant(ZoneOffset.of("+8"));
            String token = WebTokenUtil.create(user.getId().toString(), instant);
            UserLoginRes res = new UserLoginRes();
            res.setToken(token);
            // 删除验证码，使之失效
            redisService.deletePhoneCode(req.getPhone());
            return res;
        } else {
            throw new AuthException("验证码校验失败，请重新输入");
        }
    }

    public String genVirId(VirtualIdReq req) {
        return virtualService.genVirId(req);
    }

    @Override
    public String sendMes(SendMsgReq req) {
        String phone = req.getPhone();
        // 1. 发送验证码次数限流
        int hourCnt = redisService.getHourSendRecord(phone);
        if (hourCnt >= 2) {
            throw new ServiceException("验证码发送次数已达上限，请1小时后再尝试");
        }
        
        int dayCnt = redisService.getDaySendRecord(phone);
        if (dayCnt >= 4) {
            throw new ServiceException("验证码发送次数已达上限，请24小时后再尝试");
        }
        
        // 2. 如果redis的验证码未失效，可以继续使用
        String phoneCode = redisService.getPhoneCode(phone);
        String code = StringUtils.isNotEmpty(phoneCode) ? phoneCode : CommonUtils.genCode();
        ResultCode statusEnum = smsClient.sendMsg(phone, code);
        if (ResultCode.SUCCESS == statusEnum) {
            redisService.setPhoneCode(phone, code);
            return "发送成功";
        } else {
            throw new ServiceException("验证码发送失败，请稍后重试");
        }
    }

    @Override
    public UserInfoRes getUserInfo() {
        String token = getCurrentUserToken();
        return userService.getUserInfo(token);
    }


    @Override
    public String logout() {
        String token = getCurrentUserToken();
        DecodedJWT jwt = WebTokenUtil.decode(token);
        if (jwt == null) {
            throw new AuthException("token校验失败");
        }
        Long userId = Long.parseLong(jwt.getSubject());
        UserDO userDO = userService.getById(userId);
        if (userDO == null) {
            throw new ServiceException("该用户不存在");
        }
        UserActiveDO userActiveDO = new UserActiveDO();
        userActiveDO.setUserId(userDO.getId());
        userActiveDO.setIp(WebUtil.getIp());
        userActiveDO.setActiveType(ActiveStatus.EXIT.getStatus());
        userActiveDO.setActiveTime(LocalDateTime.now());
        return "退出登录成功";
    }
    
    private String getCurrentUserToken() {
        return WebUtil.getRequest().getHeader("token");
    }
}
