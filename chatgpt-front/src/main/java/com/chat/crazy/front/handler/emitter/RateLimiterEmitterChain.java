package com.chat.crazy.front.handler.emitter;

import cn.hutool.extra.spring.SpringUtil;
import com.chat.crazy.base.service.impl.RedisService;
import com.chat.crazy.base.util.ObjectMapperUtil;
import com.chat.crazy.front.domain.request.chat.ChatProcessRequest;
import com.chat.crazy.front.domain.vo.chat.ChatReplyMessageVO;
import lombok.AllArgsConstructor;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;

import static com.chat.crazy.base.constant.RedisConstant.*;

/**
 * @author lizhongyuan
 * Ip 限流处理
 */
@AllArgsConstructor
public class RateLimiterEmitterChain extends AbstractResponseEmitterChain {

    @Override
    public void doChain(ChatProcessRequest request, ResponseBodyEmitter emitter) {
        try {
            if (allowRequest(request.getUserId(), request.getUserType())) {
                if (getNext() != null) {
                    getNext().doChain(request, emitter);
                }
            } else {
                ChatReplyMessageVO chatReplyMessageVO = ChatReplyMessageVO.onEmitterChainException(request);
                chatReplyMessageVO.setText("当前访问次数较多，请稍后再试");
                emitter.send(ObjectMapperUtil.toJson(chatReplyMessageVO));
                emitter.complete();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 是否需要限流，25次/h
     * @param userId 用户id
     * @param userType 用户类型
     */
    private boolean allowRequest(Integer userId, Integer userType) {
        RedisService redisService = SpringUtil.getBean(RedisService.class);
        String timeKey = LIMIT_TIME_SUFFIX + "send:" + userType + ":" + userId;
        String reqKey = LIMIT_REQUEST_SUFFIX + "send:" + userType + ":" + userId;
        return redisService.limit(timeKey, reqKey, CHAT_MAX_REQ_HOUR, MAX_REQUEST_TIME_HOUR) > 0;
    }
}
