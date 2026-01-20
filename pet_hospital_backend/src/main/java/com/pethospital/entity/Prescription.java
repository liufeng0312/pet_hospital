package com.pethospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("prescriptions")
public class Prescription {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long medicalRecordId;
    
    /**
     * 状态 0:未支付, 1:已支付, 2:已发药
     */
    private Integer status;
    
    /**
     * 总金额
     */
    private BigDecimal totalAmount;
    
    @TableField(fill = FieldFill.INSERT)
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    // 非数据库字段，用于关联查询
    @TableField(exist = false)
    private MedicalRecord medicalRecord;
    
    @TableField(exist = false)
    private List<PrescriptionItem> items;
}
