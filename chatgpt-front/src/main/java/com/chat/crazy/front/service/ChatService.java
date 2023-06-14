package com.chat.crazy.front.service;

import com.chat.crazy.base.domain.query.PageQuery;
import com.chat.crazy.front.domain.request.chat.ChatSessionHistoryReq;
import com.chat.crazy.front.domain.request.chat.ChatSessionOperateReq;
import com.chat.crazy.front.domain.vo.chat.ChatSessionHistoryVO;
import com.chat.crazy.front.domain.vo.chat.ChatSessionListVO;
import com.chat.crazy.front.domain.vo.chat.ChatSessionOperateVO;
import com.chat.crazy.front.domain.request.chat.ChatProcessRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/**
 * @author hncboy
 * @date 2023/3/22 19:41
 * 聊天相关业务接口
 */
public interface ChatService {

    /**
     * 创建/编辑/删除聊天窗口
     * @param req
     * @return
     */
    ChatSessionOperateVO operateSession(ChatSessionOperateReq req);
    /**
     * 消息处理
     *
     * @param chatProcessRequest 消息处理请求参数
     * @return emitter
     */
    ResponseBodyEmitter sendMessage(ChatProcessRequest chatProcessRequest);

    /**
     * 获取聊天列表
     * @param query
     * @return
     */
    ChatSessionListVO getSessionList(PageQuery query);

    /**
     * 获取详细的聊天记录
     * @param req
     * @return
     */
    ChatSessionHistoryVO getSessionHistory(ChatSessionHistoryReq req);
}
