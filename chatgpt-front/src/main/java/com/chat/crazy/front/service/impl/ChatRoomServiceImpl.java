package com.chat.crazy.front.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chat.crazy.base.domain.entity.ChatMessageDO;
import com.chat.crazy.base.domain.entity.ChatRoomDO;
import com.chat.crazy.base.domain.query.PageQuery;
import com.chat.crazy.base.enums.ChatRoomStatusEnum;
import com.chat.crazy.front.service.ChatRoomService;
import com.chat.crazy.base.util.WebUtil;
import com.chat.crazy.front.mapper.ChatRoomMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author hncboy
 * @date 2023/3/25 16:31
 * 聊天室相关业务实现类
 */
@Service
public class ChatRoomServiceImpl extends ServiceImpl<ChatRoomMapper, ChatRoomDO> implements ChatRoomService {

//    @Override
//    public ChatRoomDO createChatRoom(ChatMessageDO chatMessageDO) {
//        ChatRoomDO chatRoom = new ChatRoomDO();
//        chatRoom.setId(IdWorker.getId());
////        chatRoom.setApiType(chatMessageDO.getApiType());
//        chatRoom.setIp(WebUtil.getIp());
//        chatRoom.setFirstChatMessageId(chatMessageDO.getId());
//        chatRoom.setFirstMessageId(chatMessageDO.getMessageId());
//        // 取一部分内容当标题
//        chatRoom.setTitle(StrUtil.sub(chatMessageDO.getContent(), 0, 50));
//        chatRoom.setCreateTime(LocalDateTime.now());
//        chatRoom.setUpdateTime(chatRoom.getCreateTime());
//        save(chatRoom);
//        return chatRoom;
//    }

    @Override
    public ChatRoomDO createEmptySession(String title, Integer userId, Integer userType) {
        String newTitle = StringUtils.isEmpty(title) ? "New Chat" : title;
        ChatRoomDO chatRoom = new ChatRoomDO();
        chatRoom.setId(IdWorker.getId());
        chatRoom.setIp(WebUtil.getIp());
        chatRoom.setTitle(StrUtil.sub(newTitle, 0, 50));
        chatRoom.setUserId(userId);
        chatRoom.setUserType(userType);
        chatRoom.setCreateTime(LocalDateTime.now());
        chatRoom.setUpdateTime(chatRoom.getCreateTime());
        chatRoom.setStatus(ChatRoomStatusEnum.VALID.getStatus());
        save(chatRoom);
        return chatRoom;
    }

    @Override
    public ChatRoomDO getValidSession(Long sessionId, Integer userId, Integer userType) {
        return getBaseMapper().selectOne(new LambdaQueryWrapper<ChatRoomDO>().eq(ChatRoomDO::getId, sessionId)
                    .eq(ChatRoomDO::getUserId, userId).eq(ChatRoomDO::getUserType, userType)
                        .eq(ChatRoomDO::getStatus, ChatRoomStatusEnum.VALID.getStatus()));
    }

    @Override
    public Page<ChatRoomDO> getSessionsByUser(PageQuery query) {
        Integer userId = query.getUserId();
        Integer userType = query.getUserType();
        Page<ChatRoomDO> page = new Page<>(query.getPageNum(), query.getPageSize());
        return getBaseMapper().selectPage(page, new LambdaQueryWrapper<ChatRoomDO>().eq(ChatRoomDO::getUserId, userId)
                .eq(ChatRoomDO::getUserType, userType).eq(ChatRoomDO::getStatus, ChatRoomStatusEnum.VALID.getStatus())
                .orderByDesc(ChatRoomDO::getId));
    }
}
