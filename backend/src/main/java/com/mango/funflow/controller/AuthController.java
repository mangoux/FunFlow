package com.mango.funflow.controller;

import com.mango.funflow.common.Result;
import com.mango.funflow.dto.response.CaptchaResponse;
import com.mango.funflow.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 认证控制器
 * 登录、注册相关接口
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 获取图形验证码
     *
     * @return 验证码信息
     */
    @GetMapping("/captcha")
    public Result<CaptchaResponse> getCaptcha() {
        CaptchaResponse captcha = authService.generateCaptcha();
        return Result.success(captcha);
    }
}
