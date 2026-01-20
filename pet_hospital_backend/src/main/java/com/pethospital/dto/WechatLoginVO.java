package com.pethospital.dto;

import lombok.Data;

/**
 * 微信登录响应VO
 */
@Data
public class WechatLoginVO {
    /**
     * JWT token
     */
    private String token;
    
    /**
     * 用户信息
     */
    private UserInfo user;
    
    /**
     * 微信openid（用于绑定手机号）
     */
    private String openid;
    
    /**
     * 用户信息内部类
     */
    @Data
    public static class UserInfo {
        private Long id;
        private String name;
        private String phone;
        private Integer level;
    }
}
