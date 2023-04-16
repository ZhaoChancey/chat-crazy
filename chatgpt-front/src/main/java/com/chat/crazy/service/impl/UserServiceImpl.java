package com.chat.crazy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chat.crazy.domain.entity.UserActiveDO;
import com.chat.crazy.domain.entity.UserDO;
import com.chat.crazy.mapper.UserActiveMapper;
import com.chat.crazy.mapper.UserMapper;
import com.chat.crazy.service.UserActiveService;
import com.chat.crazy.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author:
 * @Description:
 * @Date: 2023/4/14 下午5:23
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

}
