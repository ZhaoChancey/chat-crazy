package com.chat.crazy.controller;

import com.chat.crazy.annotation.FrontPreAuth;
import com.chat.crazy.domain.request.ChatProcessRequest;
import com.chat.crazy.domain.vo.ChatConfigVO;
import com.chat.crazy.service.ChatService;
import com.chat.crazy.handler.response.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * @author hncboy
 * @date 2023/3/22 19:47
 * 聊天相关接口
 */
@FrontPreAuth
@AllArgsConstructor
@Tag(name = "聊天相关接口")
@RestController
@RequestMapping
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "配置信息")
    @PostMapping("/config")
    public R<ChatConfigVO> chatConfig() {
        return R.data(chatService.getChatConfig());
    }

    @Operation(summary = "消息处理")
    @PostMapping("/chat-process")
    public ResponseBodyEmitter chatProcess(@RequestBody @Validated ChatProcessRequest chatProcessRequest, HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        return chatService.chatProcess(chatProcessRequest);
    }

    @PostMapping("/health")
    public String ping() {
        return "pong";
    }

}
