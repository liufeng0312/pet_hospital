package com.pethospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("services")
public class ServiceItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    /**
     * 类别: VACCINE, SURGERY, EXAM, LAB
     */
    private String category;
    
    private BigDecimal price;
    
    private String description;
    
    private Integer isActive;
}
