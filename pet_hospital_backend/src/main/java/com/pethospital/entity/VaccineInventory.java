package com.pethospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("vaccine_inventory")
public class VaccineInventory {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String vaccineName;
    private String vaccineType;
    private String manufacturer;
    private String batchNumber;
    private Integer quantity;
    private Integer minQuantity;
    private BigDecimal unitPrice;
    private LocalDate productionDate;
    private LocalDate expiryDate;
    private String storageLocation;
    private String notes;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic(value = "null", delval = "now()")
    private LocalDateTime deletedAt;
}
