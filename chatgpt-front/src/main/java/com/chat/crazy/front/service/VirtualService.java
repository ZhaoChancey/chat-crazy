package com.chat.crazy.front.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chat.crazy.base.domain.entity.VirtualUserDO;
import com.chat.crazy.front.domain.request.user.VirtualIdReq;

/**
 * @Author:
 * @Description:
 * @Date: 2023/4/14 下午5:22
 */
public interface VirtualService extends IService<VirtualUserDO> {

    /**
     * 生成虚拟id
     * @param req
     * @return
     */
    String genVirId(VirtualIdReq req);

    /**
     * 根据虚拟id查询主键
     * @param virId
     * @return
     */
    VirtualUserDO getByVirId(String virId);
}
