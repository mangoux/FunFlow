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
     * 获取头像路径前缀（包含用户ID部分）
     *
     * @param userId 用户ID
     * @return 头像路径前缀，格式：avatar/用户ID/
     */
    public static String getAvatarPathPrefix(Long userId) {
        return String.format("%s/%s/", AVATAR_BASE_PATH, userId);
    }

    /**
     * 获取完整的头像文件路径（包含UUID和扩展名）
     *
     * @param userId 用户ID
     * @param extension 文件扩展名（如 jpg、png）
     * @return 完整的头像文件路径，格式：avatar/用户ID/UUID.扩展名
     */
    public static String getAvatarFileName(Long userId, String extension) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return String.format("%s%s.%s",
                getAvatarPathPrefix(userId),
                uuid,
                extension);
    }
}
