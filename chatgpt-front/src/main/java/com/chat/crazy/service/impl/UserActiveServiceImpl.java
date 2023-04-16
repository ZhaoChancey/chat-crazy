package com.chat.crazy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chat.crazy.domain.entity.UserActiveDO;
import com.chat.crazy.domain.entity.VirtualUserDO;
import com.chat.crazy.domain.request.user.VirtualIdReq;
import com.chat.crazy.mapper.UserActiveMapper;
import com.chat.crazy.mapper.VirtualUserMapper;
import com.chat.crazy.service.UserActiveService;
import com.chat.crazy.service.VirtualService;
import com.chat.crazy.util.Md5Util;
import com.chat.crazy.util.WebUtil;
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
