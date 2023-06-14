package com.chat.crazy.front.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chat.crazy.base.config.ChatConfig;
import com.chat.crazy.base.domain.entity.ChatMessageDO;
import com.chat.crazy.base.domain.entity.ChatRoomDO;
import com.chat.crazy.base.enums.ApiTypeEnum;
import com.chat.crazy.base.enums.ChatMessageStatusEnum;
import com.chat.crazy.base.enums.ChatMessageTypeEnum;
import com.chat.crazy.base.exception.ServiceException;
import com.chat.crazy.front.domain.request.chat.ChatSessionHistoryReq;
import com.chat.crazy.front.service.ChatMessageService;
import com.chat.crazy.front.service.ChatRoomService;
import com.chat.crazy.base.util.WebUtil;
import com.chat.crazy.front.domain.request.chat.ChatProcessRequest;
import com.chat.crazy.front.mapper.ChatMessageMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author hncboy
 * @date 2023/3/25 16:33
 * 聊天记录相关业务实现类
 */
@Service("FrontChatMessageServiceImpl")
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessageDO> implements ChatMessageService {

    @Resource
    private ChatConfig chatConfig;

    @Resource
    private ChatRoomService chatRoomService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ChatMessageDO initChatMessage(ChatProcessRequest chatProcessRequest) {
        ChatMessageDO chatMessageDO = new ChatMessageDO();
        chatMessageDO.setId(IdWorker.getId());
        // 消息 id 手动生成
        chatMessageDO.setMessageId(UUID.randomUUID().toString());
        chatMessageDO.setMessageType(ChatMessageTypeEnum.QUESTION.getCode());
//        chatMessageDO.setApiType(apiTypeEnum);
//        if (apiTypeEnum == ApiTypeEnum.API_KEY) {
//            chatMessageDO.setApiKey(chatConfig.getOpenaiApiKey());
//        }
        chatMessageDO.setUserId(chatProcessRequest.getUserId());
        chatMessageDO.setUserType(chatProcessRequest.getUserType());
        chatMessageDO.setContent(chatProcessRequest.getPrompt());
        chatMessageDO.setModelName(chatConfig.getOpenaiApiModel());
        chatMessageDO.setOriginalData(null);
        chatMessageDO.setPromptTokens(-1);
        chatMessageDO.setCompletionTokens(-1);
        chatMessageDO.setTotalTokens(-1);
        chatMessageDO.setIp(WebUtil.getIp());
        chatMessageDO.setStatus(ChatMessageStatusEnum.INIT.getCode());
        chatMessageDO.setCreateTime(LocalDateTime.now());
        chatMessageDO.setUpdateTime(LocalDateTime.now());

        // 填充初始化父级消息参数
        populateInitParentMessage(chatMessageDO, chatProcessRequest);

        save(chatMessageDO);
        return chatMessageDO;
    }

    @Override
    public Page<ChatMessageDO> getPagedMessages(ChatSessionHistoryReq req, ChatMessageDO lastChatMessageDO) {
        Long lastId = Optional.ofNullable(lastChatMessageDO).map(ChatMessageDO::getId).orElse(null);
        Page<ChatMessageDO> page = new Page<>(req.getPageNum(), req.getPageSize());
        return getBaseMapper().selectPage(page, new LambdaQueryWrapper<ChatMessageDO>()
                .eq(ChatMessageDO::getChatRoomId, req.getSessionId())
                .eq(ChatMessageDO::getUserId, req.getUserId())
                .eq(ChatMessageDO::getUserType, req.getUserType())
                .lt(lastId != null, ChatMessageDO::getId, lastId)
                .orderByDesc(ChatMessageDO::getId));
    }

    /**
     * 填充初始化父级消息参数
     *
     * @param chatMessageDO      消息记录
     * @param chatProcessRequest 消息处理请求参数
     */
    private void populateInitParentMessage(ChatMessageDO chatMessageDO, ChatProcessRequest chatProcessRequest) {
        // 父级消息 id
        String parentMessageId = Optional.ofNullable(chatProcessRequest.getOptions())
                .map(ChatProcessRequest.Options::getParentMessageId)
                .orElse(null);

        // 对话 id
        String conversationId = Optional.ofNullable(chatProcessRequest.getOptions())
                .map(ChatProcessRequest.Options::getConversationId)
                .orElse(null);

        if (StrUtil.isAllNotBlank(parentMessageId, conversationId)) {
            // 寻找父级消息
            ChatMessageDO parentChatMessage = getOne(new LambdaQueryWrapper<ChatMessageDO>()
                    .eq(ChatMessageDO::getUserId, chatProcessRequest.getUserId())
                    .eq(ChatMessageDO::getUserType, chatProcessRequest.getUserType())
                    // 消息 id 一致
                    .eq(ChatMessageDO::getMessageId, parentMessageId)
                    // 对话 id 一致
                    .eq(ChatMessageDO::getConversationId, conversationId));
                    // 消息类型为回答
//                    .eq(ChatMessageDO::getMessageType, ChatMessageTypeEnum.ANSWER.getCode()));
            if (Objects.isNull(parentChatMessage)) {
                throw new ServiceException("父级消息不存在，本次对话出错，请先关闭上下文或开启新的对话窗口");
            }

            chatMessageDO.setParentMessageId(parentMessageId);
            chatMessageDO.setParentAnswerMessageId(parentMessageId);
            chatMessageDO.setParentQuestionMessageId(parentChatMessage.getParentQuestionMessageId());
            chatMessageDO.setChatRoomId(parentChatMessage.getChatRoomId());
            chatMessageDO.setConversationId(parentChatMessage.getConversationId());
            chatMessageDO.setContextCount(parentChatMessage.getContextCount() + 1);
            chatMessageDO.setQuestionContextCount(parentChatMessage.getQuestionContextCount() + 1);

//            // ApiKey 限制上下文问题的数量
//            if (chatMessageDO.getQuestionContextCount() > chatConfig.getLimitQuestionContextCount()) {
//                throw new ServiceException(StrUtil.format("当前允许连续对话的问题数量为[{}]次，已达到上限，请关闭上下文对话重新发送", chatConfig.getLimitQuestionContextCount()));
//            }
        } else {
            // 创建新聊天室
//            ChatRoomDO chatRoomDO = chatRoomService.createChatRoom(chatMessageDO);
            ChatRoomDO chatRoomDO = new ChatRoomDO();
            chatRoomDO.setId(chatProcessRequest.getSessionId());
            if (StringUtils.isEmpty(chatProcessRequest.getChatRoomDO().getFirstMessageId())) {
                chatRoomDO.setTitle(StrUtil.sub(chatMessageDO.getContent(), 0, 50));
            }
            chatRoomDO.setFirstChatMessageId(chatMessageDO.getId());
            chatRoomDO.setFirstMessageId(chatMessageDO.getMessageId());
            chatRoomService.updateById(chatRoomDO);
            chatMessageDO.setChatRoomId(chatProcessRequest.getSessionId());
            chatMessageDO.setContextCount(1L);
            chatMessageDO.setQuestionContextCount(1L);
        }
    }
}
