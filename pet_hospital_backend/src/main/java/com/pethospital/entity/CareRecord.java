package com.pethospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("care_records")
public class CareRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long hospitalizationId;
    
    private String content;
    
    private Long operatorId;
    
    @TableField(fill = FieldFill.INSERT)
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime recordTime;
    
    // 关联字段
    @TableField(exist = false)
    private Employee operator;

    @TableField(exist = false)
    private Hospitalization hospitalization;
}
