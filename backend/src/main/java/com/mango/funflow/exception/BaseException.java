package com.mango.funflow.exception;

import lombok.Getter;

/**
 * 基础业务异常类
 * 所有自定义异常的基类
 */
@Getter
public class BaseException extends RuntimeException {

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误消息
     */
    private final String message;

    public BaseException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BaseException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }
}
