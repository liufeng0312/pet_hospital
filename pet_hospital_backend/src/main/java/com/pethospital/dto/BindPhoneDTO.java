package com.pethospital.dto;

import lombok.Data;

/**
 * 绑定手机号请求DTO
 */
@Data
public class BindPhoneDTO {
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 验证码（可选）
     */
    private String code;
    
    /**
     * 微信openid（临时方案）
     */
    private String openid;
}
