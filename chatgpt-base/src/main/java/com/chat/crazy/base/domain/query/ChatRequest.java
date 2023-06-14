package com.chat.crazy.base.domain.query;

import com.chat.crazy.base.domain.entity.UserDO;
import com.chat.crazy.base.domain.entity.VirtualUserDO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/** 全局请求。
 *  WARNING! 当header里必须传virId的接口里才继承该类，否则virtualUser可能会查询不到报错
 * @Author:
 * @Description:
 * @Date: 2023/4/24 下午5:40
 */
@Data
public class ChatRequest {
    UserDO user;
    
    VirtualUserDO virtualUser;
    
    Integer userId;
    
    Integer userType;
}
