package com.mango.funflow.mapper;

import com.mango.funflow.entity.VideoTag;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface VideoTagMapper {

    /**
     * 插入视频标签关联记录
     *
     * @param videoTag 视频标签关联实体
     * @return 影响的行数
     */
    @Insert("INSERT INTO `video_tags` (video_id, tag_id) VALUES (#{videoId}, #{tagId})")
    @Options(useGeneratedKeys = true, keyProperty = "videoTagId", keyColumn = "video_tag_id")
    int insert(VideoTag videoTag);

    /**
     * 根据视频 ID 查询关联的标签名称列表
     *
     * @param videoId 视频 ID
     * @return 标签名称列表
     */
    @Select("SELECT t.tag_name FROM `tags` t " +
            "INNER JOIN `video_tags` vt ON t.tag_id = vt.tag_id " +
            "WHERE vt.video_id = #{videoId}")
    List<String> findTagNamesByVideoId(@Param("videoId") Long videoId);
}
