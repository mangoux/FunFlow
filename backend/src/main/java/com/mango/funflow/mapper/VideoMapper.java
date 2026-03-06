package com.mango.funflow.mapper;

import com.mango.funflow.entity.Video;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface VideoMapper {

    /**
     * 插入新视频记录
     *
     * @param video 视频实体
     * @return 影响的行数
     */
    @Insert("INSERT INTO `videos` (user_id, video_url, cover_url, title, is_public, status) " +
            "VALUES (#{userId}, #{videoUrl}, #{coverUrl}, #{title}, #{isPublic}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "videoId", keyColumn = "video_id")
    int insert(Video video);
}
