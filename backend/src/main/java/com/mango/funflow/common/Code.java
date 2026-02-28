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
     * 未认证
     */
    public static final int UNAUTHORIZED = 401;

    /**
     * 请求资源不存在
     */
    public static final int NOT_FOUND = 404;

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
     * 邮箱验证码校验失败
     */
    public static final int EMAIL_CODE_VALIDATION_ERROR = 40003;

    /**
     * 该邮箱已被注册
     */
    public static final int EMAIL_REGISTERED = 40004;

    /**
     * 账号密码不匹配
     */
    public static final int EMAIL_PASSWORD_ERROR = 40005;

    /**
     * 账号状态异常
     */
    public static final int USER_STATUS_ERROR = 40006;

    /**
     * 文件格式校验失败
     */
    public static final int FILE_VALIDATION_ERROR = 40007;

    /**
     * 邮件发送服务不可用（配置填写有误）
     */
    public static final int EMAIL_SEND_ERROR = 50001;
}
