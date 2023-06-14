package com.chat.crazy.base.exception;

import com.chat.crazy.base.handler.response.R;
import com.chat.crazy.base.handler.response.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author hncboy
 * @date 2023/3/23 10:55
 * 异常处理器
 */
@Slf4j
@Configuration
@RestControllerAdvice
public class RestExceptionTranslator {

    /**
     * 管理端登录异常处理
     * HTTP 状态为 401
     *
     * @param e 异常信息
     * @return 返回值
     */
    @ExceptionHandler(NotLoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public R<Void> handleError(NotLoginException e) {
        log.error("管理端登录异常: {}", ExceptionUtils.getStackTrace(e));
        return R.fail(ResultCode.UN_AUTHORIZED, e.getMessage());
    }

    /**
     * 业务异常处理
     * HTTP 状态为 200
     *
     * @param e 异常信息
     * @return 返回值
     */
    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.OK)
    public R<Void> handleError(ServiceException e) {
        log.error("业务异常: {}", ExceptionUtils.getStackTrace(e));
        return R.fail(e.getResultCode(), e.getMessage());
    }

    /**
     * 鉴权异常处理
     * HTTP 状态为 200
     *
     * @param e 异常信息
     * @return 返回值
     */
    @ExceptionHandler(AuthException.class)
    @ResponseStatus(HttpStatus.OK)
    public R<Void> handleError(AuthException e) {
        log.error("鉴权异常: {}", ExceptionUtils.getStackTrace(e));
        return R.fail(e.getResultCode(), e.getMessage());
    }

    /**
     * 其他异常处理
     * HTTP 状态为 200
     *
     * @param e 异常信息
     * @return 返回值
     */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.OK)
    public R<Void> handleError(Throwable e) {
        log.error("服务器异常: {}", ExceptionUtils.getStackTrace(e));
        return R.fail(ResultCode.INTERNAL_SERVER_ERROR, ResultCode.INTERNAL_SERVER_ERROR.getMessage());
    }
}
