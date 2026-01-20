package com.pethospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 手术记录实体
 */
@Data
@TableName("surgery_records")
public class SurgeryRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long serviceItemId;
    private Long medicalRecordId;
    private Long petId;
    private Long surgeonId;
    private String assistantIds;
    
    private String surgeryType;
    private String surgeryName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime surgeryDate;
    private Integer duration;
    
    private String preDiagnosis;
    private String surgeryProcess;
    private String postDiagnosis;
    private String anesthesiaType;
    private String complications;
    private String notes;
    
    private Integer status; // 0-待支付，1-已支付待手术，2-手术中，3-已完成
    private BigDecimal amount;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @TableLogic(value = "NULL", delval = "now()")
    private LocalDateTime deletedAt;
    
    // 关联对象（非数据库字段）
    @TableField(exist = false)
    private ServiceItem serviceItem;
    
    @TableField(exist = false)
    private Pet pet;
    
    @TableField(exist = false)
    private Employee surgeon;
    
    @TableField(exist = false)
    private MedicalRecord medicalRecord;
}
