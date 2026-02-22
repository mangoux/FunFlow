package com.mango.funflow.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 发送邮箱验证码请求
 */
@Data
public class SendEmailCodeRequest {
    
    /**
     * 邮箱地址
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    /**
     * 图形验证码ID
     */
    @NotBlank(message = "验证码ID不能为空")
    private String captchaId;
    
    /**
     * 图形验证码文本
     */
    @NotBlank(message = "图形验证码不能为空")
    private String captchaText;
}
