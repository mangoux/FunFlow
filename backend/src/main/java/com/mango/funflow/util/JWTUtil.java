package com.mango.funflow.util;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSignerUtil;

import java.util.Date;

/**
 * JWT 工具类
 * 用于生成、验证和解析 JWT 令牌
 */
public class JWTUtil {

    /**
     * JWT 密钥（从配置中读取，通过 init 方法初始化）
     */
    private static byte[] SECRET_KEY;

    /**
     * JWT 过期时间（毫秒）
     */
    private static long EXPIRE_TIME;

    /**
     * 负载中的 userId 键名
     */
    private static final String CLAIM_USER_ID = "userId";

    /**
     * 初始化 JWT 配置
     * 由 JWTConfig 类在应用启动时调用
     *
     * @param secretKey  密钥
     * @param expireTime 过期时间（毫秒）
     */
    public static void init(String secretKey, long expireTime) {
        SECRET_KEY = secretKey.getBytes();
        EXPIRE_TIME = expireTime;
    }

    /**
     * 生成 JWT 令牌
     *
     * @param userId 用户ID
     * @return JWT 令牌字符串
     */
    public static String generateToken(Long userId) {
        long now = System.currentTimeMillis();
        return JWT.create()
                .setPayload(CLAIM_USER_ID, userId)
                .setIssuedAt(new Date(now))
                .setExpiresAt(new Date(now + EXPIRE_TIME))
                .sign(JWTSignerUtil.hs256(SECRET_KEY));
    }

    /**
     * 判断令牌是否有效
     * 验证签名和过期时间
     *
     * @param token JWT 令牌
     * @return true-有效，false-无效
     */
    public static boolean isValid(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        try {
            // 验证签名
            JWT jwt = JWT.of(token);
            if (!jwt.verify(JWTSignerUtil.hs256(SECRET_KEY))) {
                return false;
            }
            // 验证是否过期
            JWTValidator.of(jwt).validateDate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从令牌中获取 userId
     * 调用前应先使用 isValid 方法验证令牌有效性
     *
     * @param token JWT 令牌
     * @return 用户ID，如果解析失败返回 null
     */
    public static Long getUserId(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        try {
            JWT jwt = JWT.of(token);
            Object userId = jwt.getPayload(CLAIM_USER_ID);
            if (userId == null) {
                return null;
            }
            return Long.parseLong(userId.toString());
        } catch (Exception e) {
            return null;
        }
    }
}
