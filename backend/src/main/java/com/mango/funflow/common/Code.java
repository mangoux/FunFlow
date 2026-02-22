package com.mango.funflow.common;

/**
 * 返回状态码定义类
 * 400 开头为客户端错误，500 开头为服务端错误
 */
public class Code {

    /**
     * 默认请求成功
     */
    public static final int SUCCESS = 200;

    /**
     * 默认客户端错误
     */
    public static final int ERROR = 400;

    /**
     * 默认服务端错误
     */
    public static final int SYSTEM_ERROR = 500;

    /**
     * 请求参数格式错误
     */
    public static final int VALIDATION_ERROR = 40001;

    /**
     * 图形验证码校验失败
     */
    public static final int CAPTCHA_VALIDATION_ERROR = 40002;

    /**
     * 邮件发送服务不可用（配置填写有误）
     */
    public static final int EMAIL_SEND_ERROR = 50001;
}
