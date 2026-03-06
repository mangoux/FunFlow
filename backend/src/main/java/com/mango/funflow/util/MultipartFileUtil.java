package com.mango.funflow.util;

import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 文件操作工具类
 */
public class MultipartFileUtil {

    private static final Set<String> SUPPORTED_IMAGE_EXTENSIONS = new HashSet<>(
            Arrays.asList("jpg", "jpeg", "png")
    );

    private static final Set<String> SUPPORTED_VIDEO_EXTENSIONS = new HashSet<>(
            Arrays.asList("mp4", "mov", "avi")
    );

    /**
     * 校验图片格式并返回文件扩展名
     *
     * @param file    要校验的图片文件
     * @param maxSize 最大允许的文件大小（字节）
     * @return 文件扩展名（小写，不包含点）
     * @throws IllegalArgumentException 当文件为空、格式不支持或超过大小时抛出
     */
    public static String validateImageAndGetExtension(MultipartFile file, long maxSize) {
        return validateAndGetExtension(file, maxSize, SUPPORTED_IMAGE_EXTENSIONS,
                "图片文件不能为空", "不支持的图片格式，仅支持：jpg、jpeg、png");
    }

    /**
     * 校验视频格式并返回文件扩展名
     *
     * @param file    要校验的视频文件
     * @param maxSize 最大允许的文件大小（字节）
     * @return 文件扩展名（小写，不包含点）
     * @throws IllegalArgumentException 当文件为空、格式不支持或超过大小时抛出
     */
    public static String validateVideoAndGetExtension(MultipartFile file, long maxSize) {
        return validateAndGetExtension(file, maxSize, SUPPORTED_VIDEO_EXTENSIONS,
                "视频文件不能为空", "不支持的视频格式，仅支持：mp4、mov、avi");
    }

    /**
     * 校验文件并返回扩展名的通用实现
     *
     * @param file               要校验的文件
     * @param maxSize            最大允许的文件大小（字节）
     * @param supportedExtensions 允许的扩展名集合
     * @param emptyFileMessage   文件为空时的错误提示
     * @param formatErrorMessage 格式不支持时的错误提示
     * @return 文件扩展名（小写，不包含点）
     */
    private static String validateAndGetExtension(MultipartFile file, long maxSize,
                                                   Set<String> supportedExtensions,
                                                   String emptyFileMessage,
                                                   String formatErrorMessage) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(emptyFileMessage);
        }
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("文件大小不能超过 " + formatFileSize(maxSize));
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        String extension = getFileExtension(originalFilename);
        if (extension == null) {
            throw new IllegalArgumentException("无法获取文件扩展名");
        }
        if (!supportedExtensions.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException(formatErrorMessage);
        }
        return extension.toLowerCase();
    }

    /**
     * 根据文件名获取扩展名
     *
     * @param fileName 文件名
     * @return 文件扩展名（不包含点），如果没有扩展名返回null
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }

        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return null;
        }

        return fileName.substring(lastDotIndex + 1);
    }

    /**
     * 格式化文件大小
     *
     * @param size 文件大小（字节）
     * @return 格式化的文件大小字符串
     */
    public static String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024.0 * 1024.0));
        }
    }
}
