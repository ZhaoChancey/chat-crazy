package com.hncboy.chatgpt.front.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Size;

/**
 * @date 2023/3/23 13:17
 * 用户注册处理请求
 */
@Data
@Schema(title = "用户注册请求")
public class RegisterRequest {


    @Size(max = 20, message = "用户名长度[0, 20]")
    @Schema(title = "用户名")
    private String username;

    @Size(max = 30, message = "密码长度[0, 30]")
    @Schema(title = "密码")
    public String password;

    @Schema(title = "手机号")
    public String phone;

    @Size(max = 6, message = "验证码长度不超过6位")
    @Schema(title = "验证码")
    public String code;
}
