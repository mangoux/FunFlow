package com.mango.funflow.common;

import java.util.UUID;

/**
 * OSS 常量类
 */
public class OssConstant {

    private static final String AVATAR_BASE_PATH = "avatar";
    private static final String VIDEO_BASE_PATH = "video";
    private static final String COVER_BASE_PATH = "cover";

    /**
     * 获取头像路径前缀，格式：avatar/用户ID/
     *
     * @param userId 用户 ID
     * @return 头像路径前缀
     */
    public static String getAvatarPathPrefix(Long userId) {
        return String.format("%s/%s/", AVATAR_BASE_PATH, userId);
    }

    /**
     * 获取完整的头像文件路径，格式：avatar/用户ID/UUID.扩展名
     *
     * @param userId    用户 ID
     * @param extension 文件扩展名（如 jpg、png，不含点）
     * @return 完整的头像文件路径
     */
    public static String getAvatarFileName(Long userId, String extension) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return String.format("%s%s.%s", getAvatarPathPrefix(userId), uuid, extension);
    }

    /**
     * 获取视频路径前缀，格式：video/用户ID/
     *
     * @param userId 用户 ID
     * @return 视频路径前缀
     */
    public static String getVideoPathPrefix(Long userId) {
        return String.format("%s/%s/", VIDEO_BASE_PATH, userId);
    }

    /**
     * 获取完整的视频文件路径，格式：video/用户ID/UUID.扩展名
     *
     * @param userId    用户 ID
     * @param extension 文件扩展名（如 mp4、mov，不含点）
     * @return 完整的视频文件路径
     */
    public static String getVideoFileName(Long userId, String extension) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return String.format("%s%s.%s", getVideoPathPrefix(userId), uuid, extension);
    }

    /**
     * 获取封面路径前缀，格式：cover/用户ID/
     *
     * @param userId 用户 ID
     * @return 封面路径前缀
     */
    public static String getCoverPathPrefix(Long userId) {
        return String.format("%s/%s/", COVER_BASE_PATH, userId);
    }

    /**
     * 获取完整的封面文件路径，格式：cover/用户ID/UUID.扩展名
     *
     * @param userId    用户 ID
     * @param extension 文件扩展名（如 jpg、png，不含点）
     * @return 完整的封面文件路径
     */
    public static String getCoverFileName(Long userId, String extension) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return String.format("%s%s.%s", getCoverPathPrefix(userId), uuid, extension);
    }
}
