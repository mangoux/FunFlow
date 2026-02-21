package com.mango.funflow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图形验证码响应实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaResponse {

    /**
     * 验证码 ID
     */
    private String captchaId;

    /**
     * 验证码图片（Base64编码）
     */
    private String imageData;
}
