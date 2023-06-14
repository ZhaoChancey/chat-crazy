package com.chat.crazy.front.handler.emitter;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.chat.crazy.base.handler.SensitiveWordHandler;
import com.chat.crazy.base.service.impl.RedisService;
import com.chat.crazy.base.util.ObjectMapperUtil;
import com.chat.crazy.front.domain.request.chat.ChatProcessRequest;
import com.chat.crazy.front.domain.vo.chat.ChatReplyMessageVO;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;
import java.util.List;

import static com.chat.crazy.base.constant.RedisConstant.LIMIT_REQUEST_SUFFIX;

/**
 * @author hncboy
 * @date 2023/3/29 11:58
 * 敏感词检测
 */
public class SensitiveWordEmitterChain extends AbstractResponseEmitterChain {

    @Override
    public void doChain(ChatProcessRequest request, ResponseBodyEmitter emitter) {
        List<String> prompts = SensitiveWordHandler.checkWord(request.getPrompt());
        List<String> systemMessages = SensitiveWordHandler.checkWord(request.getSystemMessage());
        try {
            // 取上一条消息的 parentMessageId 和 conversationId，使得敏感词检测未通过时不影响上下文
            ChatReplyMessageVO chatReplyMessageVO = ChatReplyMessageVO.onEmitterChainException(request);
            if (CollectionUtil.isNotEmpty(prompts)) {
                // 请求次数-1
                RedisService redisService = SpringUtil.getBean(RedisService.class);
                String reqKey = LIMIT_REQUEST_SUFFIX + "send:" + request.getUserType() + ":" + request.getUserId();
                redisService.decrementKey(reqKey);
                chatReplyMessageVO.setText(StrUtil.format("发送失败，包含敏感词{}", prompts));
                emitter.send(ObjectMapperUtil.toJson(chatReplyMessageVO));
                emitter.complete();
                return;
            }

            if (CollectionUtil.isNotEmpty(systemMessages)) {
                if (CollectionUtil.isEmpty(prompts)) {
                    // 请求次数-1
                    RedisService redisService = SpringUtil.getBean(RedisService.class);
                    String reqKey = LIMIT_REQUEST_SUFFIX + "send:" + request.getUserType() + ":" + request.getUserId();
                    redisService.decrementKey(reqKey); 
                }
                chatReplyMessageVO.setText(StrUtil.format("发送失败，系统消息包含敏感词{}", prompts));
                emitter.send(ObjectMapperUtil.toJson(chatReplyMessageVO));
                emitter.complete();
                return;
            }
        } catch (IOException e) {
            // 请求次数-1
            RedisService redisService = SpringUtil.getBean(RedisService.class);
            String reqKey = LIMIT_REQUEST_SUFFIX + "send:" + request.getUserType() + ":" + request.getUserId();
            redisService.decrementKey(reqKey);
            // 抛出
            throw new RuntimeException(e);

        }
        if (getNext() != null) {
            getNext().doChain(request, emitter);
        }
    }
}
