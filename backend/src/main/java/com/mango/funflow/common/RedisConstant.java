package com.mango.funflow.common;

/**
 * Redis 常量类
 */
public class RedisConstant {

    /**
     * 图形验证码 Key 前缀
     */
    public static final String CAPTCHA_PREFIX = "captcha:";

    /**
     * 图形验证码过期时间（秒）
     */
    public static final long CAPTCHA_EXPIRE_SECONDS = 300; // 5分钟

    /**
     * 生成图形验证码的 Redis Key
     *
     * @param captchaId 验证码ID
     * @return Redis Key
     */
    public static String getCaptchaKey(String captchaId) {
        return CAPTCHA_PREFIX + captchaId;
    }
}
