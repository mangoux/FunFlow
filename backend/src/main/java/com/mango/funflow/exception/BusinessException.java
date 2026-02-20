package com.mango.funflow.exception;

import com.mango.funflow.common.Code;

/**
 * 业务异常类
 * 用于业务逻辑中的异常情况
 */
public class BusinessException extends BaseException {
    
    /**
     * 默认业务异常（使用默认错误码）
     *
     * @param message 错误消息
     */
    public BusinessException(String message) {
        super(Code.ERROR, message);
    }
    
    /**
     * 自定义错误码的业务异常
     *
     * @param code    错误码
     * @param message 错误消息
     */
    public BusinessException(Integer code, String message) {
        super(code, message);
    }
    
    /**
     * 带原因的业务异常
     *
     * @param code    错误码
     * @param message 错误消息
     * @param cause   原始异常
     */
    public BusinessException(Integer code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
