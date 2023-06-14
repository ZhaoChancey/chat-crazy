package com.chat.crazy.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chat.crazy.base.domain.entity.DistributeDo;

public interface DistributeService extends IService<DistributeDo> {
    
    String genDistributeId(String paymentType);
}
