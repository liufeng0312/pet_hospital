package com.pethospital.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("reminders")
public class Reminder {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long customerId;
    
    private Long petId;
    
    /**
     * 类型: VACCINE, BIRTHDAY, FOLLOW_UP
     */
    private String type;
    
    private LocalDate dueDate;
    
    /**
     * 状态: 0:未发送, 1:已发送, 2:发送失败
     */
    private Integer status;
    
    private LocalDateTime sentTime;
    
    // 关联字段
    @TableField(exist = false)
    private Customer customer;
    
    @TableField(exist = false)
    private Pet pet;
}
