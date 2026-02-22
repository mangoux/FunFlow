package com.mango.funflow.service;

public interface EmailService {
    
    /**
     * 发送邮箱验证码
     *
     * @param to      收件人邮箱
     * @param code    验证码
     */
    void sendEmailCode(String to, String code);
}
