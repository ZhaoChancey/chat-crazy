package com.chat.crazy.front.domain.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author:
 * @Description:
 * @Date: 2023/4/14 下午2:20
 */
@Data
@Schema(title = "用户信息查询回参")
public class UserInfoVo {
    @Schema(title = "userId")
    private Integer id;

    @Schema(title = "昵称")
    private String nickName;

    @Schema(title = "手机号")
    private String phone;

    @Schema(title = "身份信息")
    private UserIdentity identity;

    @Data
    public static class UserIdentity {
        Integer vipType;
        Integer packageType;
        Long startTs;
        Long endTs;
        Integer freeLastDays;
        Long vipStartTs;
        Long vipEndTs;
        Integer vipLastDays;
        Integer masterType;
//        Boolean isUsed;
    }
}
