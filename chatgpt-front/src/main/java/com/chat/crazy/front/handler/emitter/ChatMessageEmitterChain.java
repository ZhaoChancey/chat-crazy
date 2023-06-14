package com.chat.crazy.front.handler.emitter;

import cn.hutool.extra.spring.SpringUtil;
import com.chat.crazy.base.config.ChatConfig;
import com.chat.crazy.base.enums.ApiTypeEnum;
import com.chat.crazy.base.exception.ServiceException;
import com.chat.crazy.base.service.impl.RedisService;
import com.chat.crazy.front.domain.request.chat.ChatProcessRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import static com.chat.crazy.base.constant.RedisConstant.LIMIT_REQUEST_SUFFIX;

/**
 * @author hncboy
 * @date 2023/3/29 12:38
 * 正常发送消息链路，最后一个节点
 */
public class ChatMessageEmitterChain extends AbstractResponseEmitterChain {

    @Override
    public void doChain(ChatProcessRequest request, ResponseBodyEmitter emitter) {
//        ApiTypeEnum apiTypeEnum = SpringUtil.getBean(ChatConfig.class).getApiTypeEnum();
        ResponseEmitter responseEmitter = SpringUtil.getBean(ApiKeyResponseEmitter.class);
//        if (apiTypeEnum == ApiTypeEnum.API_KEY) {
//            responseEmitter = SpringUtil.getBean(ApiKeyResponseEmitter.class);
//        } else {
//            responseEmitter = SpringUtil.getBean(AccessTokenResponseEmitter.class);
//        }
        responseEmitter.requestToResponseEmitter(request, emitter);
    }
}
