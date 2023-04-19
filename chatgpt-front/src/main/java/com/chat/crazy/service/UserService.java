package com.chat.crazy.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chat.crazy.domain.entity.UserDO;
import com.chat.crazy.domain.request.user.UserInfoRes;

/**
 * @Author:
 * @Description:
 * @Date: 2023/4/14 下午5:22
 */
public interface UserService extends IService<UserDO> {

    UserInfoRes getUserInfo(String token);
}
