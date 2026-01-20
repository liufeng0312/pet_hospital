package com.pethospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("vaccine_records")
public class VaccineRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long petId;
    private String vaccineName;
    private String vaccineType;
    private String batchNumber;
    private LocalDate vaccinationDate;
    private LocalDate nextDueDate;
    private Long doctorId;
    private Integer status; // 0-未支付，1-待接种，2-已完成
    private String notes;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic(value = "null", delval = "now()")
    private LocalDateTime deletedAt;
    
    // 关联字段
    @TableField(exist = false)
    private Pet pet;
    
    @TableField(exist = false)
    private Employee doctor;
    
    @TableField(exist = false)
    private java.math.BigDecimal price; // 疫苗价格，从库存表关联获取
}
