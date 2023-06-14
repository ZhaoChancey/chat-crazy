package com.chat.crazy.front.domain.vo.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author:
 * @Description:
 * @Date: 2023/4/23 上午9:57
 */
@Data
@Schema(title = "聊天窗口创建/编辑响应")
public class ChatSessionOperateVO {
    
    @Schema(title = "聊天窗口id")
    private Long sessionId;
}
