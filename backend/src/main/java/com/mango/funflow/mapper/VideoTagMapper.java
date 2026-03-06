package com.mango.funflow.mapper;

import com.mango.funflow.entity.VideoTag;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

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
}
