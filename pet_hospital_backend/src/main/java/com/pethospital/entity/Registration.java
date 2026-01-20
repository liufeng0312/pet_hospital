package com.pethospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("registrations")
public class Registration {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long customerId;
    private Long petId;
    private Long doctorId;
    
    /**
     * 状态: PENDING(待确认), WAITING(候诊中), IN_PROGRESS(就诊中), COMPLETED(已完成), CANCELLED(已取消)
     */
    private String status;
    
    /**
     * 类型: APPOINTMENT(预约), ONSITE(现场挂号)
     */
    private String type;
    
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appointmentTime;
    
    /**
     * 排队号 (当天有效)
     */
    private Integer queueNumber;
    
    @TableField(fill = FieldFill.INSERT)
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // 非数据库字段，用于关联查询
    @TableField(exist = false)
    private Customer customer;
    
    @TableField(exist = false)
    private Pet pet;
    
    @TableField(exist = false)
    private Employee doctor;
}
