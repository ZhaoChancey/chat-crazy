package com.chat.crazy.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chat.crazy.admin.domain.query.SensitiveWordPageQuery;
import com.chat.crazy.admin.domain.vo.SensitiveWordVO;
import com.chat.crazy.base.domain.entity.SensitiveWordDO;

/**
 * @author hncboy
 * @date 2023/3/28 21:07
 * 敏感词业务接口
 */
public interface SensitiveWordService extends IService<SensitiveWordDO> {

    /**
     * 敏感词分页查询
     *
     * @param sensitiveWordPageQuery 查询条件
     * @return 敏感词分页列表
     */
    IPage<SensitiveWordVO> pageSensitiveWord(SensitiveWordPageQuery sensitiveWordPageQuery);
}
