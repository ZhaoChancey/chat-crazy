package com.chat.crazy.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chat.crazy.admin.handler.converter.ChatRoomConverter;
import com.chat.crazy.admin.mapper.ChatRoomMapper;
import com.chat.crazy.admin.service.ChatRoomService;
import com.chat.crazy.admin.domain.query.ChatRoomPageQuery;
import com.chat.crazy.admin.domain.vo.ChatRoomVO;
import com.chat.crazy.base.domain.entity.ChatRoomDO;
import com.chat.crazy.base.util.PageUtil;
import org.springframework.stereotype.Service;

/**
 * @author hncboy
 * @date 2023/3/27 21:46
 * 聊天室业务实现类
 */
@Service("chatRoom")
public class ChatRoomServiceImpl extends ServiceImpl<ChatRoomMapper, ChatRoomDO> implements ChatRoomService {

    @Override
    public IPage<ChatRoomVO> pageChatRoom(ChatRoomPageQuery chatRoomPageQuery) {
        System.out.println("12345678");
        Page<ChatRoomDO> chatRoomPage = page(new Page<>(chatRoomPageQuery.getPageNum(), chatRoomPageQuery.getPageSize()), new LambdaQueryWrapper<ChatRoomDO>()
                .orderByDesc(ChatRoomDO::getId));

        return PageUtil.toPage(chatRoomPage, ChatRoomConverter.INSTANCE.entityToVO(chatRoomPage.getRecords()));
    }

    @Override
    public IPage<ChatRoomVO> pageChatRoomV2() {
        System.out.println("12345678");
        Page<ChatRoomDO> chatRoomPage = page(new Page<>(1, 1), new LambdaQueryWrapper<ChatRoomDO>()
                .orderByDesc(ChatRoomDO::getId));

        return PageUtil.toPage(chatRoomPage, ChatRoomConverter.INSTANCE.entityToVO(chatRoomPage.getRecords()));
    }
}
