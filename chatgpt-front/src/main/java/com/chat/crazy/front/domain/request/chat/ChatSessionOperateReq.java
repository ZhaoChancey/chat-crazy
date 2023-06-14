package com.chat.crazy.front.domain.request.chat;

import com.chat.crazy.base.domain.query.ChatRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @Author:
 * @Description:
 * @Date: 2023/4/23 上午9:57
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(title = "聊天窗口创建/编辑请求")
public class ChatSessionOperateReq extends ChatRequest {
    private Long sessionId;
    
    @Schema(title = "0: 创建，1：编辑窗口标题")
    @NotNull
    private Integer type;
    
    @Schema(title = "标题")
    private String title;
}
