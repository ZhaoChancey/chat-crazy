package com.chat.crazy.front.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chat.crazy.base.domain.entity.VirtualUserDO;
import com.chat.crazy.front.service.VirtualService;
import com.chat.crazy.base.util.Md5Util;
import com.chat.crazy.base.util.WebUtil;
import com.chat.crazy.base.enums.UserStatusEnum;
import com.chat.crazy.front.domain.request.user.VirtualIdReq;
import com.chat.crazy.front.mapper.VirtualUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author:
 * @Description:
 * @Date: 2023/4/14 下午5:23
 */
@Slf4j
@Service
public class VirtualServiceImpl extends ServiceImpl<VirtualUserMapper, VirtualUserDO> implements VirtualService {

    @Override
    public String genVirId(VirtualIdReq req) {
        String ip = WebUtil.getIp();
        log.info("device finger: {}, ip: {}", req.getMixSalt(), ip);
        String virId = Md5Util.getMD5(req.getMixSalt() + ip);
        VirtualUserDO userDO = getOne(new LambdaQueryWrapper<VirtualUserDO>().eq(VirtualUserDO::getVirId, virId));
        if (userDO == null) {
            // 保存到数据库
            VirtualUserDO virtualUserDO = new VirtualUserDO();
            virtualUserDO.setVirId(virId);
            virtualUserDO.setStatus(UserStatusEnum.VALID.getStatus());
            save(virtualUserDO);
        }
        return virId;
    }

    @Override
    public VirtualUserDO getByVirId(String virId) {
        return getOne(new LambdaQueryWrapper<VirtualUserDO>().eq(VirtualUserDO::getVirId, virId));
    }
}
