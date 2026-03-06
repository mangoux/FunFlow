package com.mango.funflow.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 视频标签关联实体类
 * 对应数据库表: video_tags
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoTag {

    private Long videoTagId;
    private Long videoId;
    private Long tagId;
    private LocalDateTime createdAt;
}
