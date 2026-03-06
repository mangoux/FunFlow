package com.mango.funflow.mapper;

import com.mango.funflow.entity.Tag;
import org.apache.ibatis.annotations.*;

@Mapper
public interface TagMapper {

    /**
     * 根据标签名称查询标签
     *
     * @param tagName 标签名称
     * @return 标签实体，不存在时返回 null
     */
    @Select("SELECT tag_id, tag_name, use_count FROM `tags` WHERE tag_name = #{tagName}")
    Tag findByName(@Param("tagName") String tagName);

    /**
     * 插入新标签
     *
     * @param tag 标签实体
     * @return 影响的行数
     */
    @Insert("INSERT INTO `tags` (tag_name) VALUES (#{tagName})")
    @Options(useGeneratedKeys = true, keyProperty = "tagId", keyColumn = "tag_id")
    int insert(Tag tag);

    /**
     * 增加标签的使用次数
     *
     * @param tagId 标签ID
     */
    @Update("UPDATE `tags` SET use_count = use_count + 1 WHERE tag_id = #{tagId}")
    void incrementUseCount(@Param("tagId") Long tagId);
}
