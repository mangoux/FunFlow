package com.mango.funflow.mapper;

import com.mango.funflow.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    /**
     * 根据邮箱查询用户是否存在
     *
     * @param email 邮箱地址（小写）
     * @return 用户数量
     */
    @Select("SELECT COUNT(1) FROM `users` WHERE email = #{email}")
    int countByEmail(@Param("email") String email);

    /**
     * 插入新用户
     *
     * @param user 用户实体
     * @return 影响的行数
     */
    @Insert("INSERT INTO `users` (email, password_hash, avatar_url, nickname, username, bio, status) " +
            "VALUES (#{email}, #{passwordHash}, #{avatarUrl}, #{nickname}, #{username}, #{bio}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "userId", keyColumn = "user_id")
    int insert(User user);

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱地址（小写）
     * @return 用户实体
     */
    @Select("SELECT user_id, password_hash, status FROM `users` WHERE email = #{email}")
    User findByEmail(@Param("email") String email);

    /**
     * 更新最后登录时间
     *
     * @param userId 用户ID
     */
    @Update("UPDATE `users` SET last_login_at = NOW() WHERE user_id = #{userId}")
    void updateLastLoginTime(@Param("userId") Long userId);
}
