package com.mango.funflow.controller;

import com.mango.funflow.common.Result;
import com.mango.funflow.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 视频相关接口
 */
@RestController
@RequestMapping("/videos")
public class VideoController {

    @Autowired
    private VideoService videoService;

    /**
     * 作品创作接口
     * 上传视频文件及相关信息，创建视频作品
     *
     * @param videoFile 视频文件（必填，mp4/mov/avi，≤300MB）
     * @param coverFile 封面图片（可选，jpg/jpeg/png，≤5MB）
     * @param title     视频标题（可选，0～300 字符）
     * @param tags      标签列表，JSON 数组字符串，1～5 个（必填）
     * @param isPublic  是否公开：0-私密，1-公开（可选，默认 1）
     * @return 统一响应
     */
    @PostMapping
    public Result<Void> createVideo(
            @RequestParam("video") MultipartFile videoFile,
            @RequestParam(value = "cover", required = false) MultipartFile coverFile,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam("tags") String tags,
            @RequestParam(value = "isPublic", required = false) Integer isPublic) {
        videoService.createVideo(videoFile, coverFile, title, tags, isPublic);
        return Result.success();
    }
}
