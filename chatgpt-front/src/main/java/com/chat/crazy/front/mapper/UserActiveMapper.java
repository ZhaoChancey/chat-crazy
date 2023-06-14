package com.chat.crazy.front.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chat.crazy.base.domain.entity.UserActiveDO;
import org.springframework.stereotype.Repository;

/**
 * @author hncboy
 * @date 2023/3/25 16:28
 * 用户登录信息访问层
 */
@Repository
public interface UserActiveMapper extends BaseMapper<UserActiveDO> {
}
