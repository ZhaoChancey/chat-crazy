package com.chat.crazy.front.domain.request.chat;

import com.chat.crazy.base.domain.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @Author:
 * @Description:
 * @Date: 2023/4/23 上午10:16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(title = "聊天记录获取请求")
public class ChatSessionHistoryReq extends PageQuery {
    
    @Schema(title = "聊天窗口id")
    @NotNull
    private Long sessionId;
    
    @Schema(title = "上次分页list中最后一条消息的id")
    private String lastMessageId;
}
