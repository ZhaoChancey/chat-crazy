package com.chat.crazy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chat.crazy.domain.entity.SensitiveWordDO;
import org.springframework.stereotype.Repository;

/**
 * @author hncboy
 * @date 2023/3/28 20:47
 * 敏感词数据库访问层
 */
@Repository("CommonSensitiveWordMapper")
public interface SensitiveWordMapper extends BaseMapper<SensitiveWordDO> {

}