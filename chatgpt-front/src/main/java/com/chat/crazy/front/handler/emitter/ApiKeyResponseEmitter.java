package com.chat.crazy.front.handler.emitter;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chat.crazy.base.config.ChatConfig;
import com.chat.crazy.base.domain.entity.ChatMessageDO;
import com.chat.crazy.base.enums.ApiKeyTokenLimiterEnum;
import com.chat.crazy.base.enums.ChatMessageStatusEnum;
import com.chat.crazy.base.enums.ChatMessageTypeEnum;
import com.chat.crazy.front.domain.vo.chat.ChatReplyMessageVO;
import com.chat.crazy.front.service.ChatMessageService;
import com.chat.crazy.base.util.ObjectMapperUtil;
import com.chat.crazy.front.domain.request.chat.ChatProcessRequest;
import com.chat.crazy.front.api.apikey.ApiKeyChatClientBuilder;
import com.chat.crazy.front.api.listener.ParsedEventSourceListener;
import com.chat.crazy.front.api.listener.ResponseBodyEmitterStreamListener;
import com.chat.crazy.front.api.parser.ChatCompletionResponseParser;
import com.chat.crazy.front.api.storage.ApiKeyDatabaseDataStorage;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.utils.TikTokensUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Objects;

/**
 * @author hncboy
 * @date 2023/3/24 15:51
 * ApiKey 响应处理
 */
@Component
public class ApiKeyResponseEmitter implements ResponseEmitter {

    @Resource
    private ChatConfig chatConfig;

    @Resource
    private ChatMessageService chatMessageService;

    @Resource
    private ChatCompletionResponseParser parser;

    @Resource
    private ApiKeyDatabaseDataStorage dataStorage;

    @Override
    public ResponseBodyEmitter requestToResponseEmitter(ChatProcessRequest chatProcessRequest, ResponseBodyEmitter emitter) {
        // 初始化聊天消息
        ChatMessageDO chatMessageDO = chatMessageService.initChatMessage(chatProcessRequest);
        // 所有消息
        LinkedList<Message> messages = new LinkedList<>();
        int systemTokens = TikTokensUtil.tokens(chatMessageDO.getModelName(), chatProcessRequest.getSystemMessage());
        
        // 添加用户上下文消息
        //1. 确定当前消息的token数，如果curToken > 4000
//        int maxTokens = ApiKeyTokenLimiterEnum.getTokenLimitByOuterJarModelName(chatMessageDO.getModelName());
        addContextChatMessage(chatMessageDO, messages, systemTokens, 1200);

        // 去掉第一条回答的消息
        if (messages.getFirst().getRole().equals(Message.Role.ASSISTANT.getName())) {
            messages.removeFirst();
        }
        // 系统消息
        Message systemMessage = Message.builder()
                .role(Message.Role.SYSTEM)
                .content(chatProcessRequest.getSystemMessage())
                .build();
        messages.addFirst(systemMessage);

        // 获取 包含上下文 的 token 数量
        int totalTokenCount = TikTokensUtil.tokens(chatMessageDO.getModelName(), messages);
        // 设置 promptTokens
        chatMessageDO.setPromptTokens(totalTokenCount);

        // TODO：(只是为校验代码准确性)检查 tokenCount 是否超出当前模型的 Token 数量限制
        String exceedModelTokenLimitMsg = exceedModelTokenLimit(chatProcessRequest, chatMessageDO.getModelName(), totalTokenCount, emitter);
        if (Objects.nonNull(exceedModelTokenLimitMsg)) {
            chatMessageDO.setStatus(ChatMessageStatusEnum.EXCEPTION_TOKEN_EXCEED_LIMIT.getCode());
            chatMessageDO.setResponseErrorData(exceedModelTokenLimitMsg);
            chatMessageService.updateById(chatMessageDO);
            return emitter;
        }
        // 构建聊天参数
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .maxTokens(ApiKeyTokenLimiterEnum.getTokenLimitByOuterJarModelName(chatConfig.getOpenaiApiModel()) - totalTokenCount - 1)
                .model(chatConfig.getOpenaiApiModel())
                // [0, 2] 越低越精准
                .temperature(0.8)
                .topP(1.0)
                // 每次生成一条
                .n(1)
                .presencePenalty(1)
                .messages(messages)
                .stream(true)
                .build();

        // 构建事件监听器
        ParsedEventSourceListener parsedEventSourceListener = new ParsedEventSourceListener.Builder()
//                .addListener(new ConsoleStreamListener())
                .addListener(new ResponseBodyEmitterStreamListener(emitter))
                .setParser(parser)
                .setDataStorage(dataStorage)
                .setOriginalRequestData(ObjectMapperUtil.toJson(chatCompletion))
                .setChatMessageDO(chatMessageDO)
                .build();

        ApiKeyChatClientBuilder.buildOpenAiStreamClient().streamChatCompletion(chatCompletion, parsedEventSourceListener);
        return emitter;
    }

