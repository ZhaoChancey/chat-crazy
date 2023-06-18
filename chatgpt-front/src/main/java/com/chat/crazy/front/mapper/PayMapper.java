package com.chat.crazy.front.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chat.crazy.base.domain.entity.OrderDO;
import org.springframework.stereotype.Repository;

@Repository
public interface PayMapper extends BaseMapper<OrderDO> {
}
