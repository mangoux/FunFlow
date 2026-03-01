package com.mango.funflow.service.impl;

import com.mango.funflow.common.Code;
import com.mango.funflow.common.OssConstant;
import com.mango.funflow.config.OssConfig;
import com.mango.funflow.dto.request.UpdateProfileRequest;
import com.mango.funflow.dto.response.AvatarUploadResponse;
import com.mango.funflow.dto.response.UserProfileResponse;
import com.mango.funflow.entity.User;
import com.mango.funflow.exception.BusinessException;
import com.mango.funflow.mapper.UserMapper;
import com.mango.funflow.service.OssService;
import com.mango.funflow.service.UserService;
import com.mango.funflow.util.MultipartFileUtil;
import com.mango.funflow.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户服务实现类
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OssService ossService;

    @Autowired
    private OssConfig ossConfig;

    @Override
    public UserProfileResponse getProfile() {
        // 1. 从 ThreadLocal 获取当前用户 ID
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(Code.UNAUTHORIZED, "登录状态认证失败");
        }

        // 2. 查询用户信息
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException(Code.NOT_FOUND, "用户不存在");
        }

        // 3. 构建响应对象
        return UserProfileResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .followingCount(user.getFollowingCount())
                .followerCount(user.getFollowerCount())
                .totalLikesReceived(user.getTotalLikesReceived())
                .build();
    }

    @Override
    public AvatarUploadResponse uploadAvatar(MultipartFile file) {
        // 1. 获取当前用户 ID
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(Code.UNAUTHORIZED, "登录认证失败");
        }

        // 2. 校验文件
        String extension;
        try {
            extension = MultipartFileUtil.validateImageAndGetExtension(file, 5 * 1024 * 1024);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(Code.FILE_VALIDATION_ERROR, e.getMessage());
        }

        // 3. 上传到 OSS
        String fileName = OssConstant.getAvatarFileName(userId, extension);
        String avatarUrl = ossService.uploadFile(file, fileName);

        return new AvatarUploadResponse(avatarUrl);
    }

    @Override
    public UserProfileResponse updateProfile(UpdateProfileRequest request) {
        // 获取当前用户 ID
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(Code.UNAUTHORIZED, "登录状态认证失败");
        }

        // 检查是否至少有一个字段需要更新
        if (request.getUsername() == null && request.getNickname() == null &&
                request.getAvatarUrl() == null && request.getBio() == null) {
            throw new BusinessException(Code.UPDATE_PROFILE_PARAM_ERROR, "至少需要更新一个字段");
        }

        // 如果要更新 avatarUrl，校验 URL 格式和路径
        if (request.getAvatarUrl() != null && !request.getAvatarUrl().isEmpty()) {
            // 校验路径格式：{oss_prefix}/avatar/{userId}/xxx
            String stdUrlPrefix = ossConfig.getUrlPrefix() + OssConstant.getAvatarPathPrefix(userId);
            if (!request.getAvatarUrl().startsWith(stdUrlPrefix)) {
                throw new BusinessException(Code.UPDATE_PROFILE_PARAM_ERROR, "头像 URL 格式不正确");
            }
        }

        // 构建更新对象
        User user = User.builder()
                .userId(userId)
                .username(request.getUsername())
                .nickname(request.getNickname())
                .avatarUrl(request.getAvatarUrl())
                .bio(request.getBio())
                .build();

        // 更新数据库
        try {
            userMapper.updateProfile(user);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(Code.UPDATE_PROFILE_PARAM_ERROR, "该用户名已被使用，请重新输入");
        }

        return getProfile();
    }
}
