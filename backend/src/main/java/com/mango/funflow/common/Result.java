package com.mango.funflow.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一返回结果封装类
 *
 * @param <T> 返回数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 消息
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 成功返回（无数据）
     */
    public static <T> Result<T> success() {
        return new Result<>(Code.SUCCESS, "success", null);
    }

    /**
     * 成功返回（带数据）
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(Code.SUCCESS, "success", data);
    }

    /**
     * 成功返回（带消息和数据）
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(Code.SUCCESS, message, data);
    }

    /**
     * 失败返回（带错误码和消息）
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }
}
