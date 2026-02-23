package com.mango.funflow.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.mango.funflow.common.Code;
import com.mango.funflow.common.RedisConstant;
import com.mango.funflow.dto.request.RegisterRequest;
import com.mango.funflow.dto.request.SendEmailCodeRequest;
import com.mango.funflow.dto.response.CaptchaResponse;
import com.mango.funflow.entity.User;
import com.mango.funflow.exception.BusinessException;
import com.mango.funflow.mapper.UserMapper;
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
    @Autowired
    private UserMapper userMapper;

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

    @Override
    public void register(RegisterRequest request) {
        String email = request.getEmail().toLowerCase();
        String emailCode = request.getEmailCode();
        String password = request.getPassword();

        // 验证邮箱验证码
        validateEmailCode(email, emailCode);

        // 校验邮箱是否已被注册
        validateEmailNotRegistered(email);

        // 创建用户，完成注册
        createUser(email, password);
    }

    /**
     * 校验邮箱验证码
     *
     * @param email     邮箱地址（小写）
     * @param emailCode 用户输入的邮箱验证码
     */
    private void validateEmailCode(String email, String emailCode) {
        String redisKey = RedisConstant.getEmailCodeKey(email);
        String correctEmailCode = stringRedisTemplate.opsForValue().get(redisKey);

        // 验证码不存在或已过期
        if (correctEmailCode == null) {
            throw new BusinessException(Code.EMAIL_CODE_VALIDATION_ERROR, "邮箱验证码已过期，请重新获取");
        }
        // 验证码错误
        if (!correctEmailCode.equals(emailCode)) {
            throw new BusinessException(Code.EMAIL_CODE_VALIDATION_ERROR, "邮箱验证码错误，请重新输入");
        }

        // 验证成功后删除验证码（一次性使用）
        stringRedisTemplate.delete(redisKey);
    }

    /**
     * 校验邮箱是否已被注册
     *
     * @param email 邮箱地址（小写）
     */
    private void validateEmailNotRegistered(String email) {
        int count = userMapper.countByEmail(email);
        if (count > 0) {
            throw new BusinessException(Code.EMAIL_REGISTERED, "该邮箱已被注册");
        }
    }

    /**
     * 创建用户并保存到数据库
     *
     * @param email    邮箱地址
     * @param password 明文密码
     */
    private void createUser(String email, String password) {
        User user = User.builder()
                .email(email)
                .passwordHash(BCrypt.hashpw(password))  // 密码加密
                .avatarUrl("")
                .nickname(extractNameFromEmail(email))  // 昵称从邮箱中提取
                .username(email)    // 用户名默认使用邮箱
                .bio("")
                .status(User.Status.NORMAL.getCode())
                .build();

        try {
            userMapper.insert(user);
        } catch (Exception e) {
            throw new BusinessException(Code.EMAIL_REGISTERED, "该邮箱已被注册");
        }

        log.info("用户注册成功，邮箱: {}", email);
    }

    /**
     * 从邮箱中提取昵称
     * 例如：user@example.com -> user
     *
     * @param email 邮箱地址
     * @return 昵称
     */
    private String extractNameFromEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex > 0) {
            return email.substring(0, atIndex);
        }
        throw new BusinessException(Code.VALIDATION_ERROR, "邮箱格式不正确");
    }
}
