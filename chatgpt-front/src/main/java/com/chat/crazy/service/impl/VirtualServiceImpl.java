package com.chat.crazy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chat.crazy.constant.UserStatus;
import com.chat.crazy.domain.entity.VirtualUserDO;
import com.chat.crazy.domain.request.user.VirtualIdReq;
import com.chat.crazy.mapper.VirtualUserMapper;
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
            virtualUserDO.setStatus(UserStatus.VALID.getStatus());
            save(virtualUserDO);
        }
        return virId;
    }
}
