package com.chat.crazy.front.service.impl;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chat.crazy.base.domain.query.ChatRequest;
import com.chat.crazy.base.service.impl.RedisService;
import com.chat.crazy.front.service.UserActiveService;
import com.chat.crazy.front.service.UserService;
import com.chat.crazy.front.service.VirtualService;
import com.chat.crazy.base.client.SmsClient;
import com.chat.crazy.base.constant.ApplicationConstant;
import com.chat.crazy.base.domain.entity.UserActiveDO;
import com.chat.crazy.base.domain.entity.UserDO;
import com.chat.crazy.base.exception.AuthException;
import com.chat.crazy.base.exception.ServiceException;
import com.chat.crazy.base.handler.response.ResultCode;
import com.chat.crazy.base.util.CommonUtils;
import com.chat.crazy.base.util.TokenUtil;
import com.chat.crazy.base.util.WebUtil;
import com.chat.crazy.base.enums.ActiveStatusEnum;
import com.chat.crazy.base.enums.UserStatusEnum;
import com.chat.crazy.front.domain.request.user.SendMsgReq;
import com.chat.crazy.front.domain.request.user.UserLoginReq;
import com.chat.crazy.front.domain.request.user.VirtualIdReq;
import com.chat.crazy.front.domain.vo.user.UserInfoVo;
import com.chat.crazy.front.domain.vo.user.UserLoginVo;
import com.chat.crazy.front.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.chat.crazy.base.constant.RedisConstant.*;

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

    private static final List<String> WHITE_USERS = Arrays.asList("19801169872", "15928706284");

    @Override
    public boolean verifyWebToken(String token, HttpServletResponse response) {
        DecodedJWT jwtToken = TokenUtil.decode(token);
        if (jwtToken == null) {
            return false;
        }
        try {
            //校验
            TokenUtil.verify(token);
        } catch (SignatureVerificationException e) {
            log.error(e.getMessage());
            return false;
        } catch (TokenExpiredException e) {
            // 允许一段时间有效时间同时返回新的token
            String newToken = TokenUtil.getRefreshToken(jwtToken);
            if (StringUtils.isEmpty(newToken)) {
                log.error(e.getMessage());
                return false;
            }
            log.debug("token expired, allow get refresh token [" + newToken + "]");
            response.setHeader("token", newToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public UserLoginVo login(UserLoginReq req) {
        String code = getPhoneCode(req.getPhone());
        if (WHITE_USERS.contains(req.getPhone()) || (StringUtils.isNotEmpty(code) && req.getCode().equals(code))) {
            LocalDateTime now = LocalDateTime.now().withNano(0);
            // 登录成功
            UserDO user = userService.getOne(new LambdaQueryWrapper<UserDO>().eq(UserDO::getPhone, req.getPhone()));
            if (user == null) {
                // 保存用户
                UserDO userDO = new UserDO();
                userDO.setPhone(req.getPhone());
                userDO.setStatus(UserStatusEnum.VALID.getStatus());
                userDO.setNickName(ApplicationConstant.DEFAULT_NICKNAME);
                userDO.setVipStartTime(now);
                userDO.setVipEndTime(LocalDateTime.parse("2023-08-15 23:59:59", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                userDO.setStartTime(now);
                userDO.setEndTime(LocalDateTime.parse("2023-08-15 23:59:59", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//                userDO.setStartTime(now);
//                userDO.setEndTime(now.plusDays(3));
                userService.save(userDO);
                user = userDO;
            }
            // 记录登录时间
            UserActiveDO userActiveDO = new UserActiveDO();
            userActiveDO.setUserId(user.getId());
            userActiveDO.setActiveType(ActiveStatusEnum.LOGIN.getStatus());
            userActiveDO.setActiveTime(now);
            userActiveDO.setIp(WebUtil.getIp());
            userActiveService.save(userActiveDO);
            // 生成token 
            Instant instant = now.toInstant(ZoneOffset.of("+8"));
            String token = TokenUtil.create(user.getId().toString(), instant);
            UserLoginVo res = new UserLoginVo();
            res.setToken(token);
            // 删除验证码，使之失效
            deletePhoneCode(req.getPhone());
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
        if (needHourLimit(phone)) {
            throw new ServiceException("验证码发送次数已达上限，请1小时后再尝试");
        }
        
        if (needDayLimit(phone)) {
            throw new ServiceException("验证码发送次数已达上限，请24小时后再尝试");
        }
        
        // 2. 如果redis的验证码未失效，可以继续使用
        String phoneCode = getPhoneCode(phone);
        String code = StringUtils.isNotEmpty(phoneCode) ? phoneCode : CommonUtils.genCode();
        ResultCode statusEnum = smsClient.sendMsg(phone, code);
        if (ResultCode.SUCCESS == statusEnum) {
            setPhoneCode(phone, code);
            return "发送成功";
        } else {
            throw new ServiceException("验证码发送失败，请稍后重试");
        }
    }

    @Override
    public UserInfoVo getUserInfo(ChatRequest request) {
        return userService.getUserInfo(request);
    }

    @Override
    public String logout(ChatRequest request) {
        UserDO userDO = request.getUser();
        UserActiveDO userActiveDO = new UserActiveDO();
        userActiveDO.setUserId(userDO.getId());
        userActiveDO.setIp(WebUtil.getIp());
        userActiveDO.setActiveType(ActiveStatusEnum.EXIT.getStatus());
        userActiveDO.setActiveTime(LocalDateTime.now());
        return "退出登录成功";
    }
    
    /**
     * 存储验证码和验证码的发送记录
     * @param phone 手机号
     * @param code 验证码
     */
    private void setPhoneCode(String phone, String code) {
        redisService.setValue(PHONE_NUMBER_KEY_SUFFIX + phone, code, PHONE_CODE_VALID_TIME, TimeUnit.SECONDS);
    }

    /**
     * 获取手机验证码
     * @param phone
     * @return
     */
    private String getPhoneCode(String phone) {
        return redisService.getValue(PHONE_NUMBER_KEY_SUFFIX + phone);
    }

    /**
     * 使用后的验证码失效
     * @param phone
     */
    private void deletePhoneCode(String phone) {
        redisService.deleteKey(PHONE_NUMBER_KEY_SUFFIX + phone);
    }

    /**
     * 小时级限流验证
     * @param phone 手机号
     * @return true: 需要限流
     */
    private boolean needHourLimit(String phone) {
        String hourTimeKey = LIMIT_TIME_SUFFIX + "hour:" + phone;
        String hourReqKey = LIMIT_REQUEST_SUFFIX + "hour:" + phone;
        return redisService.limit(hourTimeKey, hourReqKey, PHONE_MAX_REQUEST_HOUR, MAX_REQUEST_TIME_HOUR) <= 0;
    }

    /**
     * 天级限流验证
     * @param phone 手机号
     * @return true: 需要限流
     */
    private boolean needDayLimit(String phone) {
        String dayTimeKey = LIMIT_TIME_SUFFIX + "day:" + phone;
        String dayReqKey = LIMIT_REQUEST_SUFFIX + "day:" + phone;
        return redisService.limit(dayTimeKey, dayReqKey, PHONE_MAX_REQUEST_DAY, MAX_REQUEST_TIME_DAY) <= 0;
    }
}
