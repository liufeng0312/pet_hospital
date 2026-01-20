package com.pethospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("prescription_items")
public class PrescriptionItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long prescriptionId;
    private Long drugId;
    
    /**
     * 数量
     */
    private Integer quantity;
    
    /**
     * 用法用量
     */
    private String dosage;
    
    /**
     * 单价 (快照)
     */
    private BigDecimal price;
    
    // 非数据库字段，用于关联查询
    @TableField(exist = false)
    private Drug drug;
}
