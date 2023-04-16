package com.chat.crazy.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chat.crazy.SmsClient;
import com.chat.crazy.config.ChatConfig;
import com.chat.crazy.domain.entity.UserActiveDO;
import com.chat.crazy.domain.entity.UserDO;
import com.chat.crazy.domain.entity.VirtualUserDO;
import com.chat.crazy.domain.request.RegisterRequest;
import com.chat.crazy.domain.request.VerifySecretRequest;
import com.chat.crazy.domain.request.user.*;
import com.chat.crazy.domain.vo.ApiModelVO;
import com.chat.crazy.exception.AuthException;
import com.chat.crazy.exception.ServiceException;
import com.chat.crazy.handler.response.ResultStatusEnum;
import com.chat.crazy.service.AuthService;
import com.chat.crazy.service.UserActiveService;
import com.chat.crazy.service.UserService;
import com.chat.crazy.service.VirtualService;
import com.chat.crazy.util.CommonUtils;
import com.chat.crazy.util.Md5Util;
import com.chat.crazy.util.WebTokenUtil;
import com.chat.crazy.util.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Objects;

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
    private ChatConfig chatConfig;

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
        UserActiveDO lastLoginInfo = userActiveService.getLastLoginInfo(userId);
        LocalDateTime lastLogin = lastLoginInfo.getActiveTime();
        //根据用户登录时间，拿到用户申请Token时的secretKey
        String secretKey = WebTokenUtil.genSecretKey(lastLogin.toInstant(ZoneOffset.of("+8")));

        try {
            //校验
            WebTokenUtil.verify(secretKey, token);
        } catch (SignatureVerificationException e) {
            log.error(e.getMessage());
            return false;
        } catch (TokenExpiredException e) {
            // 允许一段时间有效时间同时返回新的token
            String newToken = WebTokenUtil.getRefreshToken(secretKey, jwtToken);
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
            Long userId;
            LocalDateTime now = LocalDateTime.now();
            // 登录成功
            UserDO user = userService.getOne(new LambdaQueryWrapper<UserDO>().eq(UserDO::getPhone, req.getPhone()));
            if (user == null) {
                // 保存用户
                UserDO userDO = new UserDO();
                userDO.setPhone(req.getPhone());
                userDO.setStatus(1);
                userDO.setNickName("小柴");
                userDO.setVipType(0);
                userDO.setStartTime(now);
                userDO.setEndTime(now.plusDays(3));
                userService.save(userDO);
                userId = userDO.getId();
            } else {
                userId = user.getId();
            }
            // 记录登录时间
            UserActiveDO userActiveDO = new UserActiveDO();
            userActiveDO.setUserId(userId);
            userActiveDO.setActiveType(0);
            userActiveDO.setActiveTime(now);
            userActiveDO.setIp(WebUtil.getIp());
            userActiveService.save(userActiveDO);
            // 生成token
            String secretKey = WebTokenUtil.genSecretKey(now.toInstant(ZoneOffset.of("+8")));
            String token = WebTokenUtil.create(secretKey, user.getId().toString(), now.toInstant(ZoneOffset.of("+8")));
            UserLoginRes res = new UserLoginRes();
            res.setToken(token);
            return res;
        } else {
            throw new AuthException("验证码校验失败，请重新输入");
        }
    }

    public String genVirId(VirtualIdReq req) {
        return virtualService.genVirId(req);
    }


    public String verifySecretKey(VerifySecretRequest verifySecretRequest) {
        if (BooleanUtil.isFalse(chatConfig.hasAuth())) {
            return "未设置密码";
        }

        if (StrUtil.isEmpty(verifySecretRequest.getToken())) {
            throw new ServiceException("Secret key is empty");
        }

        if (Objects.equals(verifySecretRequest.getToken(), chatConfig.getAuthSecretKey())) {
            return "Verify successfully";
        }

        throw new ServiceException("密钥无效 | Secret key is invalid");
    }

    public ApiModelVO getApiModel() {
        ApiModelVO apiModelVO = new ApiModelVO();
        apiModelVO.setAuth(chatConfig.hasAuth());
        apiModelVO.setModel(chatConfig.getApiTypeEnum());
        return apiModelVO;
    }

    @Override
    public String sendMes(SendMsgReq req) {
        String code = CommonUtils.genCode();
        ResultStatusEnum statusEnum = smsClient.sendMsg(req.getPhone(), code);
        if (ResultStatusEnum.SUCCESS == statusEnum) {
            redisService.setPhoneCode(req.getPhone(), code);
            return "发送成功";
        } else {
            throw new ServiceException("验证码发送失败，请重试");
        }
    }

    @Override
    public UserInfoRes getUserInfo() {
        UserInfoRes userInfoRes = new UserInfoRes();
        UserInfoRes.UserIdentity identity = new UserInfoRes.UserIdentity();
        if (StringUtils.isEmpty(WebUtil.getRequest().getHeader("token"))) {
            // 未登录
            identity.setVipType(-1);
            identity.setStartTs(0L);
            identity.setEndTs(0L);
            identity.setIsUsed(true);
            userInfoRes.setIdentity(identity);
            userInfoRes.setNickName("小柴");
            userInfoRes.setPhone("");
        } else {
            // 登录
            String token = WebUtil.getRequest().getHeader("token");
            DecodedJWT jwt = WebTokenUtil.decode(token);
            if (jwt == null) {
                throw new AuthException("token校验失败");
            }
            Long userId = Long.parseLong(jwt.getSubject());
            UserDO userDO = userService.getById(userId);
            identity.setVipType(userDO.getVipType());
            identity.setStartTs(userDO.getStartTime().toInstant(ZoneOffset.of("+8")).toEpochMilli());
            identity.setEndTs(userDO.getEndTime().toInstant(ZoneOffset.of("+8")).toEpochMilli());
            identity.setIsUsed(!(userDO.getVipType() == 0 && userDO.getEndTime().isAfter(LocalDateTime.now())));
            userInfoRes.setIdentity(identity);
            userInfoRes.setId(userId);
            userInfoRes.setNickName(userDO.getNickName());
            userInfoRes.setPhone(userDO.getPhone());
        }
        return userInfoRes;
    }


    @Override
    public String logout() {
        String token = WebUtil.getRequest().getHeader("token");
        DecodedJWT jwt = WebTokenUtil.decode(token);
        if (jwt == null) {
            throw new AuthException("token校验失败");
        }
        Long userId = Long.parseLong(jwt.getSubject());
        UserDO userDO = userService.getById(userId);
        if (userDO == null) {
            throw new AuthException("该用户不存在");
        }
        UserActiveDO userActiveDO = new UserActiveDO();
        userActiveDO.setUserId(userDO.getId());
        userActiveDO.setIp(WebUtil.getIp());
        userActiveDO.setActiveType(1);
        userActiveDO.setActiveTime(LocalDateTime.now());
        return "退出登录成功";
    }
}