    /**
     * 检查上下文消息的 Token 数是否超出模型限制
     *
     * @param chatProcessRequest 对话请求
     * @param modelName          当前使用的模型名称
     * @param tokenCount         当前上下的总 Token 数量
     * @param emitter            ResponseBodyEmitter
     */
    private String exceedModelTokenLimit(ChatProcessRequest chatProcessRequest, String modelName, int tokenCount, ResponseBodyEmitter emitter) {
        // 当前模型最大 tokens
        int maxTokens = ApiKeyTokenLimiterEnum.getTokenLimitByOuterJarModelName(modelName);

        String msg;
        // 判断 token 数量是否超过限制
        if (ApiKeyTokenLimiterEnum.exceedsLimit(modelName, tokenCount)) {
            // 获取当前 prompt 消耗的 tokens
            int currentPromptTokens = TikTokensUtil.tokens(modelName, chatProcessRequest.getPrompt());
            // 判断历史上下文是否超过限制
            int remainingTokens = tokenCount - currentPromptTokens;
            if (ApiKeyTokenLimiterEnum.exceedsLimit(modelName, remainingTokens)) {
                msg = "当前上下文字数已经达到上限，请关闭上下文或开启新的对话";
            } else {
                msg = StrUtil.format("当前上下文 Token 数量：{}，超过上限：{}，请减少字数发送或关闭上下文或开启新的对话", tokenCount, maxTokens);
            }
        }
        // 剩余的 token 太少也直返返回异常信息
        else if (maxTokens - tokenCount <= 10) {
            msg = "当前上下文字数不足以连续对话，请关闭上下文或开启新的对话";
        } else {
            return null;
        }

        try {
            ChatReplyMessageVO chatReplyMessageVO = ChatReplyMessageVO.onEmitterChainException(chatProcessRequest);
            chatReplyMessageVO.setText(msg);
            emitter.send(ObjectMapperUtil.toJson(chatReplyMessageVO));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            emitter.complete();
        }
        return msg;
    }
    
    /**
     * 添加上下文问题消息
     *
     * @param chatMessageDO 当前消息
     * @param messages      消息列表
     * @param tokens        上下文使用的token数                     
     */
    private void addContextChatMessage(ChatMessageDO chatMessageDO, LinkedList<Message> messages, int tokens, int maxTokens) {
        if (Objects.isNull(chatMessageDO)) {
            return;
        }
        
        // 父级消息id为空，表示是第一条消息，直接添加到message里
        if (Objects.isNull(chatMessageDO.getParentMessageId())) {
            int firstTokens = TikTokensUtil.tokens(chatMessageDO.getModelName(), chatMessageDO.getContent());
            if (firstTokens + tokens >= maxTokens - 1) {
                return;
            }
            messages.addFirst(Message.builder().role(Message.Role.USER)
              .content(chatMessageDO.getContent())
              .build());
            return;
        }

        // 根据消息类型去选择角色，需要添加问题和回答到上下文
        Message.Role role = (Objects.equals(chatMessageDO.getMessageType(), ChatMessageTypeEnum.ANSWER.getCode())) ?
                Message.Role.ASSISTANT : Message.Role.USER;

        // 回答不成功的情况下，不添加回答消息记录和该回答的问题消息记录
        if (Objects.equals(chatMessageDO.getMessageType(), ChatMessageTypeEnum.ANSWER.getCode())
                && !Objects.equals(chatMessageDO.getStatus(), ChatMessageStatusEnum.PART_SUCCESS.getCode())
                && !Objects.equals(chatMessageDO.getStatus(), ChatMessageStatusEnum.COMPLETE_SUCCESS.getCode())) {
            // 没有父级回答消息直接跳过
            if (Objects.isNull(chatMessageDO.getParentAnswerMessageId())) {
                return;
            }
            //TODO:添加本地缓存
            ChatMessageDO parentMessage = chatMessageService.getOne(new LambdaQueryWrapper<ChatMessageDO>()
                    .eq(ChatMessageDO::getMessageId, chatMessageDO.getParentAnswerMessageId()));
            addContextChatMessage(parentMessage, messages, tokens, maxTokens);
            return;
        }

        int curTokens = TikTokensUtil.tokens(chatMessageDO.getModelName(), chatMessageDO.getContent());
        if ((curTokens = curTokens + tokens) >= maxTokens - 1) {
            return;
        } 
        // 从下往上找并添加，越上面的数据放越前面
        messages.addFirst(Message.builder().role(role)
                .content(chatMessageDO.getContent())
                .build());
        //TODO:添加本地缓存
        ChatMessageDO parentMessage = chatMessageService.getOne(new LambdaQueryWrapper<ChatMessageDO>()
            .eq(ChatMessageDO::getMessageId, chatMessageDO.getParentMessageId()));
        addContextChatMessage(parentMessage, messages, curTokens, maxTokens);
    }
}
