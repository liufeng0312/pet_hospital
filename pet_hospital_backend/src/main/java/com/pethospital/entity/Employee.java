package com.pethospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("employees")
public class Employee {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    private String username;
    
    /**
     * 密码哈希 (查询时不返回)
     */
    @TableField(select = false)
    private String passwordHash;
    
    /**
     * 角色: DOCTOR, RECEPTIONIST, ADMIN
     */
    private String role;
    
    private String phone;
    
    /**
     * 个人介绍
     */
    private String bio;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 临时密码字段（不存储到数据库，仅用于接收前端传递的密码）
     */
    @TableField(exist = false)
    private String password;
    
    /**
     * 状态 1:在职, 0:离职
     */
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
