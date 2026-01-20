package com.pethospital.interceptor;

import com.pethospital.common.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JWT认证拦截器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationInterceptor implements HandlerInterceptor {
    
    private final JwtUtil jwtUtil;
    
    @Value("${jwt.header}")
    private String header;
    
    @Value("${jwt.prefix}")
    private String prefix;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行OPTIONS请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 从请求头获取Token
        String authHeader = request.getHeader(header);
        
        if (authHeader == null || !authHeader.startsWith(prefix)) {
            log.warn("请求未携带Token或Token格式错误");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"未授权，请先登录\",\"data\":null}");
            return false;
        }
        
        // 提取Token（去掉"Bearer "前缀）
        String token = authHeader.substring(prefix.length()).trim();
        
        // 兼容后台管理系统的mock token
        if ("mock-jwt-token".equals(token)) {
            return true;
        }
        
        // 验证Token
        if (!jwtUtil.validateToken(token)) {
            log.warn("Token无效或已过期");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"Token无效或已过期\",\"data\":null}");
            return false;
        }
        
        // 从Token中提取customerId
        Long customerId = jwtUtil.getCustomerIdFromToken(token);
        if (customerId == null) {
            log.warn("无法从Token中提取customerId");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"Token解析失败\",\"data\":null}");
            return false;
        }
        
        // 将customerId设置到request attribute中
        request.setAttribute("customerId", customerId);
        log.debug("Token验证成功，customerId: {}", customerId);
        
        return true;
    }
}
