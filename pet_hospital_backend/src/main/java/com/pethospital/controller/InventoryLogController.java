package com.pethospital.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pethospital.common.Result;
import com.pethospital.entity.InventoryLog;
import com.pethospital.service.InventoryLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = "库存流水管理")
@RestController
@RequestMapping("/api/inventory-logs")
@RequiredArgsConstructor
public class InventoryLogController {

    private final InventoryLogService inventoryLogService;

    @ApiOperation("分页查询库存流水")
    @GetMapping
    public Result<Page<InventoryLog>> list(
            @RequestParam(required = false) Long drugId,
            @RequestParam(required = false) Integer type,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(inventoryLogService.listLogs(drugId, type, page, size));
    }

    @ApiOperation("入库操作")
    @PostMapping("/inbound")
    public Result<Void> inbound(@RequestBody InventoryLog log) {
        inventoryLogService.recordInbound(log);
        return Result.success(null);
    }

    @ApiOperation("出库操作")
    @PostMapping("/outbound")
    public Result<Void> outbound(@RequestBody InventoryLog log) {
        inventoryLogService.recordOutbound(log);
        return Result.success(null);
    }

    @ApiOperation("盘点调整")
    @PostMapping("/adjustment")
    public Result<Void> adjustment(@RequestBody InventoryLog log) {
        inventoryLogService.recordAdjustment(log);
        return Result.success(null);
    }
}
