package com.chat.crazy.front.domain.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author:
 * @Description:
 * @Date: 2023/4/14 下午2:20
 */
@Data
@Schema(title = "生成虚拟id回参")
public class VirtualIdVo {
    
    @Schema(title = "虚拟id")
    private String virId;
}
