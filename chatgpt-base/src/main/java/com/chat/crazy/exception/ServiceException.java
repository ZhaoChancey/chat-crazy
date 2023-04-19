package com.chat.crazy.exception;

import com.chat.crazy.handler.response.ResultCode;
import lombok.Getter;

/**
 * @author hncboy
 * @date 2023/3/23 00:28
 * 业务异常
 */
public class ServiceException extends RuntimeException {

    @Getter
    private final ResultCode resultCode;

    public ServiceException(String message) {
        super(message);
        this.resultCode = ResultCode.FAILURE;
    }
}
