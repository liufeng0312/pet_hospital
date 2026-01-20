package com.pethospital.dto;

import lombok.Data;

/**
 * 微信登录请求DTO
 */
@Data
public class WechatLoginDTO {
    /**
     * 微信登录code
     */
    private String code;
}
