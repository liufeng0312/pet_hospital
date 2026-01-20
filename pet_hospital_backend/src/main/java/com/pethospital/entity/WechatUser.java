package com.pethospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 微信用户实体类
 */
@Data
@TableName("wechat_users")
public class WechatUser {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 微信openid
     */
    private String openid;
    
    /**
     * 微信unionid（可选）
     */
    private String unionid;
    
    /**
     * 关联客户ID
     */
    private Long customerId;
    
    /**
     * 微信昵称
     */
    private String nickname;
    
    /**
     * 微信头像URL
     */
    private String avatarUrl;
    
    /**
     * 绑定手机号
     */
    private String phone;
    
    /**
     * 微信session_key
     */
    private String sessionKey;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
