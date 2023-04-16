package com.chat.crazy.handler.emitter;

import cn.hutool.core.util.StrUtil;
import com.chat.crazy.config.ChatConfig;
import com.chat.crazy.domain.request.ChatProcessRequest;
import com.chat.crazy.domain.entity.ChatMessageDO;
import com.chat.crazy.enums.ApiTypeEnum;
import com.chat.crazy.enums.ConversationModelEnum;
import com.chat.crazy.util.ObjectMapperUtil;
import com.chat.crazy.api.accesstoken.AccessTokenApiClient;
import com.chat.crazy.api.accesstoken.ConversationRequest;
import com.chat.crazy.api.listener.ParsedEventSourceListener;
import com.chat.crazy.api.listener.ResponseBodyEmitterStreamListener;
import com.chat.crazy.api.parser.AccessTokenChatResponseParser;
import com.chat.crazy.api.storage.AccessTokenDatabaseDataStorage;
import com.chat.crazy.service.ChatMessageService;
import com.unfbx.chatgpt.entity.chat.Message;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.UUID;

/**
 * @author hncboy
 * @date 2023/3/24 13:12
 * AccessToken 响应处理
 */
@Component
public class AccessTokenResponseEmitter implements ResponseEmitter {

    @Resource
    private ChatConfig chatConfig;

    @Resource
    private ChatMessageService chatMessageService;

    @Resource
    private AccessTokenChatResponseParser parser;

    @Resource
    private AccessTokenDatabaseDataStorage dataStorage;

    @Override
    public ResponseBodyEmitter requestToResponseEmitter(ChatProcessRequest chatProcessRequest, ResponseBodyEmitter emitter) {
        // 构建 accessTokenApiClient
        AccessTokenApiClient accessTokenApiClient = AccessTokenApiClient.builder()
                .accessToken(chatConfig.getOpenaiAccessToken())
                .reverseProxy(chatConfig.getApiReverseProxy())
                .model(chatConfig.getOpenaiApiModel())
                .build();

        // 初始化聊天消息
        ChatMessageDO chatMessageDO = chatMessageService.initChatMessage(chatProcessRequest, ApiTypeEnum.ACCESS_TOKEN);

        // 构建 ConversationRequest
        ConversationRequest conversationRequest = buildConversationRequest(chatMessageDO);

        // 构建事件监听器
        ParsedEventSourceListener parsedEventSourceListener = new ParsedEventSourceListener.Builder()
//                .addListener(new ConsoleStreamListener())
                .addListener(new ResponseBodyEmitterStreamListener(emitter))
                .setParser(parser)
                .setDataStorage(dataStorage)
                .setOriginalRequestData(ObjectMapperUtil.toJson(conversationRequest))
                .setChatMessageDO(chatMessageDO)
                .build();

        // 发送请求
        accessTokenApiClient.streamChatCompletions(conversationRequest, parsedEventSourceListener);
        return emitter;
    }

    /**
     * 构建 ConversationRequest
     *
     * @param chatMessageDO 聊天消息
     * @return ConversationRequest
     */
    private ConversationRequest buildConversationRequest(ChatMessageDO chatMessageDO) {
        // 构建 content
        ConversationRequest.Content content = ConversationRequest.Content.builder()
                .parts(Collections.singletonList(chatMessageDO.getContent()))
                .build();

        // 构建 Message
        ConversationRequest.Message message = ConversationRequest.Message.builder()
                .id(chatMessageDO.getMessageId())
                .role(Message.Role.USER.getName())
                .content(content)
                .build();

        // 构建 ConversationRequest
        String model = chatConfig.getOpenaiApiModel();

        return ConversationRequest.builder()
                .messages(Collections.singletonList(message))
                .action(ConversationRequest.MessageActionTypeEnum.NEXT)
                .model(ConversationModelEnum.NAME_MAP.get(model))
                // 父级消息 id 不能为空，不然会报错，因此第一条消息也需要随机生成一个
                .parentMessageId(StrUtil.isBlank(chatMessageDO.getParentMessageId()) ? UUID.randomUUID().toString() : chatMessageDO.getParentMessageId())
                .conversationId(chatMessageDO.getConversationId())
                .build();
    }
}
