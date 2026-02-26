package com.mango.funflow.interceptor;

import com.mango.funflow.util.UserContext;
import com.mango.funflow.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 认证拦截器
 * 校验请求是否携带有效的 JWT 令牌
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 0. 放行 OPTIONS 预检请求（CORS）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 校验 Authorization 格式
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            return sendUnauthorized(response);
        }

        // 验证 Token 是否有效
        String token = authHeader.substring(BEARER_PREFIX.length());
        if (!JWTUtil.isValid(token)) {
            return sendUnauthorized(response);
        }

        // 将 userId 存储到 ThreadLocal
        Long userId = JWTUtil.getUserId(token);
        if (userId == null) {
            return sendUnauthorized(response);
        }
        UserContext.setUserId(userId);

        return true;
    }

    private boolean sendUnauthorized(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求完成后清理 ThreadLocal，防止内存泄漏
        UserContext.clear();
    }
}
