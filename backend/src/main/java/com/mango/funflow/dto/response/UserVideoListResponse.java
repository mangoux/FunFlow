package com.mango.funflow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用户视频列表响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVideoListResponse {

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 作品总数（不含违规视频）
     */
    private Long total;

    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 每页数量
     */
    private Integer pageSize;

    /**
     * 作品列表，按创建时间倒序排序
     */
    private List<VideoItemResponse> videos;
}
