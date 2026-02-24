package com.mango.funflow.config;

import com.mango.funflow.util.JWTUtil;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT 配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JWTConfig {

    /**
     * JWT 密钥
     */
    private String secretKey = "funflow-default-secret-key-please-change-in-production";

    /**
     * JWT 过期时间（毫秒），默认 7 天
     */
    private long expireTime = 7 * 24 * 60 * 60 * 1000L;

    /**
     * 初始化 JWTUtil
     */
    @PostConstruct
    public void init() {
        JWTUtil.init(secretKey, expireTime);
    }
}
