package com.pethospital.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pethospital.common.Result;
import com.pethospital.entity.Registration;
import com.pethospital.service.RegistrationService;
import com.pethospital.task.RegistrationExpireTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Api(tags = "挂号管理")
@RestController
@RequestMapping("/api/registrations")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;
    private final RegistrationExpireTask registrationExpireTask;

    @ApiOperation("分页查询挂号列表")
    @GetMapping
    public Result<Page<Registration>> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(registrationService.listRegistrations(status, date, page, size));
    }

    @ApiOperation("获取挂号详情")
    @GetMapping("/{id}")
    public Result<Registration> getById(@PathVariable Long id) {
        return Result.success(registrationService.getById(id));
    }

    @ApiOperation("新建挂号")
    @PostMapping
    public Result<Registration> create(@RequestBody Registration registration) {
        return Result.success(registrationService.createRegistration(registration));
    }

    @ApiOperation("更新挂号")
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody Registration registration) {
        registration.setId(id);
        return Result.success(registrationService.updateById(registration));
    }

    @ApiOperation("取消挂号")
    @DeleteMapping("/{id}")
    public Result<Boolean> cancel(@PathVariable Long id) {
        return Result.success(registrationService.cancelRegistration(id));
    }

    @ApiOperation("获取排队看板数据")
    @GetMapping("/queue")
    public Result<List<Registration>> getQueueBoard() {
        return Result.success(registrationService.getQueueBoard());
    }

    @ApiOperation("叫号")
    @PutMapping("/{id}/call")
    public Result<Boolean> callNext(@PathVariable Long id) {
        return Result.success(registrationService.callNext(id));
    }

    @ApiOperation("完成就诊")
    @PutMapping("/{id}/complete")
    public Result<Boolean> complete(@PathVariable Long id) {
        return Result.success(registrationService.complete(id));
    }

    @ApiOperation("获取我的预约列表（小程序）")
    @GetMapping("/my-appointments")
    public Result<List<Registration>> getMyAppointments(@RequestAttribute("customerId") Long customerId) {
        return Result.success(registrationService.getByCustomerId(customerId));
    }

    @ApiOperation("获取可预约时间段（小程序）")
    @GetMapping("/available-slots")
    public Result<List<com.pethospital.dto.TimeSlotDTO>> getAvailableSlots(
            @RequestParam Long doctorId,
            @RequestParam String date) {
        return Result.success(registrationService.getAvailableSlots(doctorId, date));
    }

    @ApiOperation("取消预约（小程序）")
    @PutMapping("/{id}/cancel")
    public Result<Boolean> cancelAppointment(@PathVariable Long id) {
        return Result.success(registrationService.cancelRegistration(id));
    }

    @ApiOperation("手动触发过期任务（测试用）")
    @PostMapping("/expire-old")
    public Result<String> manualExpireOldRegistrations() {
        int count = registrationExpireTask.manualExpire();
        return Result.success("成功处理 " + count + " 条过期挂号记录");
    }
}
