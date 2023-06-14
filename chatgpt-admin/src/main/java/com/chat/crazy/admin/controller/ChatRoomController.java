package com.chat.crazy.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chat.crazy.admin.domain.query.ChatRoomPageQuery;
import com.chat.crazy.admin.domain.vo.ChatRoomVO;
import com.chat.crazy.base.handler.response.R;
import com.chat.crazy.admin.service.ChatRoomService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author hncboy
 * @date 2023/3/27 23:13
 * 聊天室相关接口
 */
//@Tag(name = "管理端-聊天室相关接口")
@RestController
@RequestMapping("/room")
public class ChatRoomController {

    @Resource(name = "chatRoom")
    ChatRoomService chatRoomService;

//    @Operation(summary = "聊天室分页列表")
    @PostMapping("/page")
    public R<IPage<ChatRoomVO>> pageChatRoomV2(@Validated @RequestBody ChatRoomPageQuery chatRoomPageQuery) {
        return R.data(chatRoomService.pageChatRoomV2());
    }
}
