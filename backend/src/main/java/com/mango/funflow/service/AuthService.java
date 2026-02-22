package com.mango.funflow.service;

import com.mango.funflow.dto.request.SendEmailCodeRequest;
import com.mango.funflow.dto.response.CaptchaResponse;

public interface AuthService {

    /**
     * 生成图形验证码
     *
     * @return 验证码信息（包含 captchaId 和 imageData）
     */
    CaptchaResponse generateCaptcha();

    /**
     * 发送邮箱验证码
     *
     * @param request 发送邮箱验证码请求
     */
    void sendEmailCode(SendEmailCodeRequest request);
}
