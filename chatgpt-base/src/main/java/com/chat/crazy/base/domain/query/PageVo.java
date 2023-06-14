package com.chat.crazy.base.domain.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author:
 * @Description:
 * @Date: 2023/4/23 下午7:13
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PageVo<T> {
    @Schema(title = "总条数")
    Long total;
    
    @Schema(title = "具体数据")
    List<T> list;

    @Schema(title = "每页多少条")
    private Integer pageSize;

    @Schema(title = "第几页")
    private Integer pageNum;
}
