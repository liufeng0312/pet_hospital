package com.pethospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("inventory_logs")
public class InventoryLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long drugId;
    
    /**
     * 类型 1:入库, 2:出库, 3:盘点调整
     */
    private Integer type;
    
    /**
     * 变动数量 (出库为负数)
     */
    private Integer quantity;
    
    /**
     * 批号
     */
    private String batchNumber;
    
    /**
     * 有效期
     */
    private LocalDate expiryDate;
    
    /**
     * 操作人ID
     */
    private Long operatorId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    // 非数据库字段，用于关联查询
    @TableField(exist = false)
    private Drug drug;
    
    @TableField(exist = false)
    private Employee operator;
}
