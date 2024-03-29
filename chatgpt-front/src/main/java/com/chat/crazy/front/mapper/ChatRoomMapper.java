package com.chat.crazy.front.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chat.crazy.base.domain.entity.ChatRoomDO;
import org.springframework.stereotype.Repository;

/**
 * @author hncboy
 * @date 2023/3/25 16:29
 * 聊天室数据访问层
 */
@Repository("FrontChatRoomMapper")
public interface ChatRoomMapper extends BaseMapper<ChatRoomDO> {
}
