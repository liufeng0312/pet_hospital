package com.pethospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("drugs")
public class Drug {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 药品名称
     */
    private String name;
    
    /**
     * 药品编码
     */
    private String code;
    
    /**
     * 规格
     */
    private String specification;
    
    /**
     * 厂家
     */
    private String manufacturer;
    
    /**
     * 销售价格
     */
    private BigDecimal price;
    
    /**
     * 当前库存
     */
    private Integer stockQuantity;
    
    /**
     * 预警阈值
     */
    private Integer warningThreshold;
    
    /**
     * 单位: 盒, 瓶, 支
     */
    private String unit;
    
    /**
     * 是否启用
     */
    private Integer isActive;
}
