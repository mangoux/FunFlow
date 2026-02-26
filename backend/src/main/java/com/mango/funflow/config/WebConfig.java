package com.mango.funflow.config;

import com.mango.funflow.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    /**
     * 设置 Controller 统一前缀
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer c) {
        c.addPathPrefix("/api", HandlerTypePredicate.forAnnotation(RestController.class));
    }

    /**
     * 配置CORS跨域
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 对所有路径生效
                .allowedOriginPatterns("*")  // 允许所有域名（开发环境），生产环境：https://your-trusted-domain.com
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 允许的HTTP方法
                .allowedHeaders("*")  // 允许所有请求头
                .allowCredentials(true)  // 允许携带凭证（cookie等）
                .maxAge(3600);  // 预检请求的缓存时间（秒）
    }

    /**
     * 注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/**");   // 排除不需要认证的接口
    }
}
