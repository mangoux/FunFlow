package com.mango.funflow.service;

import com.mango.funflow.dto.request.UpdateProfileRequest;
import com.mango.funflow.dto.response.AvatarUploadResponse;
import com.mango.funflow.dto.response.UserProfileResponse;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * 上传用户头像
     *
     * @param file 头像文件
     * @return 头像 URL
     */
    AvatarUploadResponse uploadAvatar(MultipartFile file);

    /**
     * 更新当前登录用户的个人资料
     *
     * @param request 更新请求
     * @return 更新后的用户资料
     */
    UserProfileResponse updateProfile(UpdateProfileRequest request);
}
