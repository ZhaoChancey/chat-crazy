package com.chat.crazy.controller;

import com.chat.crazy.annotation.FrontPreAuth;
import com.chat.crazy.domain.request.RegisterRequest;
import com.chat.crazy.domain.request.VerifySecretRequest;
import com.chat.crazy.domain.request.user.*;
import com.chat.crazy.handler.response.R;
import com.chat.crazy.service.impl.AuthServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
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
    private AuthServiceImpl authService;

    @Operation(summary = "生成虚拟id")
    @PostMapping("/virId")
    public R<String> genVirId(@RequestBody @Validated VirtualIdReq req) {
        return R.success(authService.genVirId(req));
    }

    @FrontPreAuth(customize = 1)
    @Operation(summary = "获取用户信息")
    @PostMapping("/info")
    public R<UserInfoRes> getUserInfo() {
        return R.success(authService.getUserInfo());
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
    public R<UserLoginRes> login(@RequestBody @Validated UserLoginReq req) {
        return R.success(authService.login(req));
    }

    @FrontPreAuth
    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public R<String> logout() {
        return R.success(authService.logout());
    }
}
