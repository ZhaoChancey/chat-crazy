package com.chat.crazy.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chat.crazy.base.config.ServerConfig;
import com.chat.crazy.base.domain.entity.DistributeDo;
import com.chat.crazy.base.domain.po.DistributedIdWorker;
import com.chat.crazy.base.mapper.DistributeMapper;
import com.chat.crazy.base.service.DistributeService;

import javax.annotation.Resource;

/**
 * @Author:
 * @Description:
 * @Date: 2023/6/12 下午4:40
 */
public class DistributeServiceImpl extends ServiceImpl<DistributeMapper, DistributeDo> implements DistributeService {
    @Resource
    private ServerConfig serverConfig;
    
    private DistributeDo getConfigByPort(String port) {
        return getBaseMapper().selectOne(new LambdaQueryWrapper<DistributeDo>().eq(DistributeDo::getPort, port));
    }

    @Override
    public String genDistributeId(String paymentType) {
        DistributeDo configByPort = getConfigByPort(String.valueOf(serverConfig.getPort()));
        DistributedIdWorker distributedIdWorker = new DistributedIdWorker(configByPort.getWorkerId(), configByPort.getDatacenterId());
        return paymentType + distributedIdWorker.nextId();
    }
}
