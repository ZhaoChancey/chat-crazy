package com.chat.crazy.front.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chat.crazy.base.domain.entity.UserDO;
import org.springframework.stereotype.Repository;

/**
 * @author hncboy
 * @date 2023/3/25 16:28
 * 虚用户访问层
 */
@Repository
public interface UserMapper extends BaseMapper<UserDO> {
}
