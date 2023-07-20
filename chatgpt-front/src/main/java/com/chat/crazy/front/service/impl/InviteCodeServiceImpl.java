package com.chat.crazy.front.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chat.crazy.base.domain.entity.InviteCodeDO;
import com.chat.crazy.front.mapper.InviteCodeMapper;
import com.chat.crazy.front.service.InviteCodeService;
import org.springframework.stereotype.Service;

@Service
public class InviteCodeServiceImpl extends ServiceImpl<InviteCodeMapper, InviteCodeDO> implements InviteCodeService {

    @Override
    public InviteCodeDO getByInviteCode(String inviteCode) {
        return getBaseMapper().selectOne(new LambdaQueryWrapper<InviteCodeDO>().eq(InviteCodeDO::getInviteCode, inviteCode));
    }

    @Override
    public boolean update(InviteCodeDO inviteCodeDO) {
        return getBaseMapper().updateById(inviteCodeDO) > 0;
    }
}
