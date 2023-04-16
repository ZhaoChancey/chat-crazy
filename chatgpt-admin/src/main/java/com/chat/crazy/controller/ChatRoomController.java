package com.chat.crazy.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chat.crazy.domain.query.ChatRoomPageQuery;
import com.chat.crazy.domain.vo.ChatRoomVO;
import com.chat.crazy.service.ChatRoomService;
import com.chat.crazy.annotation.ApiAdminRestController;
import com.chat.crazy.handler.response.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author hncboy
 * @date 2023/3/27 23:13
 * 聊天室相关接口
 */
@AllArgsConstructor
@Tag(name = "管理端-聊天室相关接口")
@ApiAdminRestController("/chat_room")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @Operation(summary = "聊天室分页列表")
    @PostMapping("/page")
    public R<IPage<ChatRoomVO>> page(@Validated @RequestBody ChatRoomPageQuery chatRoomPageQuery) {
        return R.data(chatRoomService.pageChatRoom(chatRoomPageQuery));
    }
}
