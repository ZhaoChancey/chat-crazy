package com.chat.crazy.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chat.crazy.admin.service.SensitiveWordService;
import com.chat.crazy.admin.domain.query.SensitiveWordPageQuery;
import com.chat.crazy.admin.domain.vo.SensitiveWordVO;
import com.chat.crazy.base.annotation.ApiAdminRestController;
import com.chat.crazy.base.handler.response.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;

/**
 * @author hncboy
 * @date 2023/3/28 20:59
 * 敏感词相关接口
 */
@AllArgsConstructor
@Tag(name = "管理端-敏感词相关接口")
@ApiAdminRestController("/sensitive_word")
public class SensitiveWordController {

    @Resource
    SensitiveWordService sensitiveWordService;

    @GetMapping("/page")
    @Operation(summary = "敏感词列表分页")
    public R<IPage<SensitiveWordVO>> page(@Validated SensitiveWordPageQuery sensitiveWordPageQuery) {
        return R.data(sensitiveWordService.pageSensitiveWord(sensitiveWordPageQuery));
    }
}
