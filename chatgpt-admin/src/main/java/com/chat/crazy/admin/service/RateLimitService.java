package com.chat.crazy.admin.service;

import com.chat.crazy.admin.domain.vo.RateLimitVO;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/4/1 04:52
 * 限流记录业务接口
 */
public interface RateLimitService {

    /**
     * 查询限流列表
     *
     * @return 限流列表
     */
    List<RateLimitVO> listRateLimit();
}
