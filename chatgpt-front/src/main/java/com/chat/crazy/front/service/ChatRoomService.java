package com.chat.crazy.front.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chat.crazy.base.domain.entity.ChatMessageDO;
import com.chat.crazy.base.domain.entity.ChatRoomDO;
import com.chat.crazy.base.domain.query.PageQuery;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/3/25 16:30
 * 聊天室相关业务接口
 */
public interface ChatRoomService extends IService<ChatRoomDO> {

//    /**
//     * 创建聊天室
//     *
//     * @param chatMessageDO 聊天记录
//     * @return 聊天室
//     */
//    ChatRoomDO createChatRoom(ChatMessageDO chatMessageDO);

    /**
     * 创建空聊天窗口
     * @param title
     * @return
     */
    ChatRoomDO createEmptySession(String title, Integer userId, Integer userType);

    /**
     * 判断这个联天窗口是否与该用户绑定
     * @param sessionId
     * @param userId
     * @param userType
     * @return
     */
    ChatRoomDO getValidSession(Long sessionId, Integer userId, Integer userType);

    /**
     * 获取用户的聊天窗口列表
     * @param query 分页
     * @return
     */
    Page<ChatRoomDO> getSessionsByUser(PageQuery query);
}
