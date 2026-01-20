package com.pethospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("bills")
public class Bill {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long customerId;
    
    /**
     * 关联类型: REGISTRATION, PRESCRIPTION
     */
    private String relatedType;
    
    private Long relatedId;
    
    /**
     * 应收金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;
    
    /**
     * 实收金额
     */
    private BigDecimal finalAmount;
    
    /**
     * 支付方式: CASH, WECHAT, ALIPAY
     */
    private String paymentMethod;
    
    /**
     * 状态: 0:未付, 1:已付, 2:退款
     */
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    private LocalDateTime paidAt;
    
    // 关联字段
    @TableField(exist = false)
    private Customer customer;
}
