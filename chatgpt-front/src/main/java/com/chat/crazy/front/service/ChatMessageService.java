package com.chat.crazy.front.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chat.crazy.base.domain.entity.ChatMessageDO;
import com.chat.crazy.base.enums.ApiTypeEnum;
import com.chat.crazy.front.domain.request.chat.ChatProcessRequest;
import com.chat.crazy.front.domain.request.chat.ChatSessionHistoryReq;

import java.util.List;
import java.util.Optional;

/**
 * @author hncboy
 * @date 2023/3/25 16:30
 * 聊天记录相关业务接口
 */
public interface ChatMessageService extends IService<ChatMessageDO> {

    /**
     * 初始化聊天消息
     *
     * @param chatProcessRequest 消息处理请求参数
     * @param apiTypeEnum        API 类型
     * @return 聊天消息
     */
    ChatMessageDO initChatMessage(ChatProcessRequest chatProcessRequest);

    /**
     * 获取聊天记录详情
     * @param req
     * @param lastMessage
     * @return
     */
    Page<ChatMessageDO> getPagedMessages(ChatSessionHistoryReq req, ChatMessageDO lastMessage);
}
