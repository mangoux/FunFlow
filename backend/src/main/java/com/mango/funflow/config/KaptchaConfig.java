package com.mango.funflow.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Kaptcha 图形验证码配置类
 */
@Configuration
public class KaptchaConfig {

    @Bean
    public DefaultKaptcha defaultKaptcha() {
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        
        // 图片边框（无边框更简洁）
        properties.setProperty("kaptcha.border", "no");
        
        // 图片尺寸（增大尺寸，更清晰）
        properties.setProperty("kaptcha.image.width", "130");
        properties.setProperty("kaptcha.image.height", "48");
        
        // 字体配置（使用更清晰的字体）
        properties.setProperty("kaptcha.textproducer.font.names", "Arial,Helvetica,Courier");
        properties.setProperty("kaptcha.textproducer.font.size", "36");
        properties.setProperty("kaptcha.textproducer.font.color", "70,130,180"); // 钢蓝色，更柔和
        
        // 文字配置
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        properties.setProperty("kaptcha.textproducer.char.string", "23456789ABCDEFGHJKLMNPQRSTUVWXYZ"); // 去除易混淆字符 0,1,I,O
        properties.setProperty("kaptcha.textproducer.char.space", "4"); // 增加字符间距
        
        // 背景配置（使用浅色渐变背景，更现代）
        properties.setProperty("kaptcha.background.clear.from", "245,245,250"); // 浅灰蓝
        properties.setProperty("kaptcha.background.clear.to", "255,255,255"); // 白色
        
        // 干扰线配置（使用更柔和的干扰线）
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.DefaultNoise");
        properties.setProperty("kaptcha.noise.color", "200,200,200"); // 浅灰色干扰线
        
        // Session key
        properties.setProperty("kaptcha.session.key", "code");
        
        Config config = new Config(properties);
        kaptcha.setConfig(config);
        return kaptcha;
    }
}
