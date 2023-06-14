package com.chat.crazy.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chat.crazy.base.domain.entity.ChatRoomDO;
import org.springframework.stereotype.Repository;

/**
 * @author hncboy
 * @date 2023/3/25 21:45
 * 聊天室数据访问层
 */
@Repository
public interface ChatRoomMapper extends BaseMapper<ChatRoomDO> {
}
