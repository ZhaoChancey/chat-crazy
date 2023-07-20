package com.chat.crazy.front.service;

import com.chat.crazy.base.domain.query.ChatRequest;
import com.chat.crazy.front.domain.request.user.SendMsgReq;
import com.chat.crazy.front.domain.request.user.UserRegisterReq;
import com.chat.crazy.front.domain.request.user.VirtualIdReq;
import com.chat.crazy.front.domain.vo.user.InviteCodeVo;
import com.chat.crazy.front.domain.vo.user.UserInfoVo;
import com.chat.crazy.front.domain.request.user.UserLoginReq;
import com.chat.crazy.front.domain.vo.user.UserLoginVo;
import com.chat.crazy.front.domain.vo.user.UserRegisterVo;

import javax.servlet.http.HttpServletResponse;

public interface AuthService {

    String genVirId(VirtualIdReq req);
    /**
     * 检验jwt token有效性
     * @param token
     * @param response
     * @return
     */
    boolean verifyWebToken(String token, HttpServletResponse response);

    /**
     * 用户注册
     * @param req
     * @return
     */
    UserRegisterVo register(UserRegisterReq req);

    /**
     * 用户登录
     * @param req
     * @return
     */
    UserLoginVo login(UserLoginReq req);

    /**
     * 邀请码生成
     * @param request
     * @return
     */
    InviteCodeVo inviteCode(ChatRequest request);
    /**
     * 发送验证码
     * @param req
     * @return
     */
    String sendMes(SendMsgReq req);

    UserInfoVo getUserInfo(ChatRequest request);

    String logout(ChatRequest request);
}
