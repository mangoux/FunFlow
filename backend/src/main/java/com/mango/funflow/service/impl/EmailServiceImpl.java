package com.mango.funflow.service.impl;

import com.mango.funflow.common.Code;
import com.mango.funflow.exception.BusinessException;
import com.mango.funflow.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Override
    public void sendEmailCode(String to, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("FunFlow 验证码");
            message.setText(buildEmailContent(code));
            mailSender.send(message);
        } catch (Exception e) {
            throw new BusinessException(Code.EMAIL_SEND_ERROR, "邮件发送失败，请稍后重试");
        }
    }
    
    /**
     * 构建邮件内容
     *
     * @param code 验证码
     * @return 邮件内容
     */
    private String buildEmailContent(String code) {
        return String.format(
                "您好！\n\n" +
                "您的 FunFlow 验证码是：%s\n\n" +
                "验证码有效期为 5 分钟，请勿泄露给他人。\n\n" +
                "如非本人操作，请忽略此邮件。\n\n" +
                "FunFlow 团队",
                code
        );
    }
}
