package com.pethospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("notifications")
public class Notification {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long customerId;
    
    private String type; // APPOINTMENT, REMINDER, CONSULTATION, SYSTEM
    
    private String title;
    
    private String content;
    
    private String relatedType;
    
    private Long relatedId;
    
    private Integer isRead; // 1=已读, 0=未读
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime readAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
