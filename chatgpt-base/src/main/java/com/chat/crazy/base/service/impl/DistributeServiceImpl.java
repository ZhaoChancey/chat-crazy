package com.chat.crazy.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chat.crazy.base.config.ServerConfig;
import com.chat.crazy.base.domain.entity.DistributeDO;
import com.chat.crazy.base.domain.po.DistributedIdWorker;
import com.chat.crazy.base.mapper.DistributeMapper;
import com.chat.crazy.base.service.DistributeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author:
 * @Description:
 * @Date: 2023/6/12 下午4:40
 */
@Service
public class DistributeServiceImpl extends ServiceImpl<DistributeMapper, DistributeDO> implements DistributeService {
    @Resource
    private ServerConfig serverConfig;
    
    private DistributeDO getConfigByPort(String port) {
        return getBaseMapper().selectOne(new LambdaQueryWrapper<DistributeDO>().eq(DistributeDO::getPort, port));
    }

    @Override
    public String genDistributeId(String paymentType) {
        DistributeDO configByPort = getConfigByPort(String.valueOf(serverConfig.getPort()));
        DistributedIdWorker distributedIdWorker = new DistributedIdWorker(configByPort.getWorkerId(), configByPort.getDatacenterId());
        return paymentType + distributedIdWorker.nextId();
    }
}
