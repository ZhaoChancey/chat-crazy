package com.chat.crazy.front.domain.vo.chat;

import com.chat.crazy.base.domain.query.PageVo;
import com.chat.crazy.front.domain.request.chat.ChatProcessRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @Author:
 * @Description:
 * @Date: 2023/4/23 上午10:16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(title = "聊天记录获取响应")
public class ChatSessionHistoryVO extends PageVo<ChatSessionHistoryVO.SessionHistoryInfo> {
    
    @Data
    @Schema(title = "聊天记录列表项")
    public static class SessionHistoryInfo {
        @Schema(title = "消息配置")
        private ChatProcessRequest.Options options;
        
        @Schema(title = "消息id")
        private String messageId;

        @Schema(title = "消息类型")
        private Integer messageType;

        @Schema(title = "创建时间")
        private LocalDateTime dateTime;

        @Schema(title = "内容")
        private String text;

        @Schema(title = "标识请求是否出错")
        private boolean error;
    }
    
}
