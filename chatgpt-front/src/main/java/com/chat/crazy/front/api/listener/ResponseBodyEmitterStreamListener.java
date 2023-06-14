package com.chat.crazy.front.api.listener;

import cn.hutool.extra.spring.SpringUtil;
import com.chat.crazy.base.domain.entity.VirtualUserDO;
import com.chat.crazy.base.enums.UserTypeEnum;
import com.chat.crazy.base.service.impl.RedisService;
import com.chat.crazy.base.util.TokenUtil;
import com.chat.crazy.front.domain.vo.chat.ChatReplyMessageVO;
import com.chat.crazy.base.exception.ServiceException;
import com.chat.crazy.base.util.ObjectMapperUtil;
import com.chat.crazy.front.service.VirtualService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.Objects;

import static com.chat.crazy.base.constant.RedisConstant.LIMIT_REQUEST_SUFFIX;

/**
 * @author hncboy
 * @date 2023/3/24 17:19
 * ResponseBodyEmitter 消息流监听
 */
@Slf4j
@AllArgsConstructor
public class ResponseBodyEmitterStreamListener extends AbstractStreamListener {

    private final ResponseBodyEmitter emitter;

    @Override
    public void onMessage(String newMessage, String receivedMessage, ChatReplyMessageVO chatReplyMessageVO, int messageCount) {
        if (Objects.isNull(chatReplyMessageVO)) {
            return;
        }

        try {
            emitter.send((messageCount != 1 ? "\n" : "") + ObjectMapperUtil.toJson(chatReplyMessageVO));
        } catch (Exception e) {
            log.warn("消息发送异常，第{}条消息，消息内容：{}", messageCount, receivedMessage, e);
            throw new ServiceException("消息发送异常");
        }
    }

    @Override
    public void onComplete(String receivedMessage) {
        try {
            emitter.send("[DONE]");
        } catch (Exception e) {
            log.warn("结束标志位消息发送异常：{}", ExceptionUtils.getStackTrace(e));
            throw new ServiceException("消息发送异常");
        }
        emitter.complete();
    }

    @Override
    public void onError(String receivedMessage, Throwable t, @Nullable Response response) {
        try {
            ChatReplyMessageVO chatReplyMessageVO = new ChatReplyMessageVO();
            chatReplyMessageVO.setText(receivedMessage.concat("\n【接收消息处理异常，响应中断】"));
            emitter.send(ObjectMapperUtil.toJson(chatReplyMessageVO));
        } catch (Exception e) {
            log.warn("消息发送异常，处理异常发送消息时出错", e);
        } finally {
            try {
                emitter.complete();
                // 请求次数 - 1
                RedisService redisService = SpringUtil.getBean(RedisService.class);
                Integer userId = TokenUtil.getUserId();
                int userType = UserTypeEnum.COMMON.getType();
                if (userId == -1) {
                    // 未登录用户
                    VirtualService virtualService = SpringUtil.getBean(VirtualService.class);
                    VirtualUserDO virUser = virtualService.getByVirId(TokenUtil.getVirId());
                    userId = virUser.getId();
                    userType = UserTypeEnum.NOT_LOGIN.getType();
                }
                
                String reqKey = LIMIT_REQUEST_SUFFIX + "send:" + userType + ":" + userId;
                redisService.decrementKey(reqKey);
            } catch (Exception ignored) {

            }
        }
    }
}
