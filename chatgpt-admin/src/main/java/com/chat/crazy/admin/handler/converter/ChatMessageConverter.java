package com.chat.crazy.admin.handler.converter;

import cn.hutool.core.util.DesensitizedUtil;
import com.chat.crazy.admin.domain.vo.ChatMessageVO;
import com.chat.crazy.base.domain.entity.ChatMessageDO;
import com.chat.crazy.base.enums.ChatMessageTypeEnum;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Objects;

/**
 * @author hncboy
 * @date 2023/3/28 12:39
 * 聊天记录相关转换
 */
@Mapper
public interface ChatMessageConverter {

    ChatMessageConverter INSTANCE = Mappers.getMapper(ChatMessageConverter.class);

    List<ChatMessageVO> entityToVO(List<ChatMessageDO> chatMessageDOList);

    @AfterMapping
    default void afterEntityToVO(ChatMessageDO chatMessageDO, @MappingTarget ChatMessageVO chatMessageVO) {
        if (Objects.equals(chatMessageDO.getMessageType(), ChatMessageTypeEnum.ANSWER.getCode())) {
            chatMessageVO.setContent("回了一条消息");
        } else if (Objects.equals(chatMessageDO.getMessageType(), ChatMessageTypeEnum.QUESTION.getCode())) {
            chatMessageVO.setContent("问了一条消息");
        }
        chatMessageVO.setIp(DesensitizedUtil.ipv4(chatMessageDO.getIp()));
    }
}
