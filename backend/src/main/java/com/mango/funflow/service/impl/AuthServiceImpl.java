package com.mango.funflow.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.mango.funflow.common.Code;
import com.mango.funflow.common.RedisConstant;
import com.mango.funflow.dto.request.SendEmailCodeRequest;
import com.mango.funflow.dto.response.CaptchaResponse;
import com.mango.funflow.exception.BusinessException;
import com.mango.funflow.service.AuthService;
import com.mango.funflow.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private DefaultKaptcha defaultKaptcha;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private EmailService emailService;

    @Override
    public CaptchaResponse generateCaptcha() {
        try {
            // 生成验证码文本
            String captchaText = defaultKaptcha.createText();
            // 生成验证码图片
            BufferedImage captchaImage = defaultKaptcha.createImage(captchaText);
            // 将图片转换为 Base64
            String imageData = convertImageToBase64(captchaImage);
            // 生成唯一 captchaId
            String captchaId = UUID.randomUUID().toString();

            // 存储到 Redis，设置5分钟过期
            String redisKey = RedisConstant.getCaptchaKey(captchaId);
            stringRedisTemplate.opsForValue().set(
                    redisKey,
                    captchaText,
                    RedisConstant.CAPTCHA_EXPIRE_SECONDS,
                    TimeUnit.SECONDS
            );

            log.info("生成图形验证码成功, captchaId: {}, captchaText: {}", captchaId, captchaText);
            return new CaptchaResponse(captchaId, imageData);

        } catch (Exception e) {
            log.error("Failed to generate captcha: {}", e.getMessage(), e);
            throw new BusinessException(Code.SYSTEM_ERROR, "获取图形验证码失败，请稍后重试");
        }
    }

    /**
     * 将 BufferedImage 转换为 Base64 编码的字符串
     *
     * @param image 图片对象
     * @return Base64 编码的图片字符串
     */
    private String convertImageToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        String base64 = Base64.getEncoder().encodeToString(imageBytes);
        return "data:image/png;base64," + base64;
    }

    @Override
    public void sendEmailCode(SendEmailCodeRequest request) {
        String email = request.getEmail().toLowerCase();
        String captchaId = request.getCaptchaId();
        String captchaText = request.getCaptchaText();

        // 校验图形验证码
        validateCaptcha(captchaId, captchaText);

        // 生成 6 位数字，保存在 Redis 中，设置过期时间为 5 分钟
        String emailCode = RandomUtil.randomNumbers(6);
        String redisKey = RedisConstant.getEmailCodeKey(email);
        stringRedisTemplate.opsForValue().set(
                redisKey,
                emailCode,
                RedisConstant.EMAIL_CODE_EXPIRE_SECONDS,
                TimeUnit.SECONDS
        );

        // 向指定邮箱发送邮件验证码
        emailService.sendEmailCode(email, emailCode);
        log.info("邮箱验证码发送成功，邮箱: {}, 验证码: {}", email, emailCode);
    }

    /**
     * 校验图形验证码
     *
     * @param captchaId   验证码ID
     * @param captchaText 用户输入的验证码文本
     */
    private void validateCaptcha(String captchaId, String captchaText) {
        String redisKey = RedisConstant.getCaptchaKey(captchaId);
        String correctCaptchaText = stringRedisTemplate.opsForValue().get(redisKey);

        // 无论对错，立即删除 Redis 中的该 captchaId（一次性使用）
        stringRedisTemplate.delete(redisKey);

        // 验证码不存在或已过期
        if (correctCaptchaText == null) {
            throw new BusinessException(Code.CAPTCHA_VALIDATION_ERROR, "验证码已过期，请重新获取");
        }
        // 忽略大小写比较
        if (!correctCaptchaText.equalsIgnoreCase(captchaText)) {
            throw new BusinessException(Code.CAPTCHA_VALIDATION_ERROR, "验证码错误，请重新输入");
        }
    }
}
