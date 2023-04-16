package com.chat.crazy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chat.crazy.domain.entity.ChatRoomDO;
import com.chat.crazy.domain.entity.VirtualUserDO;
import com.chat.crazy.domain.request.user.VirtualIdReq;

/**
 * @Author:
 * @Description:
 * @Date: 2023/4/14 下午5:22
 */
public interface VirtualService extends IService<VirtualUserDO> {

    String genVirId(VirtualIdReq req);
}
