package com.chat.crazy.front.controller;

import com.chat.crazy.base.annotation.FrontPreAuth;
import com.chat.crazy.base.domain.query.ChatRequest;
import com.chat.crazy.base.handler.response.R;
import com.chat.crazy.front.service.AuthService;
import com.chat.crazy.front.domain.request.user.SendMsgReq;
import com.chat.crazy.front.domain.request.user.UserLoginReq;
import com.chat.crazy.front.domain.request.user.VirtualIdReq;
import com.chat.crazy.front.domain.vo.user.UserInfoVo;
import com.chat.crazy.front.domain.vo.user.UserLoginVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author hncboy
 * @date 2023/3/22 19:48
 * 鉴权相关接口
 */
@Tag(name = "鉴权相关接口")
@RestController
@RequestMapping("/user")
public class AuthController {

    @Resource
    private AuthService authService;

    @Operation(summary = "生成虚拟id")
    @PostMapping("/virId")
    public R<String> genVirId(@RequestBody @Validated VirtualIdReq req) {
        return R.success(authService.genVirId(req));
    }

    @FrontPreAuth(tokenAuth = false)
    @Operation(summary = "获取用户信息")
    @PostMapping("/info")
    public R<UserInfoVo> getUserInfo(ChatRequest request) {
        return R.success(authService.getUserInfo(request));
    }

    @FrontPreAuth(tokenAuth = false)
    @Operation(summary = "验证码发送")
    @PostMapping("/sendMsg")
    public R<String> sendMes(@RequestBody @Validated SendMsgReq req) {
        return R.success(authService.sendMes(req));
    }

    @FrontPreAuth(tokenAuth = false)
    @Operation(summary = "登录")
    @PostMapping("/login")
    public R<UserLoginVo> login(@RequestBody @Validated UserLoginReq req) {
        return R.success(authService.login(req));
    }

    @FrontPreAuth(virIdAuth = false)
    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public R<String> logout(ChatRequest request) {
        return R.success(authService.logout(request));
    }
}
