package com.chat.crazy.front.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chat.crazy.base.domain.entity.InviteCodeDO;

public interface InviteCodeService extends IService<InviteCodeDO> {

    InviteCodeDO getByInviteCode(String inviteCode);

    boolean update(InviteCodeDO inviteCodeDO);
}
