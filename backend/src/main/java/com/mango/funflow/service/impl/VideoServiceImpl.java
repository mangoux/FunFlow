package com.mango.funflow.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mango.funflow.common.Code;
import com.mango.funflow.common.OssConstant;
import com.mango.funflow.entity.Tag;
import com.mango.funflow.entity.Video;
import com.mango.funflow.entity.VideoTag;
import com.mango.funflow.exception.BusinessException;
import com.mango.funflow.mapper.TagMapper;
import com.mango.funflow.mapper.VideoMapper;
import com.mango.funflow.mapper.VideoTagMapper;
import com.mango.funflow.service.OssService;
import com.mango.funflow.service.VideoService;
import com.mango.funflow.util.MultipartFileUtil;
import com.mango.funflow.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 视频服务实现类
 */
@Service
@Slf4j
public class VideoServiceImpl implements VideoService {

    private static final long VIDEO_MAX_SIZE = 300L * 1024 * 1024;
    private static final long COVER_MAX_SIZE = 5L * 1024 * 1024;
    private static final int TAGS_MIN = 1;
    private static final int TAGS_MAX = 5;

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private VideoTagMapper videoTagMapper;

    @Autowired
    private OssService ossService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createVideo(MultipartFile videoFile, MultipartFile coverFile,
                            String title, String tags, Integer isPublic) {
        // 1. 获取当前用户 ID
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(Code.UNAUTHORIZED, "登录状态认证失败");
        }

        // 2. 校验视频文件
        String videoExtension;
        try {
            videoExtension = MultipartFileUtil.validateVideoAndGetExtension(videoFile, VIDEO_MAX_SIZE);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(Code.FILE_VALIDATION_ERROR, e.getMessage());
        }

        // 3. 校验封面文件（可选）
        String coverExtension = null;
        if (coverFile != null && !coverFile.isEmpty()) {
            try {
                coverExtension = MultipartFileUtil.validateImageAndGetExtension(coverFile, COVER_MAX_SIZE);
            } catch (IllegalArgumentException e) {
                throw new BusinessException(Code.FILE_VALIDATION_ERROR, e.getMessage());
            }
        }

        // 4. 解析并校验标签列表
        List<String> tagList = parseTags(tags);
        if (tagList.size() < TAGS_MIN || tagList.size() > TAGS_MAX) {
            throw new BusinessException(Code.VALIDATION_ERROR,
                    String.format("标签数量须在 %d～%d 个之间", TAGS_MIN, TAGS_MAX));
        }

        // 5. 上传视频文件到 OSS
        String videoFileName = OssConstant.getVideoFileName(userId, videoExtension);
        String videoUrl;
        try {
            videoUrl = ossService.uploadFile(videoFile, videoFileName);
        } catch (Exception e) {
            log.error("视频文件上传失败 [userId: {}]: {}", userId, e.getMessage(), e);
            throw new BusinessException(Code.SYSTEM_ERROR, "文件上传失败");
        }

        // 6. 上传封面文件到 OSS（可选）
        String coverUrl = "";
        if (coverExtension != null) {
            String coverFileName = OssConstant.getCoverFileName(userId, coverExtension);
            try {
                coverUrl = ossService.uploadFile(coverFile, coverFileName);
            } catch (Exception e) {
                log.error("封面文件上传失败 [userId: {}]: {}", userId, e.getMessage(), e);
                // 避免孤儿文件需要清理删除已上传的视频文件
                throw new BusinessException(Code.SYSTEM_ERROR, "文件上传失败");
            }
        }

        // 7. 插入视频记录
        Video video = Video.builder()
                .userId(userId)
                .videoUrl(videoUrl)
                .coverUrl(coverUrl)
                .title(title != null ? title : "")
                .isPublic(isPublic != null ? isPublic : 1)
                .status(Video.Status.REVIEWING.getCode())
                .build();
        videoMapper.insert(video);
        Long videoId = video.getVideoId();
        log.info("视频记录创建成功 [videoId: {}, userId: {}]", videoId, userId);

        // 8. 处理标签：查找或创建标签，并建立关联
        for (String tagName : tagList) {
            Tag tag = tagMapper.findByName(tagName);
            if (tag == null) {
                tag = Tag.builder().tagName(tagName).build();
                try {
                    tagMapper.insert(tag);
                    log.info("新标签创建成功 [tagId: {}, tagName: {}]", tag.getTagId(), tagName);
                } catch (DuplicateKeyException e) {
                    // 竞争插入新标签
                    tag = tagMapper.findByName(tagName);
                }
            }
            tagMapper.incrementUseCount(tag.getTagId());

            VideoTag videoTag = VideoTag.builder()
                    .videoId(videoId)
                    .tagId(tag.getTagId())
                    .build();
            videoTagMapper.insert(videoTag);
        }
    }

    /**
     * 解析 JSON 数组格式的标签字符串
     */
    private List<String> parseTags(String tags) {
        if (tags == null || tags.isBlank()) {
            throw new BusinessException(Code.VALIDATION_ERROR, "标签不能为空");
        }
        try {
            List<String> tagList = objectMapper.readValue(tags, new TypeReference<>() {});
            if (tagList == null || tagList.isEmpty()) {
                throw new BusinessException(Code.VALIDATION_ERROR, "标签不能为空");
            }
            // 过滤空字符串标签
            tagList = tagList.stream()
                    .map(String::trim)
                    .filter(t -> !t.isEmpty())
                    .toList();
            return tagList;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(Code.VALIDATION_ERROR, "标签格式不正确，请传入 JSON 数组字符串");
        }
    }
}
