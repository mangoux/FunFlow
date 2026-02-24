package com.mango.funflow.service;

import com.mango.funflow.dto.request.LoginRequest;
import com.mango.funflow.dto.request.RegisterRequest;
import com.mango.funflow.dto.request.SendEmailCodeRequest;
import com.mango.funflow.dto.response.CaptchaResponse;
import com.mango.funflow.dto.response.LoginResponse;

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

    /**
     * 用户注册
     *
     * @param request 注册请求
     */
    void register(RegisterRequest request);

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应（包含 accessToken）
     */
    LoginResponse login(LoginRequest request);
}
