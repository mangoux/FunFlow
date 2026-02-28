package com.mango.funflow.common;

import java.util.UUID;

/**
 * OSS 常量类
 */
public class OssConstant {

    /**
     * 头像存储的基础路径
     */
    private static final String AVATAR_BASE_PATH = "avatar";

    /**
     * 生成头像文件名（使用UUID）
     *
     * @param userId 用户ID
     * @param extension 文件扩展名（如 jpg、png）
     * @return 头像文件路径，格式：avatar/用户ID/UUID.扩展名
     */
    public static String getAvatarFileName(Long userId, String extension) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return String.format("%s/%s/%s.%s",
                AVATAR_BASE_PATH,
                userId,
                uuid,
                extension);
    }
}
