package com.mango.funflow.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 标签实体类
 * 对应数据库表: tags
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tag {

    /**
     * 标签唯一 ID
     */
    private Long tagId;

    /**
     * 标签名称，唯一
     */
    private String tagName;

    /**
     * 使用次数（冗余字段，用于热门标签统计）
     */
    private Integer useCount;

    private LocalDateTime createdAt;
}
