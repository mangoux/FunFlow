package com.mango.funflow.controller;

import com.mango.funflow.common.Result;
import com.mango.funflow.dto.request.UpdateProfileRequest;
import com.mango.funflow.dto.response.AvatarUploadResponse;
import com.mango.funflow.dto.response.UserProfileResponse;
import com.mango.funflow.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户信息管理相关接口
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取当前登录用户的个人资料
     *
     * @return 用户个人资料
     */
    @GetMapping("/profile")
    public Result<UserProfileResponse> getProfile() {
        UserProfileResponse profile = userService.getProfile();
        return Result.success(profile);
    }

    /**
     * 上传用户头像
     *
     * @param file 头像文件
     * @return 头像 URL
     */
    @PostMapping("/profile/avatar")
    public Result<AvatarUploadResponse> uploadAvatar(@RequestParam("file") MultipartFile file) {
        AvatarUploadResponse avatarUrl = userService.uploadAvatar(file);
        return Result.success(avatarUrl);
    }

    /**
     * 更新当前登录用户的个人资料
     *
     * @param request 更新请求
     * @return 更新后的用户资料
     */
    @PutMapping("/profile")
    public Result<UserProfileResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        UserProfileResponse profile = userService.updateProfile(request);
        return Result.success("更新成功！", profile);
    }
}
