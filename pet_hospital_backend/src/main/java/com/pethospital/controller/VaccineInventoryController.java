package com.pethospital.controller;

import com.pethospital.common.Result;
import com.pethospital.entity.VaccineInventory;
import com.pethospital.entity.VaccineStockLog;
import com.pethospital.service.VaccineInventoryService;
import com.pethospital.service.VaccineStockLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "疫苗库存管理")
@RestController
@RequestMapping("/api/vaccine-inventory")
@RequiredArgsConstructor
public class VaccineInventoryController {

    private final VaccineInventoryService inventoryService;
    private final VaccineStockLogService stockLogService;

    @ApiOperation("查询库存列表")
    @GetMapping
    public Result<Object> list(@RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(required = false) String vaccineName,
                                @RequestParam(required = false) String vaccineType,
                                @RequestParam(required = false) String batchNumber) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<VaccineInventory> pageParam = 
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
        
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<VaccineInventory> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        
        if (vaccineName != null && !vaccineName.isEmpty()) {
            wrapper.like(VaccineInventory::getVaccineName, vaccineName);
        }
        if (vaccineType != null && !vaccineType.isEmpty()) {
            wrapper.eq(VaccineInventory::getVaccineType, vaccineType);
        }
        if (batchNumber != null && !batchNumber.isEmpty()) {
            wrapper.like(VaccineInventory::getBatchNumber, batchNumber);
        }
        
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<VaccineInventory> result = 
            inventoryService.page(pageParam, wrapper);
        return Result.success(result);
    }

    @ApiOperation("获取预警信息")
    @GetMapping("/alerts")
    public Result<Map<String, Object>> getAlerts() {
        List<VaccineInventory> lowStock = inventoryService.getLowStockItems();
        List<VaccineInventory> expiring = inventoryService.getExpiringItems(30);
        
        Map<String, Object> alerts = new HashMap<>();
        alerts.put("lowStock", lowStock);
        alerts.put("expiring", expiring);
        alerts.put("lowStockCount", lowStock.size());
        alerts.put("expiringCount", expiring.size());
        
        return Result.success(alerts);
    }

    @ApiOperation("获取单条记录")
    @GetMapping("/{id}")
    public Result<VaccineInventory> getById(@PathVariable Long id) {
        return Result.success(inventoryService.getById(id));
    }

    @ApiOperation("新增库存")
    @PostMapping
    public Result<VaccineInventory> create(@RequestBody VaccineInventory inventory) {
        inventoryService.save(inventory);
        return Result.success(inventory);
    }

    @ApiOperation("更新库存")
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody VaccineInventory inventory) {
        inventory.setId(id);
        return Result.success(inventoryService.updateById(inventory));
    }

    @ApiOperation("删除库存")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(inventoryService.removeById(id));
    }

    @ApiOperation("调整库存")
    @PostMapping("/{id}/adjust")
    public Result<Void> adjustStock(@PathVariable Long id, @RequestBody AdjustStockRequest request) {
        inventoryService.adjustStock(id, request.getQuantity(), request.getReason(), request.getOperatorId());
        return Result.success(null);
    }

    @ApiOperation("查询库存变动日志")
    @GetMapping("/{id}/logs")
    public Result<List<VaccineStockLog>> getLogs(@PathVariable Long id) {
        return Result.success(stockLogService.listByInventoryId(id));
    }

    @Data
    static class AdjustStockRequest {
        private Integer quantity;
        private String reason;
        private Long operatorId;
    }
}
