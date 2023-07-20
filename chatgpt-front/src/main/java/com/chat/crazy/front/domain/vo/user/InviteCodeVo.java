package com.chat.crazy.front.domain.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author:
 * @Description:
 * @Date: 2023/4/14 下午2:20
 */
@Data
@Schema(title = "邀请码生成回参")
public class InviteCodeVo {
    
    @Schema(title = "jwt token")
    private String inviteCode;
}
