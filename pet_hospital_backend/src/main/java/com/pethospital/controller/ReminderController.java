package com.pethospital.controller;

import com.pethospital.common.Result;
import com.pethospital.entity.Reminder;
import com.pethospital.service.ReminderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "智能提醒")
@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;

    @ApiOperation("列表查询")
    @GetMapping
    public Result<List<Reminder>> list() {
        return Result.success(reminderService.listAll());
    }

    @ApiOperation("触发生成")
    @PostMapping("/generate")
    public Result<Void> generate() {
        reminderService.generateReminders();
        return Result.success(null);
    }

    @ApiOperation("发送提醒")
    @PostMapping("/send/{id}")
    public Result<Void> send(@PathVariable Long id) {
        reminderService.send(id);
        return Result.success(null);
    }

    @ApiOperation("获取我的提醒列表（小程序）")
    @GetMapping("/my-reminders")
    public Result<List<Reminder>> getMyReminders(@RequestAttribute("customerId") Long customerId) {
        return Result.success(reminderService.getMyReminders(customerId));
    }

    @ApiOperation("标记提醒已读（小程序）")
    @PutMapping("/{id}/read")
    public Result<Boolean> markAsRead(@PathVariable Long id, @RequestAttribute("customerId") Long customerId) {
        Reminder reminder = reminderService.getById(id);
        if (reminder == null || !reminder.getCustomerId().equals(customerId)) {
            return Result.error("提醒不存在或无权访问");
        }
        reminder.setStatus(1);
        return Result.success(reminderService.updateById(reminder));
    }
}
