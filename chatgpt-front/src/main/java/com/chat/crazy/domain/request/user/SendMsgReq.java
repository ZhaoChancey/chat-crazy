package com.chat.crazy.domain.request.user;

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
    
    @NotEmpty
    @Schema(title = "手机号")
    private String phone;
}
