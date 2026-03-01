package com.mango.funflow.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户资料更新请求 DTO
 */
@Data
public class UpdateProfileRequest {

    /**
     * 账号（唯一标识，不区分大小写）
     * 2-20 个字符，只能包含字母、数字、下划线
     */
    @Size(min = 4, max = 20, message = "账号长度必须在 4-20 个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "账号只能包含字母、数字、下划线")
    private String username;

    /**
     * 昵称
     * 1-20 个字符
     */
    @Size(min = 1, max = 20, message = "昵称长度必须在 1-20 个字符之间")
    private String nickname;

    /**
     * 头像 URL
     */
    private String avatarUrl;

    /**
     * 个性签名/个人简介
     * 最多 200 个字符
     */
    @Size(max = 200, message = "个性签名最多 200 个字符")
    private String bio;
}
