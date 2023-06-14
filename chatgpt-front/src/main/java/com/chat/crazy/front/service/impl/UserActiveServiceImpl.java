package com.chat.crazy.front.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chat.crazy.base.domain.entity.UserActiveDO;
import com.chat.crazy.front.service.UserActiveService;
import com.chat.crazy.front.mapper.UserActiveMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author:
 * @Description:
 * @Date: 2023/4/14 下午5:23
 */
@Slf4j
@Service
public class UserActiveServiceImpl extends ServiceImpl<UserActiveMapper, UserActiveDO> implements UserActiveService {

    @Override
    public UserActiveDO getLastLoginInfo(Long userId) {
        return getOne(new LambdaQueryWrapper<UserActiveDO>()
                    .eq(UserActiveDO::getUserId, userId)
                    .eq(UserActiveDO::getActiveType, 0)
                    .orderBy(true, false, UserActiveDO::getActiveTime).last("limit 1"));
    }
}
