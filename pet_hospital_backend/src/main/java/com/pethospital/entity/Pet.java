package com.pethospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("pets")
public class Pet {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long customerId;
    private String name;
    private String species;  // 物种: 猫, 狗, 兔子等
    private String breed;    // 品种
    private Integer gender;  // 1:公, 2:母, 0:未知
    private LocalDate birthDate;
    private BigDecimal weight;
    private String photoUrl;
    private String notes;    // 特殊注意事项、过敏史
    
    @TableField(fill = FieldFill.INSERT)
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    @TableLogic(value = "null", delval = "now()")
    private LocalDateTime deletedAt;
    
    // 非数据库字段，用于关联客户信息
    @TableField(exist = false)
    private Customer customer;
}
