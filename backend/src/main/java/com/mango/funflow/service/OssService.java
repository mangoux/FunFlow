package com.mango.funflow.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * OSS 文件上传服务接口
 * 提供通用的文件上传功能
 */
public interface OssService {

    /**
     * 上传文件到 OSS
     *
     * @param file     文件
     * @param fileName 文件在 OSS 中的完整路径（包含文件名），如 "avatar/10001/uuid.jpg"
     * @return 文件访问 URL
     */
    String uploadFile(MultipartFile file, String fileName);
}
