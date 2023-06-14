package com.chat.crazy.front.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chat.crazy.base.domain.entity.UserDO;
import com.chat.crazy.base.domain.query.ChatRequest;
import com.chat.crazy.front.domain.request.chat.ChatProcessRequest;
import com.chat.crazy.front.domain.vo.user.UserInfoVo;

/**
 * @Author:
 * @Description:
 * @Date: 2023/4/14 下午5:22
 */
public interface UserService extends IService<UserDO> {

    /**
     * 获取用户信息
     * @param request
     * @return
     */
    UserInfoVo getUserInfo(ChatRequest request);

    /**
     * 校验用户是否具有对话权限
     * @param request
     * @return
     */
    boolean verifySendMessage(ChatProcessRequest request);
}
