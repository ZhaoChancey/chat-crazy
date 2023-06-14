package com.chat.crazy.front.controller;

import cn.hutool.core.date.DateUtil;
import com.chat.crazy.base.annotation.FrontPreAuth;
import com.chat.crazy.base.domain.query.PageQuery;
import com.chat.crazy.front.domain.request.chat.ChatSessionHistoryReq;
import com.chat.crazy.front.domain.request.chat.ChatSessionOperateReq;
import com.chat.crazy.front.domain.vo.chat.ChatSessionHistoryVO;
import com.chat.crazy.front.domain.vo.chat.ChatSessionListVO;
import com.chat.crazy.front.domain.vo.chat.ChatSessionOperateVO;
import com.chat.crazy.base.handler.response.R;
import com.chat.crazy.front.domain.request.chat.ChatProcessRequest;
import com.chat.crazy.front.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "聊天相关接口")
@RestController
@RequestMapping("/chat")
public class ChatController {

    @Resource
    private ChatService chatService;

    @FrontPreAuth(tokenAuth = false)
    @Operation(summary = "创建/编辑聊天窗口")
    @PostMapping("/session/operate")
    public R<ChatSessionOperateVO> operateSession(@RequestBody ChatSessionOperateReq req) {
        return R.data(chatService.operateSession(req)); 
    }

    @Operation(summary = "消息处理")
    @FrontPreAuth(tokenAuth = false)
    @PostMapping("/msg/send")
    public ResponseBodyEmitter sendMessage(@RequestBody @Validated ChatProcessRequest chatProcessRequest, HttpServletResponse response) {
        chatProcessRequest.setSystemMessage("You are ChattGPT, a large language model trained by OpenAI. Answer as concisely as possible.\\nKnowledge cutoff: 2021-09-01\\nCurrent date: ".concat(DateUtil.today()));
        response.setHeader("cache-control", "public, max-age=0, must-revalidate");
        response.setHeader("X-Accel-Buffering", "no");
//        response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        return chatService.sendMessage(chatProcessRequest);
    }
    
    @PostMapping("/session/list")
    @Operation(summary = "获取聊天记录列表")
    @FrontPreAuth(tokenAuth = false)
    public R<ChatSessionListVO> getSessionList(@RequestBody @Validated PageQuery query) {
         return R.data(chatService.getSessionList(query));
    }

    @PostMapping("/session/history")
    @Operation(summary = "获取聊天记录")
    @FrontPreAuth(tokenAuth = false)
    public R<ChatSessionHistoryVO> getSessionHistory(@RequestBody @Validated ChatSessionHistoryReq query) {
        return R.data(chatService.getSessionHistory(query));
    }
    
    @PostMapping("/health")
    public String ping() {
        return "pong";
    }
}
