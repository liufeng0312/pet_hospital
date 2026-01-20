package com.pethospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("medical_records")
public class MedicalRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long registrationId;
    private Long petId;
    private Long doctorId;
    
    /**
     * 主诉/症状
     */
    private String symptoms;
    
    /**
     * 诊断结果
     */
    private String diagnosis;
    
    /**
     * 治疗方案
     */
    private String treatmentPlan;
    
    /**
     * 医嘱
     */
    private String doctorAdvice;
    
    /**
     * 就诊时间
     */
    @TableField(fill = FieldFill.INSERT)
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime visitTime;
    
    @TableLogic(value = "null", delval = "now()")
    private LocalDateTime deletedAt;
    
    // 非数据库字段，用于关联查询
    @TableField(exist = false)
    private Registration registration;
    
    @TableField(exist = false)
    private Pet pet;
    
    @TableField(exist = false)
    private Employee doctor;
    
    @TableField(exist = false)
    private Customer customer;
}
