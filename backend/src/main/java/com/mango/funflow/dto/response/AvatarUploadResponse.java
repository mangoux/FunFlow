package com.mango.funflow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户头像上传响应 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvatarUploadResponse {

    /**
     * 头像 URL
     */
    private String avatarUrl;
}
