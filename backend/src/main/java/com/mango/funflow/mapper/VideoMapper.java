package com.mango.funflow.mapper;

import com.mango.funflow.entity.Video;
import org.apache.ibatis.annotations.*;

import java.util.List;

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

    /**
     * 统计用户的视频总数（不含违规视频）
     *
     * @param userId 用户 ID
     * @return 视频总数
     */
    @Select("SELECT COUNT(1) FROM `videos` WHERE user_id = #{userId} AND status != 3")
    long countByUserIdExcludeViolation(@Param("userId") Long userId);

    /**
     * 分页查询用户视频列表（不含违规视频，按创建时间倒序）
     *
     * @param userId   用户 ID
     * @param offset   偏移量
     * @param pageSize 每页数量
     * @return 视频列表
     */
    @Select("SELECT video_id, user_id, video_url, cover_url, title, view_count, like_count, " +
            "is_public, status, created_at " +
            "FROM `videos` " +
            "WHERE user_id = #{userId} AND status != 3 " +
            "ORDER BY created_at DESC " +
            "LIMIT #{pageSize} OFFSET #{offset}")
    List<Video> findByUserIdExcludeViolation(@Param("userId") Long userId,
                                              @Param("offset") int offset,
                                              @Param("pageSize") int pageSize);
}
