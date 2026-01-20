package com.pethospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("vaccine_stock_logs")
public class VaccineStockLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long inventoryId;
    private String changeType; // IN, OUT, ADJUST
    private Integer quantity;
    private Long relatedRecordId;
    private Long operatorId;
    private String reason;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    // 关联字段
    @TableField(exist = false)
    private VaccineInventory inventory;
    
    @TableField(exist = false)
    private Employee operator;
}
