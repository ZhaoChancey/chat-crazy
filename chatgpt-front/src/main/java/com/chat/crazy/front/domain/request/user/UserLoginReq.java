package com.chat.crazy.front.domain.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @Author:
 * @Description:
 * @Date: 2023/4/14 下午2:20
 */
@Data
@Schema(title = "用户登录请求")
public class UserLoginReq {
    
    @NotEmpty
    @Schema(title = "手机号")
    private String phone;

//    @NotEmpty
    @Schema(title = "验证码")
    private String code = "";
}
