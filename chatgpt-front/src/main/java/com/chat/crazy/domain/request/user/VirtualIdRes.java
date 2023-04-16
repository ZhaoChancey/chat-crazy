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
@Schema(title = "生成虚拟id回参")
public class VirtualIdRes {
    
    @Schema(title = "虚拟id")
    private String virId;
}
