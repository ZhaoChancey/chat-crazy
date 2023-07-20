package com.chat.crazy.base.handler.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.servlet.http.HttpServletResponse;

/**
 * @author hncboy
 * @date 2023/3/23 12:34
 * 结果状态码
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    /**
     * 操作成功
     */
    SUCCESS(HttpServletResponse.SC_OK, "操作成功"),

    /**
     * 业务异常
     */
    FAILURE(HttpServletResponse.SC_BAD_REQUEST, "业务异常"),

    /**
     * 请求未授权
     */
    UN_AUTHORIZED(HttpServletResponse.SC_UNAUTHORIZED, "请求未授权"),

    /**
     * 服务器异常
     */
    INTERNAL_SERVER_ERROR(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "服务器异常"),
    ALI_TRADE_PRE_CREATE_ERROR(10004, "支付宝预下单接口异常"),
    ALI_TRADE_ORDER_QUERY_ERROR(10005, "支付宝订单查询接口异常"),
    ALI_TRADE_ORDER_REPEAT_ERROR(10006, "请不要重复下单"),

    USER_INVITE_CODE_ADD_ERROR(20001, "邀请码生成失败，请稍后重试"),
    USER_INVITE_CODE_PERM_ERROR(20002, "没有生成邀请码的权限"),
    ;

    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;
}
