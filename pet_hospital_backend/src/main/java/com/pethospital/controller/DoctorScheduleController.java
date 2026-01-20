package com.pethospital.controller;

import com.pethospital.common.Result;
import com.pethospital.entity.DoctorSchedule;
import com.pethospital.service.DoctorScheduleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Api(tags = "医生排班管理")
@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class DoctorScheduleController {
    
    private final DoctorScheduleService scheduleService;
    
    @ApiOperation("获取排班列表（按日期范围）")
    @GetMapping
    public Result<List<DoctorSchedule>> getSchedules(
            @RequestParam(required = false) Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<DoctorSchedule> schedules;
        if (doctorId != null) {
            schedules = scheduleService.getSchedulesByDoctorAndDateRange(doctorId, startDate, endDate);
        } else {
            schedules = scheduleService.getSchedulesByDateRange(startDate, endDate);
        }
        return Result.success(schedules);
    }
    
    @ApiOperation("获取排班详情")
    @GetMapping("/{id}")
    public Result<DoctorSchedule> getById(@PathVariable Long id) {
        return Result.success(scheduleService.getById(id));
    }
    
    @ApiOperation("创建排班")
    @PostMapping
    public Result<Boolean> create(@RequestBody DoctorSchedule schedule) {
        return Result.success(scheduleService.saveSchedule(schedule));
    }
    
    @ApiOperation("批量创建排班")
    @PostMapping("/batch")
    public Result<Boolean> batchCreate(@RequestBody List<DoctorSchedule> schedules) {
        return Result.success(scheduleService.batchSaveSchedules(schedules));
    }
    
    @ApiOperation("更新排班")
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody DoctorSchedule schedule) {
        schedule.setId(id);
        return Result.success(scheduleService.saveSchedule(schedule));
    }
    
    @ApiOperation("删除排班")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(scheduleService.deleteSchedule(id));
    }
}
