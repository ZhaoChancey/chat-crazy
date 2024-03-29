package com.chat.crazy.base.exception;

import com.chat.crazy.base.handler.response.ResultCode;
import lombok.Getter;

/**
 * @author hncboy
 * @date 2023/3/23 12:49
 * 鉴权异常
 */
public class AuthException extends RuntimeException {

    @Getter
    private final ResultCode resultCode;

    public AuthException() {
        super(ResultCode.UN_AUTHORIZED.getMessage());
        this.resultCode = ResultCode.UN_AUTHORIZED;
    }
    public AuthException(String message) {
        super(message);
        this.resultCode = ResultCode.UN_AUTHORIZED;
    }

    public AuthException(ResultCode code, String message) {
        super(message);
        this.resultCode = code;
    }
}
