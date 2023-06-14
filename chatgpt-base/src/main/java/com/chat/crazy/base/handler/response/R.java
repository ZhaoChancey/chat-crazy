package com.chat.crazy.base.handler.response;

import com.chat.crazy.base.constant.ApplicationConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;

/**
 * @author hncboy
 * @date 2023/3/22 20:16
 * 响应结果
 */
@Data
@Schema(title = "响应结果")
public class R<T> {

    @Schema(title = "状态码")
    private int code;

    @Schema(title = "承载数据")
    private T data;

    @Schema(title = "返回消息")
    private String message;
    
    @Schema(title = "时间戳")
    private long timestamp;
    
    @Schema(title = "traceId")
    private String traceId;
    
    private R(ResultCode resultCode, String message) {
        this(resultCode, null, message);
    }

    private R(ResultCode resultCode, T data) {
        this(resultCode, data, resultCode.getMessage());
    }

    private R(ResultCode resultCode, T data, String msg) {
        this(resultCode.getCode(), data, msg);
    }

    private R(int code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.message = msg;
        this.timestamp = System.currentTimeMillis();
        this.traceId = MDC.get(ApplicationConstant.TRACE_ID);
    }

    /**
     * 返回 R
     *
     * @param data 数据
     * @param <T>  T 泛型标记
     * @return R
     */
    public static <T> R<T> data(T data) {
        return data(data, HttpStatus.OK.getReasonPhrase());
    }

    /**
     * 返回 R
     *
     * @param data 数据
     * @param msg  消息
     * @param <T>  T 泛型标记
     * @return R
     */
    public static <T> R<T> data(T data, String msg) {
        return data(HttpServletResponse.SC_OK, data, msg);
    }

    /**
     * 返回 R
     *
     * @param code 状态码
     * @param data 数据
     * @param msg  消息
     * @param <T>  T 泛型标记
     * @return R
     */
    public static <T> R<T> data(int code, T data, String msg) {
        return new R<>(code, data, msg);
    }

    /**
     * 返回 R
     *
     * @param data 消息
     * @param <T> T 泛型标记
     * @return R
     */
    public static <T> R<T> success(T data) {
        return data(data, ResultCode.SUCCESS.getMessage());
    }

    /**
     * 返回 R
     *
     * @param data 消息
     * @param <T> T 泛型标记
     * @return R
     */
    public static <T> R<T> success(T data, String message) {
        return data(data, message);
    }

    /**
     * 返回 R
     *
     * @param msg 消息
     * @param <T> T 泛型标记
     * @return R
     */
    public static <T> R<T> fail(ResultCode resultCode, T data, String msg) {
        return data(resultCode.getCode(), data, msg);
    }
    
    /**
     * 返回 R
     *
     * @param msg 消息
     * @param <T> T 泛型标记
     * @return R
     */
    public static <T> R<T> fail(String msg) {
        return data(HttpServletResponse.SC_BAD_REQUEST, null, msg);
    }

    /**
     * 返回 R
     *
     * @param resultCode 状态码
     * @param msg        消息
     * @param <T>        T 泛型标记
     * @return R
     */
    public static <T> R<T> fail(ResultCode resultCode, String msg) {
        return new R<>(resultCode, msg);
    }

    /**
     * 返回 R
     *
     * @param data       数据   
     * @param msg        消息
     * @param <T>        T 泛型标记
     * @return R
     */
    public static <T> R<T> failInternal(T data, String msg) {
        return new R<>(ResultCode.INTERNAL_SERVER_ERROR, data, msg);
    }
}
