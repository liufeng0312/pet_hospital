package com.pethospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("hospitalizations")
public class Hospitalization {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long petId;
    
    /**
     * 床位号 (如 "101")
     */
    private String bedNumber;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    /**
     * 状态: ACTIVE, DISCHARGED
     */
    private String status;
    
    // 关联字段
    @TableField(exist = false)
    private Pet pet;
}
