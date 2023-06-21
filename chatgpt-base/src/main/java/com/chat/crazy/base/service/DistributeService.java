package com.chat.crazy.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chat.crazy.base.domain.entity.DistributeDO;

public interface DistributeService extends IService<DistributeDO> {
    
    String genDistributeId(String paymentType);
}
