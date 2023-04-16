package com.chat.crazy.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chat.crazy.domain.query.ChatMessagePageQuery;
import com.chat.crazy.domain.vo.ChatMessageVO;
import com.chat.crazy.service.ChatMessageService;
import com.chat.crazy.annotation.ApiAdminRestController;
import com.chat.crazy.handler.response.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;

/**
 * @author hncboy
 * @date 2023/3/27 21:39
 * 聊天记录相关接口
 */
@AllArgsConstructor
@Tag(name = "管理端-聊天记录相关接口")
@ApiAdminRestController("/chat_message")
public class ChatMessageController {

    @Resource
    private final ChatMessageService chatMessageService;

    @Operation(summary = "记录分页列表")
    @PostMapping("/page")
    public R<IPage<ChatMessageVO>> page(@Validated @RequestBody ChatMessagePageQuery chatMessagePageQuery) {
        return R.data(chatMessageService.pageChatMessage(chatMessagePageQuery));
    }
}
