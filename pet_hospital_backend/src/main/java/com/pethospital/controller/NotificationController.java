package com.pethospital.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pethospital.common.Result;
import com.pethospital.entity.Notification;
import com.pethospital.service.NotificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = "消息通知管理")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @ApiOperation("获取我的通知列表")
    @GetMapping("/my-notifications")
    public Result<Page<Notification>> getMyNotifications(
            @RequestAttribute("customerId") Long customerId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(notificationService.getMyNotifications(customerId, page, size));
    }

    @ApiOperation("标记已读")
    @PutMapping("/{id}/read")
    public Result<Boolean> markAsRead(@PathVariable Long id) {
        return Result.success(notificationService.markAsRead(id));
    }

    @ApiOperation("全部标记已读")
    @PutMapping("/read-all")
    public Result<Boolean> markAllAsRead(@RequestAttribute("customerId") Long customerId) {
        return Result.success(notificationService.markAllAsRead(customerId));
    }

    @ApiOperation("获取未读数量")
    @GetMapping("/unread-count")
    public Result<Integer> getUnreadCount(@RequestAttribute("customerId") Long customerId) {
        return Result.success(notificationService.getUnreadCount(customerId));
    }
}
