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
@Schema(title = "发送验证码请求")
public class SendMsgReq {
    
    @NotEmpty(message = "手机号不能为空")
    @Schema(title = "手机号")
    private String phone;
}
