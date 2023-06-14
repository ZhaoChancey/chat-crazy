package com.chat.crazy.front.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chat.crazy.base.domain.entity.UserActiveDO;

/**
 * @Author:
 * @Description:
 * @Date: 2023/4/14 下午5:22
 */
public interface UserActiveService extends IService<UserActiveDO> {

    /**
     * 获取用户最后一次登录时间
     * @param userId
     * @return
     */
    UserActiveDO getLastLoginInfo(Long userId);
}
