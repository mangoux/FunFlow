package com.mango.funflow.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 视频实体类
 * 对应数据库表: videos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Video {

    /**
     * 视频唯一 ID
     */
    private Long videoId;

    /**
     * 上传用户的 ID
     */
    private Long userId;

    /**
     * 视频文件 URL
     */
    private String videoUrl;

    /**
     * 视频封面图片 URL，默认为空
     */
    private String coverUrl;

    /**
     * 视频标题（文案），最多 300 字符
     */
    private String title;

    /**
     * 播放量，默认 0
     */
    private Integer viewCount;

    /**
     * 点赞量，默认 0
     */
    private Integer likeCount;

    /**
     * 收藏数，默认 0
     */
    private Integer collectCount;

    /**
     * 评论数，默认 0
     */
    private Integer commentCount;

    /**
     * 是否公开：0-私密，1-公开
     */
    private Integer isPublic;

    /**
     * 视频状态：0-审核中，1-已发布，2-已下架，3-违规
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    public enum Status {
        REVIEWING(0, "审核中"),
        PUBLISHED(1, "已发布"),
        OFFLINE(2, "已下架"),
        VIOLATION(3, "违规");

        private final Integer code;
        private final String desc;

        Status(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public Integer getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }
}
