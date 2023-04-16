package com.chat.crazy.service;

import com.chat.crazy.domain.request.user.SendMsgReq;
import com.chat.crazy.domain.request.user.UserInfoRes;
import com.chat.crazy.domain.request.user.UserLoginReq;
import com.chat.crazy.domain.request.user.UserLoginRes;

import javax.servlet.http.HttpServletResponse;

public interface AuthService {

    /**
     * 检验jwt token有效性
     * @param token
     * @param response
     * @return
     */
    boolean verifyWebToken(String token, HttpServletResponse response);

    /**
     * 用户登录
     * @param req
     * @return
     */
    UserLoginRes login(UserLoginReq req);

    /**
     * 发送验证码
     * @param req
     * @return
     */
    String sendMes(SendMsgReq req);

    UserInfoRes getUserInfo();

    String logout();
}
