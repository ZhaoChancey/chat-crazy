package com.chat.crazy.front.domain.vo.chat;

import com.chat.crazy.base.domain.query.PageVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author:
 * @Description:
 * @Date: 2023/4/23 上午10:16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(title = "历史聊天记录列表响应")
public class ChatSessionListVO extends PageVo<ChatSessionListVO.SessionInfo> {
     
    @Data
    @Schema(title = "聊天记录列表项")
    public static class SessionInfo {
        @Schema(title = "聊天窗口id")
        private String id;
        
        @Schema(title = "聊天窗口标题")
        private String title;
        
        @Schema(title = "最新修改时间")
        private long lastTimeTs;
    }
    
}
