package com.chat.crazy.front.domain.request.chat;

import com.chat.crazy.base.domain.query.PageQuery;
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
@Schema(title = "历史聊天记录列表请求")
public class ChatSessionListReq extends PageQuery {
}
