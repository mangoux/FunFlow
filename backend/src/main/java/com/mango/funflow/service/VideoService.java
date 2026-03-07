package com.mango.funflow.service;

import com.mango.funflow.dto.response.UserVideoListResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * 视频服务接口
 */
public interface VideoService {

    /**
     * 创建视频作品
     *
     * @param videoFile 视频文件（必填，mp4/mov/avi，≤300MB）
     * @param coverFile 封面图片文件（可选，jpg/jpeg/png，≤5MB）
     * @param title     视频标题，0～300 字符
     * @param tags      标签列表，JSON 数组字符串，1～5 个
     * @param isPublic  是否公开：0-私密，1-公开，默认 1
     */
    void createVideo(MultipartFile videoFile, MultipartFile coverFile,
                     String title, String tags, Integer isPublic);

    /**
     * 获取当前用户的视频列表（分页，不含违规视频，按创建时间倒序）
     *
     * @param page     页码，从 1 开始，默认 1
     * @param pageSize 每页数量，默认 20，最大 50
     * @return 用户视频列表响应
     */
    UserVideoListResponse getUserVideos(Integer page, Integer pageSize);
}
