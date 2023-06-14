package com.chat.crazy.base.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 聊天窗口操作类型 
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum SessionOperateTypeEnum {
    CREATE (0, "创建"),
    EDIT_TITLE  (1, "编辑窗口标题"),
    DELETE  (2, "删除"),
    ;

    private int status;
    private String name;
}
