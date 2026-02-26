package com.mango.funflow.service;

import com.mango.funflow.dto.response.UserProfileResponse;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 获取当前登录用户的个人资料
     *
     * @return 用户个人资料
     */
    UserProfileResponse getProfile();
}
