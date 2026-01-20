package com.pethospital.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("doctor_schedules")
public class DoctorSchedule {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long doctorId;
    private LocalDate workDate;
    private String shiftType; // MORNING, AFTERNOON, FULL_DAY, OFF
    private java.time.LocalTime startTime;
    private java.time.LocalTime endTime;
    private Integer isAvailable; // 1=可预约, 0=不可预约
    private Integer maxAppointments; // 最大预约数
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
