package com.pethospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("lab_tests")
public class LabTest {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long medicalRecordId;
    
    private Long serviceId;
    
    /**
     * 检验结果
     */
    private String result;
    
    /**
     * 报告图片链接
     */
    private String reportUrl;
    
    /**
     * 检验时间
     */
    /**
     * 检验时间
     */
    @TableField(fill = FieldFill.INSERT)
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime testTime;
    
    // 逻辑字段，非数据库必须，但通过 result 判断
    @TableField(exist = false)
    private Integer status; // 0: Pending, 1: Completed
    
    // 关联字段
    @TableField(exist = false)
    private MedicalRecord medicalRecord;
    
    @TableField(exist = false)
    private ServiceItem serviceItem;
    
    public Integer getStatus() {
        return (result != null && !result.isEmpty()) ? 1 : 0;
    }
}
