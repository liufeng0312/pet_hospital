package com.pethospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("customers")
public class Customer {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    private String phone;
    private String address;
    private Integer level; // 1:VIP, 2:普通, 3:黑名单
    private String notes;
    
    @TableField(fill = FieldFill.INSERT)
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    @TableLogic(value = "null", delval = "now()")
    private LocalDateTime deletedAt;
}
