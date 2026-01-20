package com.pethospital.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Slf4j
@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    /**
     * 生成Token
     * @param customerId 客户ID
     * @param openid 微信openid
     * @return JWT Token
     */
    public String generateToken(Long customerId, String openid) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("customerId", customerId);
        claims.put("openid", openid);
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(customerId))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * 从Token中获取客户ID
     * @param token JWT Token
     * @return 客户ID
     */
    public Long getCustomerIdFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.get("customerId", Long.class);
        } catch (Exception e) {
            log.error("获取customerId失败", e);
            return null;
        }
    }
    
    /**
     * 从Token中获取openid
     * @param token JWT Token
     * @return openid
     */
    public String getOpenidFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.get("openid", String.class);
        } catch (Exception e) {
            log.error("获取openid失败", e);
            return null;
        }
    }
    
    /**
     * 验证Token是否有效
     * @param token JWT Token
     * @return true=有效, false=无效
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            return expiration.after(new Date());
        } catch (Exception e) {
            log.error("Token验证失败", e);
            return false;
        }
    }
    
    /**
     * 刷新Token
     * @param token 旧Token
     * @return 新Token
     */
    public String refreshToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Long customerId = claims.get("customerId", Long.class);
            String openid = claims.get("openid", String.class);
            return generateToken(customerId, openid);
        } catch (Exception e) {
            log.error("Token刷新失败", e);
            return null;
        }
    }
    
    /**
     * 从Token中解析Claims
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * 获取密钥
     */
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
