package com.mango.funflow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 视频列表项响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoItemResponse {

    /**
     * 视频唯一 ID
     */
    private Long videoId;

    /**
     * 视频标题
     */
    private String title;

    /**
     * 封面图片 URL
     */
    private String coverUrl;

    /**
     * 视频文件 URL
     */
    private String videoUrl;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 观看量
     */
    private Integer viewCount;

    /**
     * 点赞量
     */
    private Integer likeCount;

    /**
     * 视频状态：0-审核中、1-已发布、2-已下架、3-违规
     */
    private Integer status;

    /**
     * 是否公开：0-私密，1-公开
     */
    private Integer isPublic;

    /**
     * 创建时间，格式：yyyy-MM-dd HH:mm:ss
     */
    private String createTime;
}
