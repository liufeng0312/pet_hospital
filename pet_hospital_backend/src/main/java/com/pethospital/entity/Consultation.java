package com.pethospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("consultations")
public class Consultation {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long customerId;
    
    private Long petId;
    
    private String title;
    
    private String content;
    
    private String images; // JSON数组字符串
    
    private String status; // PENDING, ANSWERED, CLOSED
    
    private String replyContent;
    
    private Long replyDoctorId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime replyTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    
    private LocalDateTime deletedAt;
    
    // 关联对象 (非数据库字段)
    @TableField(exist = false)
    private Pet pet;
    
    @TableField(exist = false)
    private Employee doctor;
}
